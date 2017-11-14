package com.cc.log.analyze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import com.cc.log.obj.LogMqData;

public class LogReader implements Runnable {

	private int writeCtr;
	private int nullResponses;
	private String[] logFiles;

	public void run() {
		
		Constants.changedKeys = new LinkedHashMap<>();
		for (String file : logFiles) {

			readFile(file);

			Map<Object, LogMqData> logData = RequestService.getLog();
			for (Object key : logData.keySet()) {
				// System.out.println(key + "=>" + logData.get(key));

				// if (null != key && null != lines.get(key) && null !=
				// lines.get(key).get("LoggingException")) {
				// if(!lines.get(key).get("MessageObject").contains("PreAuth")){
				// System.out.println(key + "=>" +
				// lines.get(key).get("LoggingException"));
				// readLogs.writeToResponseQueue(lines.get(key));
				// ctr++;
				// }
				// }
				
				//if(!logData.get(key).isDbLoggingStatus()){
				//	System.out.println(key + "=>" + logData.get(key));
				//}

			}
			System.out.println("size of logs: " + logData.keySet().size());
			// System.out.println("size of non-preauths: " + ctr);
			// System.out.println("all keys: " + lines.keySet());
			// readLogs.writeTiebacksToFile("1028Tiebacks.txt");
			// readLogs.writeLogDataTiebacksToFile("1028LogTiebacks.txt",
			// logData);
			analyzeKeys(logData);
			
			writeToFile();
		}
	}

