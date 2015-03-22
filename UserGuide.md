[Obtaining the library jar](UserGuide#Obtaining_the_library_jar.md)
  * [Obtaining the pre-packaged release](UserGuide#Obtaining_the_pre-packaged_release.md)
  * [Obtaining the pre-packaged release distro](UserGuide#Obtaining_the_pre-packaged_release_distro.md)
  * [Obtaining the most up-to-date version from SVN](UserGuide#Obtaining_the_most_up-to-date_version_from_SVN.md)
  * [Compiling library using your JDK installation](UserGuide#Compiling_library_using_your_JDK_installation.md)
  * [Obtaining dependencies](UserGuide#Obtaining_dependencies.md)
[Installing](UserGuide#Installing.md)

[Configuring](UserGuide#Configuring.md)
  * [Setup logging proxy](UserGuide#Setup_logging_proxy.md)
    * [Manually wrap the JDBC Connection object in the code](UserGuide#Manually_wrap_the_JDBC_Connection_object_in_the_code.md)
    * [Using JDBC Driver Proxy](UserGuide#Using_JDBC_Driver_Proxy.md)
    * [Setup logging JDBC DataSource proxy](UserGuide#Setup_logging_JDBC_DataSource_proxy.md)
    * [Wrap DataSource manually or using DI framework](UserGuide#Wrap_DataSource_manually_or_using_DI_framework.md)
  * [Setup logging engine](UserGuide#Setup_logging_engine.md)
  * [Configuration parameters](UserGuide#Configuration_parameters.md)
[Configuration examples](UserGuide#Configuration_examples.md)
  * [Configuring jdbcdslog on Resource level for Tomcat and HSQL](UserGuide#Configuring_jdbcdslog_on_Resource_level_for_Tomcat_and_HSQL.md)
  * [Configuring jdbcdslog for XA DataSource, JBoss, log4j and Oracle](UserGuide#Configuring_XA_DataSource_for_JBoss_with_log4j_and_Oracle.md)
  * [Configuring jdbcdslog for XA DataSource, JBoss, log4j and MySQL](UserGuide#Configuring_XA_DataSource_for_JBoss_with_log4j_and_MySQL.md)
[Performance](UserGuide#Performance.md)

# Obtaining the library jar #

There are three options for obtaining the files for the library.

  * Obtaining the pre-packaged release
  * Obtaining the pre-packaged release distro
  * Obtaining the most up-to-date version from SVN

## Obtaining the pre-packaged release jar ##

Most developers will want to use a pre-packaged release, which are typically the most stable versions of the library.

Click on the Downloads tab to download the latest stable library jar. It has only direct dependency on slf4j-api library which can be obtained from [slf4j project's page](http://www.slf4j.org/).

## Obtaining the pre-packaged release distro ##

You can also get a zipped archive of the entire project including dependencies, sources and unit tests on the Downloads tab. After extracting the library from the zipped archive, you can find entire library jar file in the folder with name 'target'.

## Obtaining the most up-to-date version from SVN ##

The most up-to-date version of the code is available from this project's SVN repository. Obtaining the code via SVN enables developers to get early access to fixes or features that have not yet been released in the pre-packaged version or for developers who want to contribute patches back to the project. Note that SVN releases are not guaranteed to be stable -- the code may be buggy and certain interfaces may change before the next release.

Obtain the code by using the one of SVN checkout commands listed on Source tab (you will need an SVN client installed on your computer)

## Compiling library using your JDK installation ##

You may need to compile the library in two cases:

  * you got sources from SVN (it doesn't contain jar file)
  * you need to run the library with older Java versions (release files contain library compiled using Java 6)

In this case you need to install and set up Maven on your computer, then navigate to project's directory and run mvn package command. After these steps you will find library jar in 'target' directory.

If you need to specify the path to your target JDK manually, you may specify it in pom.xml file in the project's root directory. The path to your JDK javac binary and compiler version should be specified in 'maven-compiler-plugin' plugin settings, for example:

```
<project>
...
<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<verbose>true</verbose>
					<fork>true</fork>
					<executable>/usr/lib/jdk1.4/bin/javac</executable>
					<compilerVersion>1.4</compilerVersion>
				</configuration>
			</plugin>
...
</build>
</project>
```

## Obtaining dependencies ##

After you got the library jar file, you will need to get the dependencies. Currently jdbcdslog directly depends only on 1.5.10+ version of [slf4j](http://www.slf4j.org/) library. You will need to download the slf4j-api-1.5.10.jar file from [slf4j project's page](http://www.slf4j.org/).

# Installation #

To install the jdbcdslog you just need to place library's jar and dependencies to classpath of your application.

Initially you need to put jdbcdslog-0.1.jar and slf4j-api-1.5.10.jar to classpath. Some of the dependencies could be found in lib directory of jdbcdslog distro zip file, but another may need to be  downloaded from external projects web site(i.e. slf4j.org). You may need to put other dependencies files depends on logging engine you will choose for the logging. Please look at logging section for more details.

# Configuring #

You need to configure two things in order to use the jdbcdslog library:

  * setup logging proxy
  * setup logging engine

## Setup logging proxy ##

There are three use cases of jdbcdslog usage:

  * you can manually wrap JDBC Connection object in your code
  * you can setup logging JDBC Driver proxy
  * you can setup logging JDBC DataSource proxy

### Manually wrap the JDBC Connection object in the code ###

The simplest scenario is to just wrap the existing JDBC Connection. You can do something like following in your code:

```
Connection loggingConnection = ConnectionLoggingProxy.wrap(originalConnection);
```

That's it. Now all JDBC calls passed through loggingConnection object will be intercepted and logged.

### Using JDBC Driver Proxy ###

In addition you can use JDBC Driver Logging Proxy. It will log all JDBC calls and transfer it to original JDBC Driver at the same time.

In this case you should use `org.jdbcdslog.DriverLoggingProxy` instead of the original JDBC Driver class and use jdbcdslog URL format for connection URL parameter:

`jdbc:jdbcdslog:<original URL>;targetDriver=<original JDBC driver full class name>`

In example following original connection URL

`jdbc:hsqldb:mem:mymemdb`

should be transformed to

`jdbc:jdbcdslog:hsqldb:mem:mymemdb;targetDriver=org.hsqldb.jdbcDriver`

After this all JDBC connections for new URL will use ldbcdslog logging proxy, all JDBC calls wil be logged and transfered to your original JDBC driver.

### Setup logging JDBC DataSource proxy ###

Also you can setup logging on DataSource level. The idea is the same as for JDBC Driver Proxy -- jdbcdslog DataSource proxy intercepts and log all JDBC calls.

In this case you should use one of the following classes `org.jdbcdslog.ConnectionPoolXADataSourceProxy` instead of your original DataSource class:
  * `org.jdbcdslog.ConnectionPoolXADataSourceProxy`
  * `org.jdbcdslog.XADataSourceProxy`
  * `org.jdbcdslog.ConnectionPoolDataSourceProxy`
  * `org.jdbcdslog.DataSourceProxy`

Your choice should based on the type of your original DataSource: is it XA, or Pooled or XA and Pooled?

Now you need to pass original JDBC DataSource class name to proxy. You can use one of the following to choices:

  * pass additional parameter `targetDS` to the DataBase or URL setting for DataSource. In example if our original URL is `jdbc:hsqldb:mem:mymemdb`, then you should transform it to `jdbc:hsqldb:mem:mymemdb;targetDS=<original DataSource full class name>`.

  * pass custom parameter targetDS with original DataSource class name to the xDataSourceProxy in your J2EE/Servlets container settings.

### Wrap DataSource manually or using DI framework ###

All jdbcdslog logging proxy classes have targetDSDirect property, and you can wrap any DataSource object by following code using this property:

```
  ConnectionPoolXADataSourceProxy ds = new ConnectionPoolXADataSourceProxy();
  ds.setTargetDSDirect(originalDS);
```

In addition you can use this property to wrap existing DataSource in DI frameworks(in Spring for example):

```
<bean id="dataSourceActual"
class="org.springframework.jndi.JndiObjectFactoryBean">
    <property name="jndiName">
        <value>java:comp/env/jdbc/MyDatasource</value>
    </property>
</bean>

<bean id="dataSource" class="org.jdbcdslog.ConnectionPoolDataSourceProxy">
    <property name="targetDSDirect" ref="dataSourceActual" />
</bean>
```

## Setup logging engine ##

jdbcdslog uses slf4j tool for logging. It means that all popular logging engine can be used for SQL logging with jdbcdslog. Currently slf4j supports following engines:
  * log4j
  * jakarta common logging
  * java util logging
  * System.out

If you want to connect jdbcdslog to your favorite logging engine, you need just download appropriate connector from slf4j.org web site and put it in your classpath. That's it.

jdbcdslog provides 3 loggers(in terms of log4j and jakarta commons) to maintain logging level:

  * org.jdbcdslog.ConnectionLogger - trace connection details. It produces following log entries:
`56 [main] INFO org.jdbcdslog.ConnectionLogger - connect to URL jdbc:hsqldb:. with properties: {user=sa`}
  * org.jdbcdslog.StatementLogger - trace statements sent to DB. It produces following log entries:
`62627 [http-8080-1] INFO org.jdbcdslog.StatementLogger - java.sql.PreparedStatement.executeQuery select id, email from user where username = ? parameters: {'admin'} 12ms.`
  * org.jdbcdslog.SlowQueryLogger - trace slow statements sent to DB. If query takes more time then specified by jdbcdslog.slowQueryThreshold, it will be logged.

  * org.jdbcdslog.ResultSetLogger - trace query results. It produces following log entries:
`62118 [http-8080-2] INFO org.jdbcdslog.ResultSetLogger - java.sql.ResultSet.next {1234, 'root@a.com'`}

All SQL trace entries are available on INFO and ERROR levels.

DEBUG level for other jdbcdslog classes will produce tons of debug log entries. Usually you don't want to turn it on.

## Configuration parameters ##

jdbcdslog can get configuration parameters. It can be passed as JVM parameters (i.e. -Djdbcdslog.slowQueryThreshold=1000) or specified in the jdbcdslog.properties file. jdbcdslog.properties file should be located in the classpath of your application.

For now jdbcdslog supports following parameters:

  * jdbcdslog.slowQueryThreshold - if some SQL query gets more time then specified in this parameter, it will be logged by SlowQueryLogger. Parameter should contain the time in milliseconds.

  * jdbcdslog.logText - jdbcdslog will log Reader object's content for PreparedStatement and ResultSet if this parameter set to 'true' value.

  * jdbcdslog.printStackTrace - jdbcdslog will log calling method stack trace if this parameter set to 'true' value. Please keep in mind that turning on of this parameter can kill performance of application.

# Configuration examples #

## Configuring jdbcdslog on Resource level for Tomcat and HSQL ##

Copy  slf4j-api-1.5.10.jar, slf4j-simple-1.5.10.jar and jdbcdslog-0.1.jar to the TOMCAT\_HOME/lib directory.

context.xml file for your application should contain something like following:

```
<Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource"
               maxActive="100" maxIdle="30" maxWait="10000"
               username="sa" driverClassName="org.hsqldb.jdbcDriver"
               url="jdbc:hsqldb:."/>
```

then you need to modify it in following way:

```
<Resource name="jdbc/TestDB" auth="Container" type="javax.sql.DataSource"
               maxActive="100" maxIdle="30" maxWait="10000"
               username="sa" driverClassName="org.jdbcdslog.DriverLoggingProxy"
               url="jdbc:jdbcdslog:hsqldb:.;targetDriver=org.hsqldb.jdbcDriver"/>
```

Now you can start or restart your tomcat and see SQL trace entries in the catalina.out file.

## Configuring XA DataSource for JBoss with log4j and Oracle ##

Copy jdbcdslog-0.1.jar, slf4j-api-1.5.10.jar and slf4j-log4j12-1.5.10.jar to SERVER\_ROOT/lib directory.

You already should have definition of DataSource like:

```
  <xa-datasource>
    <jndi-name>OracleDS</jndi-name>
    <track-connection-by-tx></track-connection-by-tx>
    <isSameRM-override-value>false</isSameRM-override-value>
    <xa-datasource-class>oracle.jdbc.xa.client.OracleXADataSource</xa-datasource-class>
    <xa-datasource-property name="URL">jdbc:oracle:oci8:@db</xa-datasource-property>
    <xa-datasource-property name="User">user</xa-datasource-property>
    <xa-datasource-property name="Password">password</xa-datasource-property>
    <exception-sorter-class-name>org.jboss.resource.adapter.jdbc.vendor.OracleExceptionSorter</exception-sorter-class-name>
    <no-tx-separate-pools></no-tx-separate-pools>
  </xa-datasource>

```

Then you need to modify it:

```
  <xa-datasource>
    <jndi-name>OracleDS</jndi-name>
    <track-connection-by-tx></track-connection-by-tx>
    <isSameRM-override-value>false</isSameRM-override-value>

    <xa-datasource-class>org.jdbcdslog.ConnectionPoolXADataSourceProxy</xa-datasource-class>
    <xa-datasource-property name="URL">jdbc:oracle:oci8:@db?targetDS=oracle.jdbc.xa.client.OracleXADataSource</xa-datasource-property>

    <xa-datasource-property name="User">user</xa-datasource-property>
    <xa-datasource-property name="Password">password</xa-datasource-property>
    <exception-sorter-class-name>org.jboss.resource.adapter.jdbc.vendor.OracleExceptionSorter</exception-sorter-class-name>
    <no-tx-separate-pools></no-tx-separate-pools>
  </xa-datasource>
```

and add jdbcdslog category entry to log4j configuration file. I.e. you can add following to jboss-log4j.xml:

```
<category name="org.jdbcdslog">
    <priority value="INFO"/>
</category>
```

After changes applied you will see jdbcdslog SQL entries in the logs.

## Configuring XA DataSource for JBoss with log4j and MySQL ##

Thanks to Dmitry Egorov for contribution to this section.

Copy jdbcdslog-0.1.jar, slf4j-api-1.5.10.jar and slf4j-log4j12-1.5.10.jar to SERVER\_ROOT/lib directory.

You already should have definition of DataSource like:

```
<datasources>
  <local-tx-datasource>
    <jndi-name>MySqlDS</jndi-name>
    <connection-url>jdbc:mysql://mysql-hostname:3306/jbossdb</connection-url>
    <driver-class>com.mysql.jdbc.Driver</driver-class>
    <user-name>x</user-name>
    <password>y</password>
    <exception-sorter-class-name>org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter</exception-sorter-class-name>
  </local-tx-datasource>
</datasources>

```

Then you need to modify it:

```
<datasources>
  <local-tx-datasource>
    <jndi-name>MySqlDS</jndi-name>
    <connection-url>jdbc:jdbcdslog:mysql://mysql-hostname:3306/jbossdb;targetDriver=com.mysql.jdbc.Driver</connection-url>
    <driver-class>org.jdbcdslog.DriverLoggingProxy</driver-class>
    <user-name>x</user-name>
    <password>y</password>
    <exception-sorter-class-name>org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter</exception-sorter-class-name>
  </local-tx-datasource>
</datasources>
```

and add jdbcdslog category entry to log4j configuration file. I.e. you can add following to jboss-log4j.xml:

```
<category name="org.jdbcdslog">
    <priority value="INFO"/>
</category>
```

After changes applied you will see jdbcdslog SQL entries in the logs.


# Performance #

According to our stress tests jdbcdslog produces 20s overhead for 1 million queries or 0.02 ms per query with log4j and asynchronous appender.