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

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.data.uploaders.AcdmDataUploaders;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;
import com.ibg.parsers.flat.FlatFileParser;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.FlatFileProps;
import com.ibg.utils.PropertyReader;
import com.ibg.utils.TxtFileFilter;

public class FlatFileLoaderTester {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	private Set<File> flatDataFiles;
	private PropertyReader prop;

	public void startLoadProcess() {

		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
		CollectFiles<TxtFileFilter> dataFiles = new CollectFiles<TxtFileFilter>();
		dataFiles.getAllDataFiles();
		flatDataFiles = dataFiles.getAllFlatFile();
		
		System.out.println("flatDataFiles: " + flatDataFiles);

		String processSeq = prop.getFileProcessSeq();
		Map<String, File> flatFiles = new LinkedHashMap<String, File>();
		arrangeFlatFilesInProcessingOrder(processSeq, flatDataFiles, flatFiles);
		
		System.out.println("path: " + flatFiles.get("_sfcontact").getAbsolutePath());
		try {
			
			// _arlg,_arcust,_sfcontact,_pref,_opaplst,_excel,_orderinfo
			//loadData(flatFiles.get("_arlg").getAbsolutePath(), "_arlg");
			// loadData(flatFiles.get("_arcust").getAbsolutePath(), "_arcust");
			loadData(flatFiles.get("_sfcontact").getAbsolutePath(), "_sfcontact");
			//loadData(flatFiles.get("_opaplst").getAbsolutePath(), "_opaplst");
		
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	private void loadData(String file, String obj) {

		BufferedWriter bufWriter = null;

		try {
			
			String logFileName = prop.getFileParserLog() + obj + "-.log";
			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
			String loadEnv = prop.getLoadDbEnv();
			System.out.println("loadEnv: " + loadEnv);
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));
			
			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields(obj).split("\\,"));

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(flds);
			parser.setBufferWriter(bufWriter);

			List<Map<String, String>> data = parser.getData();
			
			// print the data
			for (Map<String, String> rowData : data) {
				
				String admin = rowData.get("ADMINISTRATOR");
				
				if ("true".equals(admin)){
					System.out.println("COUTTSUSERID:admin=>" + rowData.get("COUTTSUSERID") + ":" + admin + "=>" + ("true".equals(admin) ? "true" : "false"));
				}
				//for (String dataKey : rowData.keySet()) {
				//	System.out.printf(dataKey + ":" + rowData.get(dataKey) + ", ");					
				//}
				System.out.println("");
			}

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
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

	public static void main(String[] args) {
		new FlatFileLoaderTester().startLoadProcess();
	}

}
