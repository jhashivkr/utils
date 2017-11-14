package com.ibg.file.loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.ibg.data.uploaders.AcdmDataUploaders;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;
import com.ibg.parsers.flat.FlatFileParser;
import com.ibg.utils.FlatFileProps;
import com.ibg.utils.PropertyReader;

public class FlatLoaderScheduler {

	private Set<File> flatDataFiles;
	private PropertyReader prop;
	private String custGrpName;

	public void setFlatDataFiles(Set<File> flatDataFiles) {
		this.flatDataFiles = flatDataFiles;
	}

	public void setCustGrpName(String custGrpName) {
		this.custGrpName = custGrpName;
	}

	public void startLoadProcess() {

		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
		String processSeq = prop.getFileProcessSeq();
		Map<String, File> flatFiles = new LinkedHashMap<String, File>();
		arrangeFlatFilesInProcessingOrder(processSeq, flatDataFiles, flatFiles);

		startFlatFileLoadProcess(flatFiles);
	}

	public void startLoadProcess(Set<File> flatDataFiles, DataSource dataSource) {

		this.flatDataFiles = flatDataFiles;
		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
		String processSeq = prop.getFileProcessSeq();
		Map<String, File> flatFiles = new LinkedHashMap<String, File>();
		arrangeFlatFilesInProcessingOrder(processSeq, flatDataFiles, flatFiles);

		startSelectiveFlatFileLoadProcess(flatFiles, dataSource);
	}

	private void startSelectiveFlatFileLoadProcess(Map<String, File> flatFiles, DataSource dataSource) {

		try {
			loadData(flatFiles.get("_sfcontact").getAbsolutePath(), "_sfcontact", dataSource);
			loadData(flatFiles.get("_arlg").getAbsolutePath(), "_arlg", dataSource);
			loadData(flatFiles.get("_arcust").getAbsolutePath(), "_arcust", dataSource);
			loadData(flatFiles.get("_opaplst").getAbsolutePath(), "_opaplst", dataSource);
			loadData(flatFiles.get("_pref").getAbsolutePath(), "_pref", dataSource);
			loadData(flatFiles.get("_orderinfo").getAbsolutePath(), "_orderinfo", dataSource);
		} catch (Exception e) {
			// do nothing - just swallow the exception
		}

	}

	private void startFlatFileLoadProcess(Map<String, File> flatFiles) {

		// start loading the file
		for (String key : flatFiles.keySet()) {
			loadData(flatFiles.get(key).getAbsolutePath(), key);
		}

	}

	private void loadData(String file, String obj, DataSource dataSource) {

		BufferedWriter bufWriter = null;
		StringBuilder bldr = new StringBuilder();

		try {

			Calendar cldr = new GregorianCalendar();

			bldr.append(prop.getFileParserLog()).append(custGrpName).append('/').append(custGrpName).append(obj).append('-');
			bldr.append(cldr.get(Calendar.YEAR)).append((cldr.get(Calendar.MONTH) + 1)).append(cldr.get(Calendar.DAY_OF_MONTH)).append(
					cldr.get(Calendar.HOUR_OF_DAY)).append(cldr.get(Calendar.MINUTE));
			bldr.append(".log");

			File logFile = new File(bldr.toString());

			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}

			bufWriter = new BufferedWriter(new FileWriter(logFile));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			String[] dataFlds = flatFileProps.getFields(obj).split("\\,");
			List<String> dataFields = Arrays.asList(dataFlds);

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(dataFields);
			parser.setBufferWriter(bufWriter);

			// get the file data
			List<Map<String, String>> data = parser.getData();

			// get the particular bean reference and load
			// the data in the table
			if (null != ServiceLocator.getBean(obj)) {
				AcdmDataUploaders loader = (AcdmDataUploaders) ServiceLocator.getBean(obj);
				loader.setData(data);
				loader.setPropertyReader(prop);
				loader.setBufferWriter(bufWriter);
				loader.setDataSource(dataSource);
				loader.startDataUpload(dataFlds);

			}

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (bufWriter != null) {
					bufWriter.close();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void loadData(String file, String obj) {

		BufferedWriter bufWriter = null;
		Connection connection = null;
		DataSource dataSource = null;
		try {

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			connection = env.getConn("stage");
			dataSource = env.getDataSource("stage");

			Calendar cldr = new GregorianCalendar();
			String timeStamp = cldr.get(Calendar.YEAR) + "" + (cldr.get(Calendar.MONTH) + 1) + "" + cldr.get(Calendar.DAY_OF_MONTH) + ""
					+ cldr.get(Calendar.HOUR_OF_DAY) + "" + cldr.get(Calendar.MINUTE);

			String logFileName = prop.getFileParserLog() + obj + "-" + timeStamp + ".log";
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			String[] dataFlds = flatFileProps.getFields(obj).split("\\,");
			List<String> dataFields = Arrays.asList(dataFlds);

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(dataFields);
			parser.setBufferWriter(bufWriter);

			List<Map<String, String>> data = parser.getData();
			for (Map<String, String> lst : data) {
				for (String key : lst.keySet()) {
					System.out.println("key:value =>" + key + ":" + lst.get(key));
				}
			}

			if (null != ServiceLocator.getBean(obj)) {
				AcdmDataUploaders loader = (AcdmDataUploaders) ServiceLocator.getBean(obj);
				loader.setData(data);
				loader.setPropertyReader(prop);
				loader.setBufferWriter(bufWriter);
				loader.setDataSource(dataSource);
				loader.startDataUpload(dataFlds);

			}

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		} finally {
			try {
				if (bufWriter != null) {
					bufWriter.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void arrangeFlatFilesInProcessingOrder(String processingSeq, Set<File> files, Map<String, File> flatFiles) {

		if (null != files && !files.isEmpty()) {
			Iterator<File> setItr = null;

			List<String> seqList = Arrays.asList(processingSeq.split("\\,"));
			Pattern fileNamePattern = null;

			for (String filePat : seqList) {
				fileNamePattern = Pattern.compile("\\s*" + filePat + "\\s*");

				setItr = files.iterator();

				for (; setItr.hasNext();) {
					File file = setItr.next();
					if (fileNamePattern.matcher(file.getName()).find()) {
						flatFiles.put(filePat, file);
						setItr.remove();
						break;
					}
				}

			}

		}
	}

}
