package com.ibg.data.uploaders;

import java.sql.Connection;

import javax.sql.DataSource;

public abstract class AbstractAcdmDataUploaders implements AcdmDataUploaders{
	public abstract void setDataSource(DataSource dataSource);	
	public void setDbConnection(Connection connection){		
	}
}
