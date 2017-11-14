package com.ibg.utils;

import java.sql.*;

import org.apache.log4j.Logger;

import com.ibg.db.ServiceLocator;

public class DBUtility {
	private static final Logger log = Logger.getLogger(DBUtility.class);

	/*
	 * // commented out. Was hurting non-datasource users. static { refresh(); }
	 */
	public static void close(ResultSet resultSet) {
		try {
			if (resultSet != null)
				resultSet.close();
		} catch (SQLException e) {
			log.warn("DBUtility: 1 SQLException thrown when closing ResultSet. ", e);
		} finally {
			resultSet = null;
		}
	}

	public static void close(ResultSet resultSet, Statement statement) {
		close(resultSet);

		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			if (!"08006".equals(e.getSQLState())) { // ignore
													// "No more data to read"
													// Actual problem is
													// staleconnectionexception.
													// There is a patch but for
													// now just ignore error.
													// This should be ok once
													// new websphere in May2008.
				log.warn("DBUtility: 2 SQLException thrown when closing a statement. " + e.getSQLState(), e);
			}
		} finally {
			statement = null;
		}
	}

	public static void close(ResultSet resultSet, Statement statement, Connection connection) {
		close(resultSet, statement);

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			log.warn("DBUtility: 3 SQLException thrown when closing Connection. " + e.getSQLState(), e);
		} finally {
			connection = null;
		}
	}

	public static void close(Statement statement) {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException e) {
			log.warn("DBUtility: 4 SQLException thrown when closing a statement. ", e);
		} finally {
			statement = null;
		}
	}

	public static void close(Statement statement, Connection connection) {
		close(statement);

		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			log.warn("DBUtility: 5 SQLException thrown when closing Connection. ", e);
		} finally {
			connection = null;
		}
	}

}
