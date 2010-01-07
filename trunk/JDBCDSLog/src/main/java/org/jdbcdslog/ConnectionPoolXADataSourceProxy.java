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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionPoolXADataSourceProxy implements DataSource, XADataSource, ConnectionPoolDataSource
	, Serializable {

	static Logger logger = LoggerFactory.getLogger(ConnectionPoolXADataSourceProxy.class);
	
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
		logger.debug("getConnection()");
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
	
	void invokeTargetSetMethod(String m, Object p) {
		try {
			Method me = targetDS.getClass().getMethod(m,
					new Class[] { String.class });
			if (me != null)
				me.invoke(targetDS, new Object[] { p });
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void setURL(String p) {
		invokeTargetSetMethod("setURL", p);
	}
	
	public void setDatabaseName(String p) {
		invokeTargetSetMethod("setDatabaseName", p);
	}
	
	public void setDescription(String p) {
		invokeTargetSetMethod("setDescription", p);
	}
	
	public void setDataSourceName(String p) {
		invokeTargetSetMethod("setDataSourceName", p);
	}
	
	public void setDriverType(String p) {
		invokeTargetSetMethod("setDriverType", p);
	}
	
	public void setNetworkProtocol(String p) {
		invokeTargetSetMethod("setNetworkProtocol", p);
	}
	
	public void setPassword(String p) {
		invokeTargetSetMethod("setPassword", p);
	}
	
	public void setPortNumber(int p) {
		invokeTargetSetMethod("setPortNumber", new Integer(p));
	}
	
	public void setServerName(String p) {
		invokeTargetSetMethod("setServerName", p);
	}
	
	public void setServiceName(String p) {
		invokeTargetSetMethod("setServiceName", p);
	}
	
	public void setTNSEntryName(String p) {
		invokeTargetSetMethod("setTNSEntryName", p);
	}
	
	public void setUser(String p) {
		invokeTargetSetMethod("setUser", p);
	}
	
	public void setDatabase(String p) {
		invokeTargetSetMethod("setDatabase", p);
	}

	public boolean isWrapperFor(Class iface) throws SQLException {
		return false;
	}

	public Object unwrap(Class iface) throws SQLException {
		return null;
	}

}
