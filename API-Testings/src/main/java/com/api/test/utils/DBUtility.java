package com.api.test.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class DBUtility
{
	private static final Logger log = Logger.getLogger(DBUtility.class);
	private static final int MAX_JDBC_CONNECTION_RETRIES;
	static
	{
		String retryProperty = VariableData.getProperty("jdbcconnectionmaxretries");
		int maxRetries;
		try
		{
			maxRetries = Integer.parseInt(retryProperty);
		}
		catch (NumberFormatException nfe)
		{
			log.warn("Invalid value for jdbcconnectionmaxretries: " + retryProperty);
			maxRetries = 3;
		}
		MAX_JDBC_CONNECTION_RETRIES = maxRetries;
	}

	private static DataSource dataSource;
	private static DataSource datawarehouseDataSource;
	private static DataSource timsDB2DataSource;
	private static String defaultID;
	private static String defaultPassword;
	private static String defaultDatawarehouseUserID;
	private static String defaultDatawarehousePSWD;
	private static String timsDB2UserID;
	private static String timsDB2Password;
	private static String mainframeUserID;
	private static String mainframePSWD;
	private static DataSource mainframeDataSource;

	/*	// commented out.  Was hurting non-datasource users.
		static
		{
			refresh();
		}
	*/
	public static void close(ResultSet resultSet)
	{
		try
		{
			if (resultSet != null)
				resultSet.close();
		}
		catch (SQLException e)
		{
			log.warn("DBUtility: 1 SQLException thrown when closing ResultSet. ", e);
		}
		finally
		{
			resultSet = null;
		}
	}
	public static void close(ResultSet resultSet, Statement statement)
	{
		close(resultSet);

		try
		{
			if (statement != null)
				statement.close();
		}
		catch (SQLException e)
		{
			if (!"08006".equals(e.getSQLState()))
			{ //ignore "No more data to read"  Actual problem is staleconnectionexception.  There is a patch but for now just ignore error. This should be ok once new websphere in May2008.
				log.warn(
					"DBUtility: 2 SQLException thrown when closing a statement. "
						+ e.getSQLState(), e);
			}
		}
		finally
		{
			statement = null;
		}
	}
	public static void close(ResultSet resultSet, Statement statement, Connection connection)
	{
		close(resultSet, statement);

		try
		{
			if (connection != null)
				connection.close();
		}
		catch (SQLException e)
		{
			log.warn(
				"DBUtility: 3 SQLException thrown when closing Connection. " + e.getSQLState(), e);
		}
		finally
		{
			connection = null;
		}
	}
	public static void close(Statement statement)
	{
		try
		{
			if (statement != null)
				statement.close();
		}
		catch (SQLException e)
		{
			log.warn("DBUtility: 4 SQLException thrown when closing a statement. ", e);
		}
		finally
		{
			statement = null;
		}
	}
	public static void close(Statement statement, Connection connection)
	{
		close(statement);

		try
		{
			if (connection != null)
				connection.close();
		}
		catch (SQLException e)
		{
			log.warn("DBUtility: 5 SQLException thrown when closing Connection. ", e);
		}
		finally
		{
			connection = null;
		}
	}
	/**
	 * This overloaded method calls getConnection(String user, boolean pooled) to
	 * get a pooled database connection for the default user.
	 * @return database connection
	 * @exception java.sql.SQLException
	 */
	public static Connection getConnection() throws SQLException
	{
		return getConnection(null, true);
		
		//return getConnection(null, false);
	}
	/**
	 * This overloaded method calls getConnection(String user, boolean pooled) to
	 * get either a pooled database connection for the specified user.
	 * @param user - user ID and password used to secure connection
	 * @return database connection
	 * @exception java.sql.SQLException
	 */
	public static Connection getConnection(String user) throws SQLException
	{
		return getConnection(user, true);
	}
	/**
	 * Note: The behavior of the set of overloaded getConnection methods is to
	 *    use the default user if a user parameter is not present and to
	 *    secure a pooled connection if no pooled parameter is present.
	 * @param user - user ID and password used to secure connection
	 * @param pooled - whether to get a pooled connection
	 * @return database connection
	 * @exception java.sql.SQLException
	 */
	public static Connection getConnection(String user, boolean pooled) throws SQLException
	{
		String userID = null;
		String password = null;
		if (pooled && dataSource == null) refresh();
		if (user == null) // default user info desired
		{
			userID = defaultID;
			password = defaultPassword;
		}
		else
		{
			userID = VariableData.properties.getProperty(user + "ID");
			password = VariableData.properties.getProperty(user + "Password");
		}

		Connection connection;
		if (pooled) // get database connection from the shared pool
		{
			connection = getPooledEBOracleConnectionWithRetries(userID, password);
		}
		else // get straight/non-pooled database connection
			{
			connection = getNonPooledEBOracleConnectionWithRetries(userID, password);
		}
		return connection;
	}
	/**
	 * @param userID
	 * @param password
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static Connection getNonPooledEBOracleConnectionWithRetries(String userID, String password) throws SQLException
	{
		Connection connection;
		try
		{
			Class.forName(VariableData.properties.getProperty("oracleDriver"));
			DriverManager.setLogWriter(null);
			System.out.println(userID + ", " + password);
			connection = DriverManager.getConnection(VariableData.properties.getProperty("oracleURL"), userID, password);
		}
		catch (ClassNotFoundException e)
		{
			log.warn("DBUtility: ClassNotFoundException thrown " + " when creating non-pooled connection.", e);
			throw new SQLException("Unable to load Oracle driver: " + e.getMessage());
		}
		return connection;
	}
	/**
	 * @param userID
	 * @param password
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static Connection getPooledEBOracleConnectionWithRetries(String userID, String password) throws SQLException
	{
		Connection connection = null;
		int attemptCount = 0;
		SQLException latestException = null;
		while (connection == null && attemptCount < MAX_JDBC_CONNECTION_RETRIES)
		{
			attemptCount++;
			try
			{
				connection = dataSource.getConnection(userID, password);
			}
			catch (SQLException e)
			{
				log.info("Error connecting to Oracle on retry #" + attemptCount);
				latestException = e;
			}
		}

		if (attemptCount > 1)
		{
			if (connection == null)
			{
				log.warn("Failed to connect to Oracle after " + attemptCount + " attempts", latestException);
				throw latestException;
			}
			else
			{
				log.warn("Connected to Oracle after " + attemptCount + " attempts");
			}
		}
		return connection;
	}

	public static Connection getDataWarehouseConnection(String user, boolean pooled)
		throws SQLException
	{
		String userID = null;
		String password = null;
		Connection connection = null;
		try
		{
			if (user == null) // default user info desired
			{
				userID = defaultDatawarehouseUserID;
				password = defaultDatawarehousePSWD;
			}
			else
			{
				userID = VariableData.properties.getProperty("datawarehouseUserID");
				password = VariableData.properties.getProperty("datawarehousePSWD");
			}

			if (pooled) // get database connection from the shared pool
			{
				if (datawarehouseDataSource == null)
					refreshDatawarehouse();

				connection = datawarehouseDataSource.getConnection(userID, password);
			}
			else
			{ // get straight/non-pooled database connection
				try
				{
					Class.forName(VariableData.properties.getProperty("datawarehouseDriver"));
					DriverManager.setLogWriter(null);
					connection =
						DriverManager.getConnection(
							VariableData.properties.getProperty("datawarehouseURL"),
							userID,
							password);
				}
				catch (ClassNotFoundException e)
				{
					log.warn(
						"DBUtility:getDatawarehouseConnection ClassNotFoundException thrown "
							+ " when creating non-pooled connection.", e);
				}
				catch (Exception ee)
				{
					throw ee;
				}
			}
		}
		catch (Exception f)
		{
			f.printStackTrace();
			throw new SQLException("DBUtility.getDataWarehouseConnection err "+f);
		}

		return connection;
	}

	/**
	 * This overloaded method calls getConnection(String user, boolean pooled) to
	 * get either a pooled or non-pooled database connection for the default user.
	 * @param pooled - whether to get a pooled connection
	 * @return database connection
	 * @exception java.sql.SQLException
	 */
	public static Connection getConnection(boolean pooled) throws SQLException
	{
		return getConnection(null, pooled);
	}
	public static void refresh()
	{
		defaultID = VariableData.properties.getProperty("defaultID");
		defaultPassword = VariableData.properties.getProperty("defaultPassword");

		try
		{
			// access naming system
			Context context = new InitialContext();
			// get DataSource from naming system
			String jndiKey = VariableData.properties.getProperty("oracleDataSourceName");
			dataSource =
				(DataSource) context.lookup(jndiKey);
					
			refreshDatawarehouse();
			refreshTIMS_DB2_IdAndPassword();
			refreshMainframe();
		}
		catch (NamingException e)
		{
			// Ignore - this means we're running in a non-container environment
		}
		catch (Throwable e)
		{
			log.warn("DBUtility: Unknown Exception thrown when setting DataSource.", e);
		}
	}
	private static void refreshDatawarehouse() throws Exception
	{
		defaultDatawarehouseUserID =
			VariableData.properties.getProperty("defaultDatawarehouseUserID");
		defaultDatawarehousePSWD = VariableData.properties.getProperty("defaultDatawarehousePSWD");

		try
		{
			// access naming system
			Context context = new InitialContext();

			// get DataSource from naming system 
			datawarehouseDataSource =
				(DataSource) context.lookup(
					VariableData.properties.getProperty("datawarehouseDataSourceName"));
		}
		catch (NamingException e)
		{
			throw e;
		}
		catch (Exception f)
		{
			throw f;
		}
	}
	public static Connection getTIMSDB2Connection(boolean pooled) throws Exception
	{
		if (timsDB2Password == null || timsDB2UserID == null)
		{
			refreshTIMS_DB2_IdAndPassword();
		}

		Connection connection = null;
		if (pooled) // get database connection from the shared pool
		{
			if (timsDB2DataSource == null)
				refreshTIMS_DB2_IdAndPassword();

			connection = timsDB2DataSource.getConnection(timsDB2UserID, timsDB2Password);
		}
		else // get straight/non-pooled database connection
			{
			try
			{
				Class.forName(VariableData.properties.getProperty("TIMS_DB2_Driver"));
				DriverManager.setLogWriter(null);
				java.util.Properties libs = new java.util.Properties();
				libs.put("libraries", "EB");
				libs.put("user", timsDB2UserID);
				libs.put("password", timsDB2Password);
				connection =
					DriverManager.getConnection(
						VariableData.properties.getProperty("TIMS_DB2_URL"),
						libs);
			}
			catch (ClassNotFoundException e)
			{
				log.warn(
					"DBUtility: ClassNotFoundException thrown when creating TIMS DB2 non-pooled connection.", e);
				throw e;
			}
		}
		return connection;
	}
	private static void refreshTIMS_DB2_IdAndPassword() throws Exception
	{
		timsDB2UserID = VariableData.properties.getProperty("TIMS_DB2_ID");
		timsDB2Password = VariableData.properties.getProperty("TIMS_DB2_Password");
		try
		{
			// access naming system
			Context context = new InitialContext();
			// get DataSource from naming system
			timsDB2DataSource =
				(DataSource) context.lookup(VariableData.properties.getProperty("TIMS_DB2_DataSourceName"));
		}
		catch (ConfigurationException ce)
		{
			// ignore - this means we're running in a non-pooled environment
		}
		catch (NoInitialContextException nice){
			// ignore
		}
		catch (Exception e)
		{
			throw e;
		}
	}
	public static Connection getMainframeConnection(boolean pooled)
	throws SQLException
	{
		Connection connection = null;
		try
		{
			if (pooled) // get database connection from the shared pool
			{
				if (mainframeDataSource == null)
					refreshMainframe();
	
				connection = mainframeDataSource.getConnection(mainframeUserID, mainframePSWD);
			}
			else
			{ // get straight/non-pooled database connection
				try
				{
					mainframeUserID = VariableData.properties.getProperty("mainframeUserID");
					mainframePSWD   = VariableData.properties.getProperty("mainframePSWD");
					java.util.Properties conProp = new java.util.Properties();
					conProp.put("user", mainframeUserID);
					conProp.put("password", mainframePSWD);
					conProp.put("clientApplicationInformation","iPage");

					Class.forName(VariableData.properties.getProperty("mainframeDB2Driver"));
					DriverManager.setLogWriter(null);
//					connection =
//						DriverManager.getConnection(
//							VariableData.properties.getProperty("mainframeURL"),
//							VariableData.properties.getProperty("mainframeUserID"),
//							VariableData.properties.getProperty("mainframePSWD"));

					connection =
						DriverManager.getConnection(
							VariableData.properties.getProperty("mainframeURL"),
							conProp);

				}
				catch (ClassNotFoundException e)
				{
					log.warn(
						"DBUtility:getMainframeConnection ClassNotFoundException thrown "
							+ " when creating non-pooled connection.", e);
				}
				catch (Exception e)
				{
					log.warn(e);
				}
			}
		}
		catch (SQLException e)
		{
			throw e;
		}
		catch (Exception f)
		{
			f.printStackTrace();
			throw new SQLException("DBUtility.getMainframeConnection err "+f);
		}
	
		return connection;
	}
	private static void refreshMainframe() throws Exception
	{
		mainframeUserID =
			VariableData.properties.getProperty("mainframeUserID");
		mainframePSWD = VariableData.properties.getProperty("mainframePSWD");

		try
		{
			// access naming system
			Context context = new InitialContext();

			// get DataSource from naming system 
			mainframeDataSource =
				(DataSource) context.lookup(
					VariableData.properties.getProperty("mainframeDataSourceName"));
		}
		catch (NamingException e)
		{
			throw e;
		}
		catch (Exception f)
		{
			log.warn("refreshMainframe f=", f);
			throw f;
		}
	}

}
