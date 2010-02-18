package org.jdbcdslog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

public class CallableStatementLoggingProxy extends PreparedStatementLoggingProxy implements InvocationHandler {

	Map namedParameters = new TreeMap();
	
	public CallableStatementLoggingProxy(CallableStatement ps, String sql) {
		super(ps, sql);
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object r = null;
		try {
			r = method.invoke(target, args);
			if(setMethods.contains(method.getName()) && args[0] instanceof Integer)
				parameters.put(args[0], args[1]);
			if(setMethods.contains(method.getName()) && args[0] instanceof String)
				namedParameters.put(args[0], args[1]);
			if("clearParameters".equals(method.getName()))
				parameters = new TreeMap();
			if(logger.isInfoEnabled() && executeMethods.contains(method.getName())) {
				StringBuffer s = new StringBuffer(method.getDeclaringClass().getName())
					.append(".").append(method.getName());
				s.append(" ");
				s.append(sql);
				s.append(" parameters: ");
				s.append(parametersToString());
				s.append(" named parameters: ");
				s.append(namedParameters);
				logger.info(s.toString());
			}
			if(r instanceof ResultSet)
				r = ResultSetLoggingProxy.wrapByResultSetProxy((ResultSet)r);
		} catch(Throwable t) {
			LogUtils.handleException(t, method, logger);
		}
		return r;	
	}

}
