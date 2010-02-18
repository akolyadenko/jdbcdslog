package org.jdbcdslog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;

public class LogUtils {
	public static void handleException(Throwable e, Method m ,Logger l) 
		throws Throwable {
		String fullMethodName = m.getDeclaringClass().getName() + "." + m.getName();
		if(e instanceof InvocationTargetException) {
			InvocationTargetException t = (InvocationTargetException)e;
			if(l.isErrorEnabled()) 
				l.error(fullMethodName + " throws exception: " + t.getClass().getName() + ": "
					+ t.getTargetException().getMessage(), t.getTargetException());
			throw t.getTargetException();
		} else {
			if(l.isErrorEnabled())
				l.error(fullMethodName + " throws exception: " + e.getClass().getName() + ": "
						+ e.getMessage(), e);
			throw e;
		}
	}
}
