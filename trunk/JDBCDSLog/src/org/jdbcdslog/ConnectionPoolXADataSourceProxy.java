package org.jdbcdslog;

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

public class ConnectionPoolXADataSourceProxy implements DataSource, XADataSource, ConnectionPoolDataSource
	, Serializable {

	Object targetDS = null;

	public ConnectionPoolXADataSourceProxy() throws JDBCDSLogException {
		try {
			String className = System
					.getProperty("org.jdbcdslog.ConnectionPoolXADataSourceProxy.targetDS");
			if (className == null)
				throw new JDBCDSLogException(
						"Can't find org.jdbcdslog.ConnectionPoolXADataSourceProxy.targetDS property.");
			Class cl = Class.forName(className);
			if (cl == null)
				throw new JDBCDSLogException("Can't load class of targetDS.");
			Object targetObj = cl.newInstance();
			targetDS = targetObj;
		} catch (Exception e) {
			throw new JDBCDSLogException(e);
		}
	}

	public Connection getConnection() throws SQLException {
		if(targetDS instanceof DataSource)
			return wrap(((DataSource)targetDS).getConnection());
		else 
			throw new SQLException("targetDS doesn't implement DataSource interface.");
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		if(targetDS instanceof DataSource)
			return wrap(((DataSource)targetDS).getConnection(username, password));
		else
			throw new SQLException("targetDS doesn't implement DataSource interface.");
	}

	public PrintWriter getLogWriter() throws SQLException {
		if(targetDS instanceof DataSource)
			return ((DataSource)targetDS).getLogWriter();
		if(targetDS instanceof XADataSource)
			return ((XADataSource)targetDS).getLogWriter();
		if(targetDS instanceof ConnectionPoolDataSource)
			return ((ConnectionPoolDataSource)targetDS).getLogWriter();
		throw new SQLException("targetDS doesn't have getLogWriter() method");
	}

	public int getLoginTimeout() throws SQLException {
		if(targetDS instanceof DataSource)
			return ((DataSource)targetDS).getLoginTimeout();
		if(targetDS instanceof XADataSource)
			return ((XADataSource)targetDS).getLoginTimeout();
		if(targetDS instanceof ConnectionPoolDataSource)
			return ((ConnectionPoolDataSource)targetDS).getLoginTimeout();
		throw new SQLException("targetDS doesn't have getLogTimeout() method");
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		if(targetDS instanceof DataSource)
			((DataSource)targetDS).setLogWriter(out);
		if(targetDS instanceof XADataSource)
			((XADataSource)targetDS).setLogWriter(out);
		if(targetDS instanceof ConnectionPoolDataSource)
			((ConnectionPoolDataSource)targetDS).setLogWriter(out);
		throw new SQLException("targetDS doesn't have setLogWriter() method");
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		if(targetDS instanceof DataSource)
			((DataSource)targetDS).setLoginTimeout(seconds);
		if(targetDS instanceof XADataSource)
			((XADataSource)targetDS).setLoginTimeout(seconds);
		if(targetDS instanceof ConnectionPoolDataSource)
			((ConnectionPoolDataSource)targetDS).setLoginTimeout(seconds);
		throw new SQLException("targetDS doesn't have setLogWriter() method");
	}

	Connection wrap(Connection con) {
		return (Connection)Proxy.newProxyInstance(con.getClass().getClassLoader()
				, new Class[]{Connection.class}, new GenericLoggingProxy(con));
	}
	
	PooledConnection wrap(PooledConnection con) {
		return (PooledConnection)Proxy.newProxyInstance(con.getClass().getClassLoader()
				, new Class[]{PooledConnection.class}, new GenericLoggingProxy(con));
	}
	
	XAConnection wrap(XAConnection con) {
		return (XAConnection)Proxy.newProxyInstance(con.getClass().getClassLoader()
				, new Class[]{XAConnection.class}, new GenericLoggingProxy(con));
	}

	public XAConnection getXAConnection() throws SQLException {
		if(targetDS instanceof XADataSource)
			return wrap(((XADataSource)targetDS).getXAConnection());
		else
			throw new SQLException("targetDS doesn't implement XADataSource interface.");
	}

	public XAConnection getXAConnection(String user, String password)
			throws SQLException {
		if(targetDS instanceof XADataSource)
			return wrap(((XADataSource)targetDS).getXAConnection(user, password));
		else
			throw new SQLException("targetDS doesn't implement XADataSource interface.");
	}

	public PooledConnection getPooledConnection() throws SQLException {
		if(targetDS instanceof ConnectionPoolDataSource)
			return wrap(((ConnectionPoolDataSource)targetDS).getPooledConnection());
		else
			throw new SQLException("targetDS doesn't implement ConnectionPoolDataSource interface.");
	}

	public PooledConnection getPooledConnection(String user, String password)
			throws SQLException {
		if(targetDS instanceof ConnectionPoolDataSource)
			return wrap(((ConnectionPoolDataSource)targetDS).getPooledConnection(user, password));
		else
			throw new SQLException("targetDS doesn't implement ConnectionPoolDataSource interface.");
	}
	
	public void setURL(String url) {
		try {
		Method m = targetDS.getClass().getMethod("setURL", new Class[]{String.class});
		if(m != null)
			m.invoke(targetDS, new Object[]{url});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
