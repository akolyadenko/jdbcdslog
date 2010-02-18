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
			if(logger.isInfoEnabled() && executeMethods.contains(method.getName())) {
				StringBuffer sb = new StringBuffer();
				sb.append(method.getDeclaringClass().getName())
					.append(".")
					.append(method.getName())
					.append(" ")
					.append(args[0]);
				logger.info(sb.toString());
			}
			r = method.invoke(targetStatement, args);
			if(r instanceof ResultSet)
				r = ResultSetLoggingProxy.wrapByResultSetProxy((ResultSet)r);
		} catch(Throwable t) {
			LogUtils.handleException(t, method, logger);
		}
		return r;
	}
	
	
}
