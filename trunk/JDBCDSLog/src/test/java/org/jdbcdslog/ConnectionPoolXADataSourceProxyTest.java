package org.jdbcdslog;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConnectionPoolXADataSourceProxyTest extends TestCase {
	public ConnectionPoolXADataSourceProxyTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(ConnectionPoolXADataSourceProxyTest.class);
	}

	public void testApp() throws Exception {
		System.setProperty("org.jdbcdslog.ConnectionPoolXADataSourceProxy.targetDS", "org.hsqldb.jdbc.JDBCDataSource");
		ConnectionPoolXADataSourceProxy ds = new ConnectionPoolXADataSourceProxy();
		ds.setURL("jdbc:hsqldb:mem:mymemdb");
	}
}
