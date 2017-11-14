package com.ibg.datasource;

import java.sql.Connection;

import javax.sql.DataSource;

import org.javalite.activejdbc.Base;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

public class ActiveJdbcCon implements ApplicationListener<ContextStartedEvent> {

	private DataSource acdmDataSource;

	public DataSource getAcdmDataSource() {
		return acdmDataSource;
	}

	public void setAcdmDataSource(DataSource dataSource) {
		this.acdmDataSource = dataSource;
	}

	public Connection getConnection() {

		try {
			if (Base.hasConnection()) {
				return Base.connection();
			} else if (null != acdmDataSource) {
				Base.open(acdmDataSource);
				return Base.connection();
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public Connection getThisConnection(DataSource dataSource) {

		acdmDataSource = dataSource;
		return getConnection();

	}
	
	public static Connection getSelRecConnection(DataSource dataSource){
		try {
			if (null != dataSource) {
				Base.open(dataSource);
				return Base.connection();
			}
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public static void closeConnection() {
		if (Base.hasConnection()) {
			Base.close();
		}
	}

	@Override
	public void onApplicationEvent(ContextStartedEvent event) {
		Base.open(acdmDataSource);
	}

}
