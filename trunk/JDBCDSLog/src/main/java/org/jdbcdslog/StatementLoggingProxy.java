package org.jdbcdslog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatementLoggingProxy implements InvocationHandler {

	static Logger logger = LoggerFactory.getLogger(StatementLoggingProxy.class);
	
	Object targetStatement = null;
	
	static List executeMethods = Arrays.asList(new String[]{"addBatch", "execute", "executeQuery", "executeUpdate"});
	
	public StatementLoggingProxy(Statement statement) {
		targetStatement = statement;
	}


	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object r = null;
		try {
			r = method.invoke(targetStatement, args);
			if(logger.isInfoEnabled() && executeMethods.contains(method.getName())) {
				String fullMethodName = method.getDeclaringClass().getName() + "." + method.getName();
				logger.info(fullMethodName + args[0]);
			}
			if(r instanceof ResultSet)
				r = ResultSetLoggingProxy.wrapByResultSetProxy((ResultSet)r);
		} catch(Throwable t) {
			String fullMethodName = method.getDeclaringClass().getName() + "." + method.getName();
			if(logger.isErrorEnabled())
				logger.error(fullMethodName + " throws exception: " + t.getClass().getName() + ": "
					+ t.getMessage(), t);
			throw t;
		}
		return r;
	}
	
	
}
