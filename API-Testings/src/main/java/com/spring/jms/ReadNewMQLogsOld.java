package com.spring.jms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cc.log.obj.LogMqData;

import ibg.cc.mq.message.RequestMessage;
import ibg.cc.mq.message.ResponseMessage;
import ibg.cc.mq.writers.MessageWriter;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.cybersource.emsqueue.EMSCybersourceService;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.cybersource.creditcard.constants.CyberSourceConstants;
import ibg.cybersource.service.CreditCardServiceLocator;

public class ReadNewMQLogsOld {

	private int ctr;
	private int writeCtr;
	private int nullResponses;

	public int countValidLines(String fileName) {
		int lineCtr = 0;
		try {
			Path path = FileSystems.getDefault().getPath(constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.request_obj_pattern.matcher(line);
				if (matcher.find()) {
					// conditional processing - remove in live scenario
					Calendar calendar = Calendar.getInstance();
					try {
						calendar.setTime(constants.df.parse(line.substring(0, 19)));
						// if (calendar.get(Calendar.HOUR_OF_DAY) >= 22) {

						CreditCardInfo ccInfo = createCreditCardInfo((line.split("=>"))[1]);
						// ccInfo.setRequest(ByteBuffer.wrap((line.split("=>"))[1].getBytes()));
						if (null != ccInfo) {

							String tranType = ccInfo.getRequestField(MQReqField.TranType);
							constants.tranTypes.add(tranType);
							if (!CyberSourceConstants.CC_PREAUTH.equalsIgnoreCase(tranType)) {
								LogTiebacks logTiebacks = new LogTiebacks();
								logTiebacks.setTieBack(ccInfo.getRequestField(MQReqField.TieBack));
								logTiebacks.setTimeStamp(line.substring(0, 23));
								logTiebacks.setTranType(tranType);
								lineCtr++;

								constants.dataList.add(logTiebacks);

								// try {
								// System.out.println("line: " + line);
								// System.out.println("ccInfo: " + new
								// String(ccInfo.getRequest().array(),
								// java.nio.charset.StandardCharsets.UTF_8));
								// System.out.println(ccInfo.getRequestField(MQReqField.Card_Bill_To)
								// + "," +
								// ccInfo.getRequestField(MQReqField.Merch_Bill_To)
								// + ", " +
								// ccInfo.getRequestField(MQReqField.Division));
								// CreditCardCustomerProfileService
								// creditCardCustomerProfileSrvc =
								// (CreditCardCustomerProfileService)
								// CreditCardServiceLocator
								// .getService(CreditCardServiceLocator.ServiceName.CRECRD_CUSTOMER_PROFILE_SERVICE);
								// Map<String, String> subsIdMap =
								// creditCardCustomerProfileSrvc.getSubscriptionIDInfo(ccInfo);
								// if(null != subsIdMap &&
								// !subsIdMap.isEmpty()){
								// System.out.println("subsIdMap: " +
								// subsIdMap);
								// }

								// } catch (Exception e) {
								// e.printStackTrace();
								// }

							}
						}

						// }
					} catch (ParseException e1) {
						e1.printStackTrace();
					}

				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return lineCtr;
	}

	public int countBuildObjects(String fileName) {
		int lineCtr = 0;
		try {
			Path path = FileSystems.getDefault().getPath(constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			constants.keyTiebacks = new LinkedHashMap<>();
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.request_obj_pattern.matcher(line);
				if (matcher.find()) {
					String req = (line.split("=>"))[1].trim();
					req = req.split("\\|")[1].trim();
					if (null != req && !req.isEmpty()) {

						String tranType = req.substring(22, 32).trim();
						String tieBack = req.substring(2, 22).trim();
						constants.tranTypes.add(tranType);
						boolean proceed = true;
						//boolean proceed = CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_SALE.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_CREDIT.equalsIgnoreCase(tranType);
						//boolean proceed = CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType);
						if (proceed) {

							LogMqData logMqData = new LogMqData();
							logMqData.setTieBack(tieBack);
							logMqData.setTimeStamp(line.substring(0, 23));
							logMqData.setTranType(tranType);
							logMqData.setRequestObj(req);
							
							String key = "";
							Matcher matcher1 = constants.data_pattern.matcher(line);
							if (matcher1.find()) {
								key = line.substring(0, 19) + "-" + matcher1.group(0);
								logMqData.setKey(key);
							}
							lineCtr++;
							
							String tieback = getTieback(line);
							if(!constants.keyTiebacks.containsKey(tieback)){
								constants.keyTiebacks.put(tieback, key);
							}

							constants.mqDataList.add(logMqData);

						}
					}

				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return lineCtr;
	}

	
	public int countLoggingErrors(String fileName) {
		int lineCtr = 0;
		try {
			Path path = FileSystems.getDefault().getPath(constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.db_log_done_data_pattern.matcher(line);
				if (matcher.find()) {
					String tieBack = line.split("\\|")[0].trim();

					if (null != tieBack && !tieBack.isEmpty()) {

						LogMqData logMqData = new LogMqData();
						logMqData.setTieBack(tieBack);
						logMqData.setTimeStamp(line.substring(0, 23));
						Matcher matcher1 = constants.data_pattern.matcher(line);
						if (matcher1.find()) {
							String key = line.substring(0, 19) + "-" + matcher1.group(0);
							logMqData.setKey(key);
						}
						lineCtr++;

						constants.mqDataList.add(logMqData);

					}

				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return lineCtr;
	}
	public int countCyberSourceResponses(String line) {
		int lineCtr = 0;

		while (null != line) {
			Matcher matcher = constants.cs_call_stat_data_pattern.matcher(line);
			if (matcher.find()) {
				String req = (line.split("=>"))[1].trim();

				// constants.mqCsResponses.put(key, value)

			}
		}

		return lineCtr;
	}

	private String decideKey(String possibleKeyTmstmp, String possibleKeyStr, String line) {

		// conditional processing - remove in live scenario
		Calendar calendar = Calendar.getInstance();

		int ctr = 1;
		String thisKey = possibleKeyTmstmp + "-" + possibleKeyStr;

		if(null  == constants.logData || constants.logData.isEmpty()){
			return thisKey;
		}
		
		if (null == constants.logData.keySet() || constants.logData.keySet().isEmpty()) {
			return thisKey;
		} else if (constants.logData.keySet().contains(thisKey)) {
			return thisKey;
		}
		
		if(line.contains("CyberSource Call Starts.")){
			return thisKey;
		}
		//check if this has the tieback
		//String tieback = getTieback(line);
		//if(null != tieback && !tieback.isEmpty()){
		//	if(constants.keyTiebacks.containsKey(tieback)){
		//		return constants.keyTiebacks.get(tieback);
		//	}
		//}
		

		// for the boundary cases
		// start setting this possible time stamp by 1 sec and search for an
		// existing key in the set
		try {
			calendar.setTime(constants.df.parse(possibleKeyTmstmp));

			for (; ctr < 5; ctr++) {
				calendar.setTimeInMillis(calendar.getTimeInMillis() - 1000);
				thisKey = constants.df.format(calendar.getTime()) + "-" + possibleKeyStr;

				if (constants.logData.keySet().contains(thisKey)) {
					
					
					return thisKey;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		thisKey = possibleKeyTmstmp + "-" + possibleKeyStr;
		return thisKey;

	}
	
	public void readFile(String name) {
		try {
			Path path = FileSystems.getDefault().getPath(constants.logFilePath, name);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			String key = "";
			
			constants.lines = new LinkedHashMap<>();
			constants.dbLogErrorlines = new LinkedHashMap<>();
			
			constants.logData = new LinkedHashMap<>();
			constants.keyTiebacks = new LinkedHashMap<>();
			constants.threadPoolStatus = new LinkedHashMap<>();
			
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.data_pattern.matcher(line);
				if (matcher.find()) {
					key = decideKey(line.substring(0, 19), matcher.group(0), line);
					
					
					
					if(!constants.logData.containsKey(key)){
						LogMqData logMqData = new LogMqData();
						logMqData.setKey(key);
						logMqData.setTimeStamp(line.substring(0, 23));
						constants.logData.put(key, logMqData);
					}
					analyzeFileLines(key, line);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
	
	public void analyzeFileLines(String key, String line) {
		try {

			if (line.contains("Request object=>")) {
				captureRequestObject(key, line);
				
			}

			else if (line.contains("CyberSource Call.ErrorList=>")) {
				captureCSCallError(key, line);

			}

			else if (line.contains("CyberSource Call.Status=>")) {
				captureCSStatus(key, line);
			}

			else if (line.contains("Response Type|Message=>")) {
				captureResponseType(key, line);
			}

			if (line.contains("Writing Response Done")) {
				captureResponseWriteStatus(key, line);
			}

			if (line.contains("Db Logging Done")) {
				captureDbLogStatus(key, line);
			}

			if (line.contains("INSERT INTO EB.CREDIT_CARD_TRANSACTION_HIST")) {
				captureDbLogErrors(key, line);
			}

		} catch (Exception ioe) {
			ioe.printStackTrace();
		}

	}
	
	private void captureRequestObject(String key, String line){
		try {
			String [] req = line.split("=>");
			req = req[1].split("\\|");
			
			String tieback = getTieback(line);
			constants.logData.get(key).setCorrelId(req[0]);
			constants.logData.get(key).setRequestObj(req[1]);
			constants.logData.get(key).setTieBack(tieback);
						
			if(!constants.keyTiebacks.containsKey(tieback)){
				constants.keyTiebacks.put(tieback, key);
			}
			
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	private void captureCSCallError(String key, String line){
		try {
			constants.logData.get(key).setCsError((line.split("=>"))[1]);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	private void captureCSStatus(String key, String line){
		try {
			constants.logData.get(key).setCsStatus((line.split("=>"))[1]);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	private void captureResponseType(String key, String line){
		try {
			constants.logData.get(key).setResponseType((line.split("=>"))[1]);
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	private void captureDbLogErrors(String key, String line){
		
		//Matcher matcher = constants.db_log_error_data_pattern.matcher(line);
		//if (matcher.find()) {
		
		try {
			constants.logData.get(key).setDbErrorLog(line);
		} catch (Exception e) {
			 e.printStackTrace();
		}		
	}
	
	
	private void captureResponseWriteStatus(String key, String line){

		try {
			constants.logData.get(key).setResponseSentStatus(true);
		} catch (Exception e) {
			 e.printStackTrace();
		}	
		
	}
	
	private void captureDbLogStatus(String key, String line){
		try {
			constants.logData.get(key).setDbLoggingStatus(true);
		} catch (Exception e) {
			 e.printStackTrace();
		}		
	}
	
	private void collectData(String key, String line, Map<String, Map<String, String>> lines, boolean find, String dataGroup){
		try {
			lines.get(key).put(dataGroup, (line.split("=>"))[1]);
		} catch (Exception e) {
			 e.printStackTrace();
			if (find) {
				Map<String, String> lineData = new HashMap<>();
				lineData.put(dataGroup, (line.split("=>"))[1]);
				lines.put(key, lineData);
			}
		}
	}
	
	private void writeFileBytesBuffered(String filename, Map<String, Map<String, String>> lines) {

		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ingram\\1023-logs\\mqdata\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (String key : lines.keySet()) {
				content = key + " => " + lines.get(key) + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void writeToFile(String filename, List<LogTiebacks> lines) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (LogTiebacks data : lines) {
				content = data.getTimeStamp() + '|' + data.getTieBack() + '|' + data.getTranType() + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void writeToLogFile(String filename, List<LogMqData> lines) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (LogMqData data : lines) {
				content = data.toString() + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void writeTiebacksToFile(String filename) {
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (String tieback : constants.keyTiebacks.keySet()) {
				content = tieback + "," + constants.keyTiebacks.get(tieback) + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}


	private void writeToResponseQueue(Map<String, String> line) {

		String correlId = "";
		int no = 0;
		CreditCardInfo ccInfo = createCreditCardInfo(line, correlId);
		if (null == ccInfo) {
			return;
		}

		String tranType = ccInfo.getRequestField(MQReqField.TranType);
		constants.tranTypes.add(tranType);

		MQResponseObjectCreator response = new MQResponseObjectCreator(ccInfo);
		ResponseMessage responseMessage = null;

		if (null != line.get("CSStatus")) {
			responseMessage = response.createPreAuthResponse(line.get("CSStatus").trim());
		} else {
			responseMessage = response.createPreAuthResponse(constants.csError);
		}

		try {
			no = constants.tranTypesStats.get(tranType);
			no++;
			constants.tranTypesStats.put(tranType, no);
		} catch (Exception e) {
			constants.tranTypesStats.put(tranType, 1);
		}

		if (CyberSourceConstants.CC_PREAUTH.equalsIgnoreCase(tranType)
				&& constants.writeResponseTypes.contains(CyberSourceConstants.CC_PREAUTH)) {

			try {

				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(2);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ((CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType)
				|| CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType))
				&& (constants.writeResponseTypes.contains(CyberSourceConstants.CC_AUTHCHARGS)
						|| constants.writeResponseTypes.contains(CyberSourceConstants.CC_AUTHCHARGE))) {
			try {
				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(1);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (CyberSourceConstants.CC_SALE.equalsIgnoreCase(tranType)
				&& constants.writeResponseTypes.contains(CyberSourceConstants.CC_SALE)) {

			try {
				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(1);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (CyberSourceConstants.CC_CREDIT.equalsIgnoreCase(tranType)
				&& constants.writeResponseTypes.contains(CyberSourceConstants.CC_CREDIT)) {
			try {
				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(1);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void writeResponse(ResponseMessage responseMessage) {
		if (null != responseMessage) {
			writeCtr++;
			MessageWriter messageWriter = (MessageWriter) CreditCardServiceLocator.getBean("messageWriter");
			messageWriter.setResponseMessage(responseMessage);
			messageWriter.send(ctr++);
		} else {
			nullResponses++;
		}

	}

	private CreditCardInfo createCreditCardInfo(Map<String, String> line, String correlId) {

		try {
			if (null != line && !line.isEmpty()) {
				String[] request = line.get("MessageObject").trim().split("\\|");
				correlId = request[0];

				CreditCardInfo ccInfo = new CreditCardInfo(
						ByteBuffer.wrap(request[1].trim().getBytes(java.nio.charset.StandardCharsets.UTF_8)));

				return ccInfo;
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	private CreditCardInfo createCreditCardInfo(String line) {

		try {
			if (null != line && !line.isEmpty()) {
				String[] request = line.trim().split("\\|");
				CreditCardInfo ccInfo = new CreditCardInfo(
						ByteBuffer.wrap(request[1].trim().getBytes(java.nio.charset.StandardCharsets.UTF_8)));

				return ccInfo;
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	public static void main(String[] args) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");

		readLogsNWriteToResponseQ();
		// statistics();

		//getAllBuildObjects();
		
		//getAllDBLogObjects();

		// readLogs.test();
		
		
		

	}

	private static void readLogsNWriteToResponseQ() {
		ReadNewMQLogsOld readLogs = new ReadNewMQLogsOld();

		//String[] files = { "6_1_server.log.2015-10-28", "6_2_server.log.2015-10-28" };
		String[] files = { "6_1_server.log.2015-10-28" };

		int ctr = 0;
		for (String file : files) {
			
			readLogs.readFile(file);

			ctr = 0;

			for (String key : constants.logData.keySet()) {
				//System.out.println(key + "=>" + constants.logData.get(key));

				//if (null != key && null != lines.get(key) && null != lines.get(key).get("LoggingException")) {
					// if(!lines.get(key).get("MessageObject").contains("PreAuth")){
				//	System.out.println(key + "=>" + lines.get(key).get("LoggingException"));
					// readLogs.writeToResponseQueue(lines.get(key));
				//	ctr++;
					// }
				//}

			}
			System.out.println("size of logs: " + constants.logData.keySet().size());
			// System.out.println("size of non-preauths: " + ctr);
			// System.out.println("all keys: " + lines.keySet());
			readLogs.writeTiebacksToFile("1028Tiebacks.txt");

		}

		System.out.println("writeCtr: " + readLogs.writeCtr);
		System.out.println("nullResponses: " + readLogs.nullResponses);
		System.out.println("constants.tranTypesStats: " + constants.tranTypesStats);

	}

	private static void getAllBuildObjects() {

		ReadNewMQLogsOld readLogs = new ReadNewMQLogsOld();

		//String[] files = { "4_1_server.log.2015-10-28", "4_2_server.log.2015-10-28", "5_1_server.log.2015-10-28","5_2_server.log.2015-10-28",
		//		"6_1_server.log.2015-10-28", "6_2_server.log.2015-10-28" };

		String[] files = { "6_1_server.log.2015-10-28" };
		int totLines = 0;
		for (String file : files) {
			constants.mqDataList.clear();
			totLines = readLogs.countBuildObjects(file);
			
			System.out.println("size of logs | totLinse: " + constants.mqDataList.size() + " | " + totLines);
			if (null != constants.mqDataList && !constants.mqDataList.isEmpty()) {

				Collections.sort(constants.mqDataList, new Comparator<LogMqData>() {
					@Override
					public int compare(LogMqData o1, LogMqData o2) {
						try {
							if (constants.df.parse(o1.getTimeStamp()).before(constants.df.parse(o2.getTimeStamp()))) {
								return 1;
							}
						} catch (ParseException e) {
							e.printStackTrace();
							return 0;
						}
						return 0;
					}

				});
				readLogs.writeToLogFile("1028LogData.txt", constants.mqDataList);
				readLogs.writeTiebacksToFile("1028Tiebacks.txt");
			}
		}
		

	}
	
	private static void getAllDBLogObjects() {

		ReadNewMQLogsOld readLogs = new ReadNewMQLogsOld();

		String[] files = { "6_1_server.log.2015-10-28", "6_2_server.log.2015-10-28" };

		for (String file : files) {
			readLogs.countLoggingErrors(file);
			
			System.out.println("size of logs: " + constants.mqDataList.size());
			if (null != constants.mqDataList && !constants.mqDataList.isEmpty()) {

				Collections.sort(constants.mqDataList, new Comparator<LogMqData>() {
					@Override
					public int compare(LogMqData o1, LogMqData o2) {
						try {
							if (constants.df.parse(o1.getTimeStamp()).before(constants.df.parse(o2.getTimeStamp()))) {
								return 1;
							}
						} catch (ParseException e) {
							e.printStackTrace();
							return 0;
						}
						return 0;
					}

				});
				//readLogs.writeToLogFile("1028LogData.txt", constants.mqDataList);
			}
		}
		

	}

	private static void statistics() {
		ReadNewMQLogsOld readLogs = new ReadNewMQLogsOld();

		// String[] files = { "4_1_server.log.2015-10-26",
		// "4_2_server.log.2015-10-26", "5_1_server.log.2015-10-26",
		// "5_2_server.log.2015-10-26", "6_1_server.log.2015-10-26",
		// "6_2_server.log.2015-10-26" };

		String[] files = { "6_1_server.log.2015-10-26", "6_2_server.log.2015-10-26" };

		// count valid lines
		for (String file : files) {
			System.out.println();
			System.out.printf("Total Messages in %s: %d\n", file, readLogs.countBuildObjects(file));
			System.out.printf("Non-PreAuth Messages processed in %s: %d\n", file, readLogs.countValidLines(file));

			System.out.println("Distinct Trans Types: " + constants.tranTypes);

			// Map<String, Map<String, String>> lines =
			// readLogs.analyzeFileLines(file);
			// System.out.println("size of logs: " + lines.size());
		}

		if (null != constants.dataList && !constants.dataList.isEmpty()) {

			Collections.sort(constants.dataList, new Comparator<LogTiebacks>() {
				@Override
				public int compare(LogTiebacks o1, LogTiebacks o2) {
					try {
						if (constants.df.parse(o1.getTimeStamp()).before(constants.df.parse(o2.getTimeStamp()))) {
							return 1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
						return 0;
					}
					return 0;
				}

			});
			readLogs.writeToFile("1026Tiebacks.txt", constants.dataList);
		}
	}

	private String getTieback(String line) {	
		
		String key = (line.split("\\|"))[0].trim();		
		try{
			key = key.substring(key.lastIndexOf(')') + 1, key.length());
		}catch(Exception e){
			key = "";
		}
		key = key.trim();

		
		return key;
	}

	private String createKey(String line) {
		
		String timeStamp = line.substring(0, 19);
		Calendar calendar = Calendar.getInstance();
		try {

			calendar.setTime(constants.df.parse(timeStamp));
			if (!(calendar.get(Calendar.HOUR_OF_DAY) >= 15 && calendar.get(Calendar.HOUR_OF_DAY) < 19)) {
				return null;
			}
		} catch (ParseException e1) {
			System.out.println("line: " + line);
			e1.printStackTrace();
		}
		
		String req = (line.split("\\|"))[0].trim();		
		try{
			req = req.substring(req.lastIndexOf(')') + 1, req.length());
		}catch(Exception e){
			req = "";
		}
		req = req.trim();

		String key = "";
		if (null != req && !req.isEmpty()) {
			Matcher matcher = constants.data_pattern.matcher(line);
			if (matcher.find()) {
				key = line.substring(0, 19) + "-" + matcher.group(0) + '|' + req;
			}

		}		
		//System.out.print(key + ",");
		return key;
	}
	
	private String createShortKey(String line) {

		String key = "";
		Matcher matcher = constants.data_pattern.matcher(line);
		if (matcher.find()) {
			key = line.substring(0, 19) + "-" + matcher.group(0);
		}		
		return key;
	}
	
	private static class constants {

		public static final String logFilePath = "D:\\ipage-workspace\\mq-logs\\1028-server-log\\";

		public static final Pattern data_pattern = Pattern.compile("\\b(pool-\\w*-thread-\\w*)\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern request_obj_pattern = Pattern.compile("\\b\\w*Request object=>\\w*\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern cs_call_error_data_pattern = Pattern.compile("\\b\\w*CyberSource Call.ErrorList\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern cs_call_stat_data_pattern = Pattern.compile("\\bCyberSource Call.Status\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern resp_type_data_pattern = Pattern.compile("\\bResponse Type|Message=>\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern write_resp_done_data_pattern = Pattern.compile("\\bWriting Response Done\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
		public static final Pattern db_log_done_data_pattern = Pattern.compile("\\bDb Logging Done\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
		
		public static final Pattern db_log_error_data_pattern = Pattern.compile("\\bINSERT INTO EB.CREDIT_CARD_TRANSACTION_HIST\\b");

		// 2015-10-25 05:56:29,807
		public static final Pattern YYYY_MM_DD_HH_MM_SS_SSS = Pattern.compile(
				"^(0[1-9]|1[0-2])\\/(0[1-9]|1\\d|2\\d|3[01])\\/(19|20)\\d{2}$",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final String csError = "900|REJECT|Validation Error";
		public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		public static final String MessageObject = "MessageObject";
		public static final String CSErrorList = "CSErrorList";
		public static final String CSStatus = "CSStatus";
		public static final String ResponseType = "ResponseType";
		public static final String WritingResponseDone = "WritingResponseDone";
		public static final String LoggingDone = "LoggingDone";

		// public static final String writeResponseTypes =
		// CyberSourceConstants.CC_PREAUTH + "," +
		// CyberSourceConstants.CC_AUTHCHARGE + "," +
		// CyberSourceConstants.CC_AUTHCHARGS + "," +
		// CyberSourceConstants.CC_SALE + "," + CyberSourceConstants.CC_CREDIT;
		public static final String writeResponseTypes = CyberSourceConstants.CC_AUTHCHARGE + ","
				+ CyberSourceConstants.CC_AUTHCHARGS + "," + CyberSourceConstants.CC_SALE + ","
				+ CyberSourceConstants.CC_CREDIT;

		public static final String betweenTime = "";
		public static List<LogTiebacks> dataList = new LinkedList<>();
		public static List<LogMqData> mqDataList = new LinkedList<>();
		public static Set<String> tranTypes = new HashSet<>();
		public static Map<String, Integer> tranTypesStats = new HashMap<>();
		public static Map<String, String> mqCsResponses = new LinkedHashMap<>();
		
		public static Map<String, Map<String, String>> lines;
		public static Map<String, String> dbLogErrorlines;
		public static Map<String, String> keyTiebacks;
		
		public static Map<String, String> threadPoolStatus;
		
		public static Map<String, LogMqData> logData;

	}
	
	private static void testFlow(){
		//String str = "01EE50W1L20R4046      PreAuth   IBC             1603                          AMEX                                000574.20                                                                                                                                                                                                                                            20R4046                                                        4423484884955000001382                  ";		
				String str = "01EE50W1L20R4046      PreAuth   IBC             1603                                                              000574.20                                                                                                                                                                                                                                            20R4046                                                                                                ";
				
				RequestMessage requestMessage = new RequestMessage();
				ResponseMessage responseMessage = null;
				EMSCybersourceService service = null;
				int msgNo = 0;
				requestMessage.setMsgBody(ByteBuffer.wrap(str.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
				requestMessage.setCorrelId("c54040404040404040404040404040404040404040404040");
				
				//System.out.println("CyberSource Call Starts. ");
				service = new EMSCybersourceService();
				responseMessage = service.getRequestProcessed(requestMessage);
							
				//CCRequestUtility ccRequestUtility = new CCRequestUtility();
				//CreditCardInfo ccInfo = new CreditCardInfo(ByteBuffer.wrap(str.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
				//CCRequest ccRequest;
				//try {
				//	ccRequest = ccRequestUtility.createRequest(str);
					
				//	String subId = ccRequest.getSubscriptionId();
				//	String requestId = ccRequest.getRequestId();
				//	String merchantID = ccRequest.getMerchantId();
				//	String merchantRefCode = ccRequest.getMechantRefferenceCode();
					
				//	if (StringUtils.isBlank(merchantID)) {
				//		System.out.println("blank merchant Id");
				//	} else if (subId.trim().length() > 30) {
				//		System.out.println("max merchant Id length");
				//	}
				//	if (StringUtils.isBlank(merchantRefCode)) {
				//		System.out.println("merchant ref code missing");
				//	} else if (merchantRefCode.trim().length() > 50) {
				//		System.out.println("merchant ref code length ");
				//	}
				//} catch (CyberSourceException e) {
				//	// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
				
	}

}
