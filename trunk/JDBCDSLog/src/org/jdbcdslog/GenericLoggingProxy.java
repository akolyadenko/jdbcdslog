package org.jdbcdslog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.sql.PooledConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericLoggingProxy implements InvocationHandler  {

	static Logger logger = LoggerFactory.getLogger(GenericLoggingProxy.class);
	
	static List methodsBlackList = Arrays.asList(new String[]{"getAutoCommit", "getCatalog", "getTypeMap"
			, "clearWarnings", "setAutoCommit", "getFetchSize", "setFetchSize", "commit"});
	
	static String sql = null;
	
	Object target = null;
	
	public GenericLoggingProxy(Object target) {
		this.target = target;
	}
	
	public GenericLoggingProxy(Object target, String sql) {
		this.target = target;
		this.sql = sql;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String fullMethodName = method.getDeclaringClass().getName() + "." + method.getName() + "(target.toString=" 
			+ target.toString() + ")";
		try { 
			boolean writeLog = method.getDeclaringClass() != Object.class && !methodsBlackList.contains(method.getName());
			if (writeLog) {
				String s = "";
				s += "Invoking " + fullMethodName + " with parameters: ";
				if (args != null && args.length > 0) {
					s += toString(args[0]);
					for (int i = 1; i < args.length; i++)
						s += ", " + toString(args[i]);
				}
				if(sql != null)
					s += " for sql: " + sql;
				logger.debug(s);			
			}
			Object r = method.invoke(target, args);
			if(method.getName().equals("prepareCall") || method.getName().equals("prepareStatement"))
				r = wrap(r, (String)args[0]);
			else 
				r = wrap(r, null);
			return r;
		} catch(Throwable t) {
			logger.error(fullMethodName + " throws exception: " + t.getClass().getName() + ": "
					+ t.getMessage(), t);	
			throw t;
		}
	}

	private String toString(Object o) {
		if(o == null)
			return "null";
		else
			return o.toString();
	}

	private Object wrap(Object r, String sql) {
		if(r instanceof Connection) 
			return wrapByGenericProxy(r, Connection.class, sql);
		if(r instanceof CallableStatement)
			return wrapByGenericProxy(r, CallableStatement.class, sql);
		if(r instanceof PreparedStatement)
			return wrapByGenericProxy(r, PreparedStatement.class, sql);
		if(r instanceof Statement)
			return wrapByGenericProxy(r, Statement.class, sql);
		if(r instanceof ResultSet)
			return wrapByResultSetProxy(r);
		return r;
	}

	private Object wrapByResultSetProxy(Object r) {
		return Proxy.newProxyInstance(r.getClass().getClassLoader(), new Class[]{ResultSet.class}, 
				new ResultSetLoggingProxy(r));
	}

	private Object wrapByGenericProxy(Object r, Class interf, String sql) {
//		System.out.println("wrap " + r.getClass().getName() + " for " + interf.getName());
		if(!(r instanceof Statement))
			sql = null;
		return Proxy.newProxyInstance(r.getClass().getClassLoader(), new Class[]{interf}, 
				new GenericLoggingProxy(r, sql));
	}

}
