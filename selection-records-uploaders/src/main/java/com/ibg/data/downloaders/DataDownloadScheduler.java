package com.ibg.data.downloaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.javalite.activejdbc.Base;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;
import com.ibg.file.loader.JsonLoaderScheduler;
import com.ibg.utils.PropertyReader;

public class DataDownloadScheduler {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	private FileWriter writer = null;
	private BufferedWriter bufWriter = null;
	private String processingCustGrp;
	private Map<String, Connection> connection = new HashMap<String, Connection>();

	protected void startLoadProcess() {

		scheduleLoads();
	}

	private void scheduleLoads() {

		DataSource dataSource = null;
		DataSource dataSourceJson = null;

		try {

			Calendar cldr = new GregorianCalendar();
			String endTime = cldr.get(Calendar.YEAR) + "" + (cldr.get(Calendar.MONTH) + 1) + "" + cldr.get(Calendar.DAY_OF_MONTH) + ""
					+ cldr.get(Calendar.HOUR_OF_DAY) + "" + cldr.get(Calendar.MINUTE);

			String logFileName = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getLoadStatusLogs() + "load-status-" + endTime + ".log";

			writer = new FileWriter(new File(logFileName), true);
			bufWriter = new BufferedWriter(writer);

			System.out.println("----------------------------------------------------");

			bufWriter.write("----------------------------------------------------\n");

			// clean up old existing data for this customer group
			try {

				// flat file uploaders
				dataSource = createDBConn("dbEnv");
				startFlatFileLoadProcess(dataSource);
				dataSource = closeDBConn("dbEnv");

				System.out.printf("processing ends for: %s at %s\n", processingCustGrp, new Date());
				System.out.println("----------------------------------------------------");
				bufWriter.write("processing ends for: " + processingCustGrp + " at " + cldr.getTime().toString() + "\n");
				bufWriter.write("----------------------------------------------------\n");

				bufWriter.close();
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();

				if (null != connection) {
					closeDBConn("dbEnv");
					closeDBConn("dbEnvJson");
				}
				if (null != dataSource) {
					dataSource = null;
				}

			} finally {
				if (null != connection) {
					closeDBConn("dbEnv");
					closeDBConn("dbEnvJson");
				}
				if (null != dataSource) {
					dataSource = null;
				}

			}

		} catch (IOException e) {
			System.out.println("Error from DataDownloadScheduler: " + e);
			e.printStackTrace();
		}
	}

	protected void startFlatFileLoadProcess(DataSource dataSource) {
		FlatDownloadScheduler flatLoader = new FlatDownloadScheduler();
		flatLoader.startLoadProcess(dataSource);
	}

	private DataSource createDBConn(String dbEnv) {

		DataSource dataSource = null;

		try {

			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
			String loadEnv = prop.getLoadDbEnv();

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean(dbEnv));
			connection.put(dbEnv, env.getConn(loadEnv));

			dataSource = env.getDataSource(loadEnv);

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.get(dbEnv).close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return dataSource;
	}

	private DataSource closeDBConn(String dbEnv) {

		try {
			if (null != connection.get(dbEnv)) {
				connection.get(dbEnv).close();
			}

			if (Base.hasConnection()) {
				Base.close();
			}

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (null != connection.get(dbEnv)) {
					connection.get(dbEnv).close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static void main(String[] args) {
		new DataDownloadScheduler().startLoadProcess();
	}

}
