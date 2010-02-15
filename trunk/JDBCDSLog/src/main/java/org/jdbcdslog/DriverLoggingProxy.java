package org.jdbcdslog;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DriverLoggingProxy implements Driver {

	static Logger logger = LoggerFactory.getLogger(DriverLoggingProxy.class);
	
	static
    {
        try
        {
            DriverManager.registerDriver(new DriverLoggingProxy());
        }
        catch(Exception exception) { }
    }
	
	Driver target = null;
	
	public DriverLoggingProxy() throws JDBCDSLogException {
		try {
			logger.info("In Constructor.");
			String className = System
				.getProperty("org.jdbcdslog.DriverLoggingProxy.targetDriver");
			if (className == null)
				throw new JDBCDSLogException("Can't find org.jdbcdslog.DriverLoggingProxy.targetDriver property.");
			Class cl = Class.forName(className);
			if (cl == null)
				throw new JDBCDSLogException("Can't load class of targetDS.");
			Object targetObj = cl.newInstance();
			target = (Driver)targetObj;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new JDBCDSLogException(e);
		}
	}
	
	public boolean acceptsURL(String url) throws SQLException {
		logger.info("acceptsURL");
		return target.acceptsURL(url);
	}

	public Connection connect(String url, Properties info) throws SQLException {
		logger.info("connect");
		return ConnectionLoggingProxy.wrap(target.connect(url, info));
	}

	public int getMajorVersion() {
		return target.getMajorVersion();
	}

	public int getMinorVersion() {
		return target.getMinorVersion();
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		return target.getPropertyInfo(url, info);
	}

	public boolean jdbcCompliant() {
		return target.jdbcCompliant();
	}
	
}
