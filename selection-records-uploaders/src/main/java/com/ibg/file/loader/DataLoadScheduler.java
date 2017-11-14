package com.ibg.file.loader;

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

import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.data.uploaders.PreLoadCleaner;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;
import com.ibg.utils.PropertyReader;

public class DataLoadScheduler extends LoaderScheduler {

	private Map<String, LoadFileDetails> files;
	private FileWriter writer = null;
	private BufferedWriter bufWriter = null;
	private String processingCustGrp;
	private Map<String, Connection> connection = new HashMap<String, Connection>();

	protected void startLoadProcess() {
		DoneFileCollector doneFiles = new DoneFileCollector();

		FileCollector fileCollectors = new FileCollector(doneFiles.getDoneFiles());
		files = fileCollectors.getDoneFiles();

		scheduleLoads();
	}

	private void scheduleLoads() {

		DataSource preLoadCleanDataSource = null;
		DataSource dataSource = null;
		DataSource dataSourceJson = null;

		boolean preLoadCleanSuccess = false;
		if (null != files) {

			try {

				Calendar cldr = new GregorianCalendar();
				String endTime = cldr.get(Calendar.YEAR) + "" + (cldr.get(Calendar.MONTH) + 1) + "" + cldr.get(Calendar.DAY_OF_MONTH) + ""
						+ cldr.get(Calendar.HOUR_OF_DAY) + "" + cldr.get(Calendar.MINUTE);

				String logFileName = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getLoadStatusLogs() + "load-status-" + endTime
						+ ".log";

				for (String key : files.keySet()) {
					if (null != files.get(key).getFlatFileSet() && null != files.get(key).getJsonFileSet()) {

						preLoadCleanSuccess = false;

						processingCustGrp = files.get(key).getDataProcGrp();
						processingCustGrp = processingCustGrp.substring(processingCustGrp.lastIndexOf('_') + 1);

						writer = new FileWriter(new File(logFileName), true);
						bufWriter = new BufferedWriter(writer);

						System.out.println("----------------------------------------------------");
						System.out.printf("processing starts for: %s at %s\n", files.get(key).getDataProcGrp(), new Date());

						bufWriter.write("----------------------------------------------------\n");
						bufWriter.write("processing starts for: " + files.get(key).getDataProcGrp() + " at " + cldr.getTime().toString() + "\n");

						// clean up old existing data for this customer group
						try {
							preLoadCleanSuccess = new PreLoadCleaner().cleanOldUserData(processingCustGrp);

							if (preLoadCleanSuccess) {
								// flat file uploaders
								dataSource = createDBConn("dbEnv");
								startFlatFileLoadProcess(files.get(key).getFlatFileSet(), dataSource);
								dataSource = closeDBConn("dbEnv");

								// json file uploader
								dataSourceJson = createDBConn("dbEnv");
								startJsonLoadProcess(files.get(key).getJsonFileSet(), dataSourceJson);
								dataSourceJson = closeDBConn("dbEnv");

								System.out.printf("processing ends for: %s at %s\n", processingCustGrp, new Date());
								System.out.println("----------------------------------------------------");
								bufWriter.write("processing ends for: " + processingCustGrp + " at " + cldr.getTime().toString() + "\n");
								bufWriter.write("----------------------------------------------------\n");
							} else {
								System.out.printf("Failing load for: %s at %s\n", processingCustGrp, new Date());
								System.out.println("----------------------------------------------------");
								bufWriter.write("Failing load for: " + processingCustGrp + " at " + cldr.getTime().toString()
										+ " as preloadceanup failed\n");
								bufWriter.write("----------------------------------------------------\n");
							}

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

					}// if

				}// for

			} catch (IOException e) {
				System.out.println("Error from DataLoadScheduler: " + e);
				e.printStackTrace();
			}
		}
	}

	protected void startFlatFileLoadProcess(Set<File> flatFiles, DataSource dataSource) {
		FlatLoaderScheduler flatLoader = new FlatLoaderScheduler();
		flatLoader.setCustGrpName(processingCustGrp);
		flatLoader.startLoadProcess(flatFiles, dataSource);
	}

	protected void startJsonLoadProcess(Set<File> jsonDataFiles, DataSource dataSource) {

		new UploadMiscLogs();
		JsonLoaderScheduler jsonLoader = new JsonLoaderScheduler();
		jsonLoader.setCustGrpName(processingCustGrp);
		jsonLoader.startLoadProcess(jsonDataFiles, dataSource);
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
		new DataLoadScheduler().startLoadProcess();
	}

}
