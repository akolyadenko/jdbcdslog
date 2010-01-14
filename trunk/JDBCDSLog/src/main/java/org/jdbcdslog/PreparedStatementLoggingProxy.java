package org.jdbcdslog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreparedStatementLoggingProxy implements InvocationHandler {

	static Logger logger = LoggerFactory.getLogger(PreparedStatementLoggingProxy.class);
	
	TreeMap parameters = new TreeMap();
	
	Object target = null;
	
	String sql = null;
	
	StringBuffer sb = new StringBuffer();
	
	StringBuffer sb2 = new StringBuffer();
	
	static List setMethods = Arrays.asList(new String[]{"setAsciiStream", "setBigDecimal", "setBinaryStream"
			, "setBoolean", "setByte", "setBytes", "setCharacterStream", "setDate", "setDouble", "setFloat"
			, "setInt", "setLong", "setObject", "setShort", "setString", "setTime", "setTimestamp", "setURL"});
	
	static List executeMethods = Arrays.asList(new String[]{"addBatch", "execute", "executeQuery", "executeUpdate"});
	
	public PreparedStatementLoggingProxy(PreparedStatement ps, String sql) {
		target = ps;
		this.sql = sql;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object r = null;
		try {
			r = method.invoke(target, args);
			if(setMethods.contains(method.getName()) && args[0] instanceof Integer)
				parameters.put(args[0], args[1]);
			if("clearParameters".equals(method.getName()))
				parameters = new TreeMap();
			if(logger.isInfoEnabled() && executeMethods.contains(method.getName())) {
				sb.setLength(0); 
				sb.append(method.getDeclaringClass().getName()); // 0.1
				sb.append(".");
				sb.append(method.getName()); 
				sb.append(" ");
				sb.append(sql);
				sb.append(" ");
				sb.append(parametersToString());
				logger.info(sb.toString());
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

	String parametersToString() {
		sb2.setLength(0);
		sb2.append("{");
		int maxParamNumber = 0;
		if(parameters.size() > 0)
			maxParamNumber = ((Integer)parameters.lastKey()).intValue();
		if(maxParamNumber > 0) {
			Integer key = new Integer(1);
			if(parameters.containsKey(key))
				sb2.append("(").append(parameters.get(key)).append(")");
			else
				sb2.append("(null)");
		}
		for(int i = 2; i <= maxParamNumber; i ++) {
			Integer key = new Integer(i);
			sb2.append(", ");
			if(parameters.containsKey(key))
				sb2.append("(").append(parameters.get(key)).append(")");
			else
				sb2.append("(null)");
		}
		sb2.append("}");
		return sb2.toString();
	}

}
