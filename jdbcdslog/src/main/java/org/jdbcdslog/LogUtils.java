package org.jdbcdslog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;

public class LogUtils {
	public static void handleException(Throwable e, Logger l, StringBuffer msg) 
		throws Throwable {
		if(e instanceof InvocationTargetException) {
			Throwable t = ((InvocationTargetException)e).getTargetException();
			if(l.isErrorEnabled()) 
				l.error(msg + " throws exception: " + t.getClass().getName() + ": "
					+ t.getMessage(), t);
			throw t;
		} else {
			if(l.isErrorEnabled())
				l.error(msg + " throws exception: " + e.getClass().getName() + ": "
						+ e.getMessage(), e);
			throw e;
		}
	}
	
	public static StringBuffer createLogEntry(Method method, Object sql, String parameters, String namedParameters) {
		StringBuffer s = new StringBuffer(method.getDeclaringClass().getName())
		.append(".").append(method.getName());
		s.append(" ");
		if(sql != null)
			s.append(sql);
		if(parameters != null) {
			s.append(" parameters: ");
			s.append(parameters);
		}
		if(namedParameters != null) {
			s.append(" named parameters: ");
			s.append(namedParameters);
		}
		return s;
	}

	public static String sqlValueToString(Object o) {
		if(o == null)
			return "null";
		if(o instanceof String)
			return "'" + o.toString() + "'";
		else
			return o.toString();
	}
}
