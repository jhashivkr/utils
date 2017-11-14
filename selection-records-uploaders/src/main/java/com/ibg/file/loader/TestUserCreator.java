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

public class TestUserCreator {

	private Set<File> flatDataFiles;
	private PropertyReader prop;

	public void setFlatDataFiles(Set<File> flatDataFiles) {
		this.flatDataFiles = flatDataFiles;
	}

	public void startLoadProcess() {

		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
		String processSeq = prop.getFileProcessSeq();
		Map<String, File> flatFiles = new LinkedHashMap<String, File>();
		arrangeFlatFilesInProcessingOrder(processSeq, flatDataFiles, flatFiles);

		startFlatFileLoadProcess(flatFiles);
	}

	private void startFlatFileLoadProcess(Map<String, File> flatFiles) {

		// _arlg,_arcust,_sfcontact,_pref,_opaplst,_excel,_orderinfo
		loadData(flatFiles.get("_arlg").getAbsolutePath(), "_arlg");
		loadData(flatFiles.get("_arcust").getAbsolutePath(), "_arcust");
		loadData(flatFiles.get("_sfcontact").getAbsolutePath(), "_sfcontact");
		loadData(flatFiles.get("_opaplst").getAbsolutePath(), "_opaplst");

	}

	private void loadData(String file, String obj) {

		BufferedWriter bufWriter = null;
		Connection connection = null;
		DataSource dataSource = null;
		try {

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			connection = env.getConn("dev");
			dataSource = env.getDataSource("dev");

			Calendar cldr = new GregorianCalendar();
			String timeStamp = cldr.get(Calendar.YEAR) + "" + (cldr.get(Calendar.MONTH) + 1) + "" + cldr.get(Calendar.DAY_OF_MONTH) + ""
					+ cldr.get(Calendar.HOUR_OF_DAY) + "" + cldr.get(Calendar.MINUTE);

			String logFileName = prop.getFileParserLog() + obj + "-" + timeStamp + ".log";
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			String [] dataFlds = flatFileProps.getFields(obj).split("\\,");
			List<String> dataFields = Arrays.asList(dataFlds);

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(dataFields);
			parser.setBufferWriter(bufWriter);

			List<Map<String, String>> data = parser.getData();

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
