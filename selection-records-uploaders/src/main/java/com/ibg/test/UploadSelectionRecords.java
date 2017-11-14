package com.ibg.test;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.uploaders.SelectionRecUploadMgr;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;

public class UploadSelectionRecords {

	static {
		new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}
	final static Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {
		try {

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			DataSource dataSource = env.getDataSource("stage");

			SelectionRecUploadMgr selectionRecUploadMgr = ((SelectionRecUploadMgr) ServiceLocator.getBean("selRecUploadMgr"));
			selectionRecUploadMgr.setDataSource(dataSource);
			selectionRecUploadMgr.prepareListItemData();

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		}

	}

}
