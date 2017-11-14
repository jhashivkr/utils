package com.ibg.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.javalite.activejdbc.Base;

import com.ibg.datasource.DBEnvConnections;
import com.ibg.utils.PropertyReader;

@Aspect
public class DBConnection {

	private static Connection connection = null;
	private static String loadDbEnv = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getLoadDbEnv();
	private static DataSource dataSource = null;

	public DBConnection() {

	}
	
	public static Connection getDbConnection(){
		return connection;
	}
	
	public static DataSource getDataSource(){
		return dataSource;
	}

	@Before("execution(* com.ibg.data.uploaders.*.startData*())")
	private static void prepareConnection() {
		System.out.println("called before the call of startData*");
		try {

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			connection = env.getConn(loadDbEnv);
			dataSource =  env.getDataSource(loadDbEnv);
			Base.open(dataSource);
		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			}
		}
		
		
	}

	@After("execution(* com.ibg.data.uploaders.*.startData*())")
	public void closeConnection() {
		System.out.println("called after the call of startData*");
		try {
			if (Base.hasConnection()) {
				Base.close();
			}
			if (null != connection) {
				connection.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (Base.hasConnection()) {
					Base.close();
				}
				if (null != connection) {
					connection.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		
	}

}
