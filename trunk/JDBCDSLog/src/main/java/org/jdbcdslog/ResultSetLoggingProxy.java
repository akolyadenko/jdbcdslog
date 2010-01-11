package org.jdbcdslog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultSetLoggingProxy  implements InvocationHandler {

	static Logger logger = LoggerFactory.getLogger(ResultSetLoggingProxy.class);
	
	Object target = null;
	
	public ResultSetLoggingProxy(Object target) {
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object r = method.invoke(target, args);
		if(target instanceof ResultSet && method.getName() == "next" && ((Boolean)r).booleanValue()) {
			ResultSet rs = (ResultSet)target;
			ResultSetMetaData md = rs.getMetaData();
			String s = "Result set next row : {";
			if(md.getColumnCount() > 0)
				s += rs.getObject(1);
			for(int i = 2; i <= md.getColumnCount(); i++)
				s += ", " + rs.getObject(i);
			s += "}";
			logger.info(s);
		} 
		return r;
	}

	static Object wrapByResultSetProxy(Object r) {
		return Proxy.newProxyInstance(r.getClass().getClassLoader(), new Class[]{ResultSet.class}, 
				new ResultSetLoggingProxy(r));
	}

}
