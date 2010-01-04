package org.jdbcdslog;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;


public class OracleConnectionPoolDataSourceProxy extends OracleConnectionPoolDataSource {
	
	static Logger logger = LoggerFactory.getLogger(OracleConnectionPoolDataSourceProxy.class);
	
	public OracleConnectionPoolDataSourceProxy() throws SQLException {
		super();
	}
	
	public Connection getConnection() throws SQLException {
		return wrap(super.getConnection());
	}
	
	public Connection getConnection(String _user, String _passwd)
			throws SQLException {
		return wrap(super.getConnection(_user, _passwd));
	}
	
	public Connection getConnection(Properties cachedConnectionAttributes)
			throws SQLException {
		return wrap(super.getConnection(cachedConnectionAttributes));
	}
	
	public PooledConnection getPooledConnection() throws SQLException {
		return wrap(super.getPooledConnection());
	}
	
	public Connection getConnection(String _user, String _passwd,
			Properties cachedConnectionAttributes) throws SQLException {
		return wrap(super.getConnection(_user, _passwd, cachedConnectionAttributes));
	}
	
	public PooledConnection getPooledConnection(String _user, String _passwd)
			throws SQLException {
		return wrap(super.getPooledConnection(_user, _passwd));
	}
	
	public synchronized void setLogWriter(PrintWriter pw) {
		super.setLogWriter(pw);
	}
	
	Connection wrap(Connection con) {
		return (Connection)Proxy.newProxyInstance(con.getClass().getClassLoader(), new Class[]{Connection.class}, new GenericLoggingProxy(con));
	}
	
	PooledConnection wrap(PooledConnection con) {
		return (PooledConnection)Proxy.newProxyInstance(con.getClass().getClassLoader(), new Class[]{PooledConnection.class}, new GenericLoggingProxy(con));
	}
}
