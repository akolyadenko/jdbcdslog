The goal of the project is to create a high-performance and easy to use tool for the SQL queries logging for JDBC applications.

Features include:
  * can be attached on Connection, Driver or DataSource(XA,Pooling) levels (the last was my main motivation to start this project)
  * can log bind parameters for PreparedStatement and CallableStatement calls
  * can log result of queries from ResultSet objects
  * can log queries execution time
  * can detect slow queries
  * logging engine agnostic (thanks to SLF4J, you can use most of popular logging libraries like log4j, apache common logging, java logging or simple printing to System.out)
  * can log calling method stack trace.

Additionally fork of this project is available: http://code.google.com/p/jdbcdslog-exp/, which adds new features:

  * more directly fill SQL statement (you can copy it to the tool like Oracle's SQL Developer and can be used directly)
  * can use a configurable SQL formatter
  * can be configured to display elapsed time
  * is available in the maven central repository
  * supports Java 5 and above (you need to understand that the project should compile with jdk 1.5)

**News.**

06/29/2010. Version 1.0.5 is released.

  * logging of Reader object's content passed to PreparedStatement and ResultSet has been implemented.
  * logging of calling methods stack trace has been implemented.
  * bug fixes.

06/11/2010. Version 1.0.4 is released.

  * New property targetDSDirect added to proxy classes. It allows to wrap DataSource objects by proxy in DI frameworks like Spring. Thanks to  Dill Sellars for the patch.


04/08/2010. Version 1.0.3 is released.

  * NullPointerException during parameters initialization has been fixed.

04/07/2010. Version 1.0.2 is released.

  * targetDS parameter can be configured for DataSource in J2EE/Servlets container. Previously it could be configured only as part of URL or Database parameters.

04/06/2010. Version 1.0.1 is released.

  * New configuration parameter `jdbcdslog.slowQueryThreshold` and logger `org.jdbcdslog.SlowQueryLogger` for slow queries detection were added.
  * Added new classes: `XADataSourceProxy`, `ConnectionPoolDataSourceProxy` and `DataSourceProxy`. It allows to wrap different types of DataSources: XA, Pooling and just `DataSource`. Previously only `ConnectionPoolXADataSourceProxy` was available.

03/30/2010. Version 1.0.0 is released.