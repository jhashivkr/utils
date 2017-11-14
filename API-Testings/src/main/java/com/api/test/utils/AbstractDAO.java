/**
 * Created on Jan 24, 2008
 *
 */
package com.api.test.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractDAO
{
	private Connection conn;
	private boolean receivedExternalConnection;

	/**
	 * Default constructor.  Assumes the connection should be created via the JNDI datasource.
	 */
	protected AbstractDAO()
	{
		receivedExternalConnection = false;
		conn = null;
	}

	/**
	 * Uses the connection that's passed in.  This is primarily useful for calls from outside WebSphere.
	 * @param c Existing connection
	 */
	protected AbstractDAO(Connection c)
	{
		if (c != null)
		{
			conn = c;
			receivedExternalConnection = true;
		}
		else
		{
			receivedExternalConnection = false;
		}
	}

	protected final void close(ResultSet rs, Statement stmt)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			}
			catch (Throwable t)
			{
			}
		}

		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch (Throwable t)
			{
			}
		}
	}

	/**
	 * Closes the connection, if present.  
	 * <br>Note that this can be dangerous in a non-Websphere 
	 * environment 'cuz the DAO cannot recreate the connection. 
	 */
	protected final void closeConnection()
	{
		if (!receivedExternalConnection)
		{
			try
			{
				conn.close();
			}
			catch (Throwable t)
			{
			}
			finally
			{
				conn = null;
			}
		}
	}
	protected final void closeConnection(ResultSet rs, Statement stmt)
	{
		close(rs, stmt);
		closeConnection();
	}

	protected final void closeResultSet(ResultSet resultSet)
	{
		try
		{
			if (resultSet != null)
			{
				resultSet.close();
			}
		}
		catch (SQLException e)
		{
			// don't know, don't care
		}
	}

	protected final void closeStatement(Statement statement)
	{
		try
		{
			if (statement != null)
			{
				statement.close();
			}
		}
		catch (SQLException e)
		{
			// don't know, don't care
		}
	}

	protected Connection createNewConnection() throws SQLException
	{
		return DBUtility.getConnection();
	}

	/**
	 * @return Returns the connection passed in at instantiation, or creates a new one via the DBUtility.getConnection() method
	 * @throws SQLException
	 */
	protected final Connection getConnection() throws SQLException
	{
		if (!receivedExternalConnection)
		{
			conn = createNewConnection();
		}

		return conn;
	}

	protected final Date parseOracleDate(String oracleValue) throws Exception
	{
		if (oracleValue == null)
		{
			return null;
		}

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return df.parse(oracleValue);
	}
	
	protected final void setConnection(Connection conn) throws SQLException
	{
		if (this.conn != null)
		{
			throw new SQLException("Cannot set connection when the DAO already has a connection");
		}
		else
		{
			this.conn = conn;
		}
	}
}
