The goal of the project is to create a high-performance and easy to use tool for the SQL queries logging for JDBC applications.

Features include:

* can be attached on Connection, Driver or DataSource(XA,Pooling) levels (the last was my main motivation to start this project)
* can log bind parameters for PreparedStatement and CallableStatement calls
* can log result of queries from ResultSet objects
* can log queries execution time
* can detect slow queries
* logging engine agnostic (thanks to SLF4J, you can use most of popular logging libraries like log4j, apache common logging, java logging or simple printing to System.out)
* can log calling method stack trace.


User guide: https://github.com/akolyadenko/jdbcdslog/blob/wiki/UserGuide.md
