package org.jdbcdslog;

import java.lang.reflect.Proxy;
import java.sql.Connection;

import javax.sql.PooledConnection;
import javax.sql.XAConnection;

public class ConnectionLoggingProxy {

	public static Connection wrap(Connection con) {
		return (Connection)Proxy.newProxyInstance(con.getClass().getClassLoader()
				, new Class[]{Connection.class}, new GenericLoggingProxy(con));
	}

	public static PooledConnection wrap(PooledConnection con) {
		return (PooledConnection)Proxy.newProxyInstance(con.getClass().getClassLoader()
				, new Class[]{PooledConnection.class}, new GenericLoggingProxy(con));
	}

	public static XAConnection wrap(XAConnection con) {
		return (XAConnection)Proxy.newProxyInstance(con.getClass().getClassLoader()
				, new Class[]{XAConnection.class}, new GenericLoggingProxy(con));
	}

}