	private boolean isMatchingKey(String line, String thisKey){
		LogMqData logMqData = RequestService.getLogContent(thisKey);
		if(null == logMqData.getTieBack()){
			//if(line.contains("DD201829955R46  DAAC") && line.contains("pool-6-thread-91")){
			//	System.out.println("returning:line: " + line);
			//	System.out.println("returning:key: " + thisKey);
			//}
			return true;
		}					
		else if(null != logMqData.getTieBack() && line.contains(logMqData.getTieBack())){						
			//if(line.contains("DD201829955R46  DAAC") && line.contains("pool-6-thread-91")){
			//	System.out.println("returning:line - 1: " + line);
			//	System.out.println("returning:key - 1: " + thisKey);
				//System.out.println("logMqData: " + logMqData);
			//}
			return true;
		}
		return false;
	}
	private String decideKey(String possibleKeyTmstmp, String possibleKeyStr, String line) {

		// conditional processing - remove in live scenario
		Calendar calendar = Calendar.getInstance();

		int ctr = 1;
		String thisKey = possibleKeyTmstmp + "-" + possibleKeyStr;

		if (RequestService.isLogEmpty()) {
			return thisKey;
		}

		if (line.contains("CyberSource Call Starts.")) {			
			
			// check if this key already exist in the dataset
			if (RequestService.doesContains(thisKey)) {
				// remove this key existing data into logData1 dataset
				LogMqData tmp = RequestService.getLogContent(thisKey);
				// change the key with appending tieback to it
				String newKey = tmp.getKey() + "|" + tmp.getTieBack();
				RequestService.removeContent(thisKey);

				RequestService.addLog(newKey, tmp);
				Constants.changedKeys.put(thisKey, newKey);
				
				//if(line.contains("2015-11-03 23:08:03")){
				//	System.out.println("thisKey, newKey" + thisKey + ", " + newKey);
				//}
				
				//System.out.println("tmp:" + newKey + "=>" + tmp);
			}

			
			return thisKey;
		} else if (RequestService.doesContains(thisKey)) {
			
			if(isMatchingKey(line, thisKey)){
				return thisKey;
			}
				
		}

		// for the boundary cases
		// start setting this possible time stamp by 1 sec and search for an
		// existing key in the set
		try {
			calendar.setTime(Constants.df.parse(possibleKeyTmstmp));

			for (; ctr < 60; ctr++) {
				calendar.setTimeInMillis(calendar.getTimeInMillis() - 1000);
				thisKey = Constants.df.format(calendar.getTime()) + "-" + possibleKeyStr;

				if (RequestService.doesContains(thisKey)) {
					

					if(isMatchingKey(line, thisKey)){
						return thisKey;
					}
					
					
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		thisKey = possibleKeyTmstmp + "-" + possibleKeyStr;
		//if(line.contains("DD201829955R46  DAAC") && line.contains("pool-6-thread-91")){
		//	System.out.println("returning - 1: " + thisKey);
		//}
		return thisKey;

	}

	public void readFile(String name) {
		try {
			Path path = FileSystems.getDefault().getPath(Constants.logFilePath, name);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			String key = "";

			Constants.keyTiebacks = new LinkedHashMap<>();			

			while ((line = reader.readLine()) != null) {
				Matcher matcher = Constants.data_pattern.matcher(line);
				if (matcher.find()) {
					key = decideKey(line.substring(0, 19), matcher.group(0), line);

					//if(line.contains("DD201829955R46  DAAC") && line.contains("pool-6-thread-91")){
					//	System.out.println("line: " + line);
					//	System.out.println("key: " + key);
					//}
					LogMqData logMqData = RequestService.getLogContent(key);
					if(null == logMqData){
						logMqData = new LogMqData();
						logMqData.setKey(key);
						logMqData.setTimeStamp(line.substring(0, 23));
						RequestService.addLog(key, logMqData);
					}	
					
					if (!line.contains("CyberSource Call Starts.")) {				
						
						analyzeFileLines(key, line, matcher.group(0), logMqData);
						RequestService.addLog(key, logMqData);
					}
					
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	public void analyzeFileLines(String key, String line, String thread, LogMqData logMqData) {
		try {

			if (line.contains("Request object=>")) {
				captureRequestObject(key, line, thread, logMqData);
			} else if (line.contains("CyberSource Call.ErrorList=>")) {
				captureCSCallError(key, line, thread, logMqData);
			} else if (line.contains("CyberSource Call.Status=>")) {
				captureCSStatus(key, line, thread, logMqData);
			} else if (line.contains("Response Type|Message=>")) {
				captureResponseType(key, line, thread, logMqData);
			}
			if (line.contains("Writing Response Done")) {
				captureResponseWriteStatus(key, line, thread, logMqData);
			}
			if (line.contains("Db Logging Done")) {
				captureDbLogStatus(key, line, thread, logMqData);
			}
			if (line.contains("INSERT INTO EB.CREDIT_CARD_TRANSACTION_HIST")) {
				captureDbLogErrors(key, line, thread, logMqData);
			}

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}

	private void captureRequestObject(String key, String line, String thread, LogMqData logMqData) {
		try {
			String[] req = line.split("=>");
			req = req[1].split("\\|");

			String tieback = Utilities.getTieback(line);

			logMqData.setCorrelId(req[0]);
			logMqData.setRequestObj(req[1]);
			logMqData.setTieBack(tieback);

			if (!Constants.keyTiebacks.containsKey(tieback)) {
				Constants.keyTiebacks.put(tieback, key);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void captureCSCallError(String key, String line, String thread, LogMqData logMqData) {
		try {
			logMqData.setCsError((line.split("=>"))[1]);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void captureCSStatus(String key, String line, String thread, LogMqData logMqData) {
		try {
			logMqData.setCsStatus((line.split("=>"))[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void captureResponseType(String key, String line, String thread, LogMqData logMqData) {
		try {
			logMqData.setResponseType((line.split("=>"))[1]);
			if("1".equalsIgnoreCase(logMqData.getResponseType())){
				logMqData.setTranType("PreAuth");
			}else{
				logMqData.setTranType("Settlement");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void captureDbLogErrors(String key, String line, String thread, LogMqData logMqData) {

		try {
			
			logMqData.setDbErrorLog(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void captureResponseWriteStatus(String key, String line, String thread, LogMqData logMqData) {

		try {
			logMqData.setResponseSentStatus(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void captureDbLogStatus(String key, String line, String thread, LogMqData logMqData) {
		try {
			
			if(null != logMqData.getTieBack() && line.contains(logMqData.getTieBack())){
				logMqData.setDbLoggingStatus(true);
			}else{			
				if(Constants.changedKeys.containsKey(key)){
					LogMqData logMqDataTmp = RequestService.getLogContent(Constants.changedKeys.get(key));
					if(line.contains(logMqDataTmp.getTieBack())){
						logMqDataTmp.setDbLoggingStatus(true);
						logMqData = logMqDataTmp;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void analyzeKeys(Map<Object, LogMqData> logData) {
		
		System.out.println("Analyzing lost data possibility starts");
		Set<Object> keySet = logData.keySet();
		Set<String> allTiebacks = Constants.keyTiebacks.keySet();

		Set<String> allLogTiebacks = new LinkedHashSet<>();

		for (Object key : keySet) {
			allLogTiebacks.add(logData.get(key).getTieBack());
		}
		for (String tieback : allTiebacks) {
			if (!allLogTiebacks.contains(tieback)) {
				System.out.println(tieback + "," + Constants.keyTiebacks.get(tieback));
				// System.out.println(tieback + "=>" +
				// Constants.logData.get(Constants.keyTiebacks.get(tieback)));
			}
		}
		
		System.out.println("Analyzing lost data possibility ends");
	}

	/**
	 * @return the writeCtr
	 */
	public int getWriteCtr() {
		return writeCtr;
	}

	/**
	 * @param writeCtr
	 *            the writeCtr to set
	 */
	public void setWriteCtr(int writeCtr) {
		this.writeCtr = writeCtr;
	}

	/**
	 * @return the nullResponses
	 */
	public int getNullResponses() {
		return nullResponses;
	}

	/**
	 * @param nullResponses
	 *            the nullResponses to set
	 */
	public void setNullResponses(int nullResponses) {
		this.nullResponses = nullResponses;
	}

	/**
	 * @return the logFiles
	 */
	public String[] getLogFiles() {
		return logFiles;
	}

	/**
	 * @param logFiles
	 *            the logFiles to set
	 */
	public void setLogFiles(String[] logFiles) {
		this.logFiles = logFiles;
	}
	
	private void writeToFile() {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", "11-11-Non-PreAuth-data.txt"),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			
			Map<Object, LogMqData> logData = RequestService.getLog();

			String content = "";
			for (Object key : logData.keySet()) {
				
				if(!logData.get(key).getRequestObj().contains("PreAuth")){
					content = logData.get(key).toString() + '\n';
					writer.write(content, 0, content.length());
				}
				
			}
			
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
