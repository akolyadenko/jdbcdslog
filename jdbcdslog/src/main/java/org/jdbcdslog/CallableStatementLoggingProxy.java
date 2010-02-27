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
			boolean toLog = logger.isInfoEnabled() && executeMethods.contains(method.getName());
			long t1 = 0;
			if(toLog)
				t1 = System.currentTimeMillis();
			r = method.invoke(target, args);
			if(setMethods.contains(method.getName()) && args[0] instanceof Integer)
				parameters.put(args[0], args[1]);
			if(setMethods.contains(method.getName()) && args[0] instanceof String)
				namedParameters.put(args[0], args[1]);
			if("clearParameters".equals(method.getName()))
				parameters = new TreeMap();
			if(toLog) {
				long t2 = System.currentTimeMillis();
				StringBuffer s = LogUtils.createLogEntry(method, args[0], parametersToString(), namedParameters.toString());
				logger.info(s.append(" ").append(t2 - t1).append(" ms.").toString());
			}
			if(r instanceof ResultSet)
				r = ResultSetLoggingProxy.wrapByResultSetProxy((ResultSet)r);
		} catch(Throwable t) {
			LogUtils.handleException(t, logger, 
					LogUtils.createLogEntry(method, args[0], parametersToString(), namedParameters.toString()));
		}
		return r;	
	}

}
