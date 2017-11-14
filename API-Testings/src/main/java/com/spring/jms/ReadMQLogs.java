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

import ibg.cc.mq.message.ResponseMessage;
import ibg.cc.mq.writers.MessageWriter;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.cybersource.creditcard.constants.CyberSourceConstants;
import ibg.cybersource.service.CreditCardServiceLocator;

public class ReadMQLogs {

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
				Matcher matcher = constants.build_obj_comp_data_pattern.matcher(line);
				if (matcher.find()) {
					// conditional processing - remove in live scenario
					Calendar calendar = Calendar.getInstance();
					try {
						calendar.setTime(constants.df.parse(line.substring(0, 19)));
						//if (calendar.get(Calendar.HOUR_OF_DAY) >= 22) {
							
							CreditCardInfo ccInfo = createCreditCardInfo((line.split("=>"))[1]);
							//ccInfo.setRequest(ByteBuffer.wrap((line.split("=>"))[1].getBytes()));
							if (null != ccInfo) {
								
								String tranType = ccInfo.getRequestField(MQReqField.TranType);
								constants.tranTypes.add(tranType);
								if(!CyberSourceConstants.CC_PREAUTH.equalsIgnoreCase(tranType)){
									LogTiebacks logTiebacks = new LogTiebacks();
									logTiebacks.setTieBack(ccInfo.getRequestField(MQReqField.TieBack));
									logTiebacks.setTimeStamp(line.substring(0, 23));
									logTiebacks.setTranType(tranType);
									lineCtr++;
									
									constants.dataList.add(logTiebacks);
									
									
									//try {
										//System.out.println("line: " + line);
										//System.out.println("ccInfo: " + new String(ccInfo.getRequest().array(), java.nio.charset.StandardCharsets.UTF_8));
										//System.out.println(ccInfo.getRequestField(MQReqField.Card_Bill_To) + "," + ccInfo.getRequestField(MQReqField.Merch_Bill_To) + ", " + ccInfo.getRequestField(MQReqField.Division));
									//	CreditCardCustomerProfileService creditCardCustomerProfileSrvc = (CreditCardCustomerProfileService) CreditCardServiceLocator
									//			.getService(CreditCardServiceLocator.ServiceName.CRECRD_CUSTOMER_PROFILE_SERVICE);
									//	Map<String, String> subsIdMap = creditCardCustomerProfileSrvc.getSubscriptionIDInfo(ccInfo);
									//	if(null != subsIdMap && !subsIdMap.isEmpty()){
										//	System.out.println("subsIdMap: " + subsIdMap);
										//}
										
									//} catch (Exception e) {
									//	e.printStackTrace();
									//}
									
								}
							}
							
						//}
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
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.build_obj_comp_data_pattern.matcher(line);
				if (matcher.find()) {
					
					String req = (line.split("=>"))[1].trim();
					req = req.split("\\|")[1].trim();
					
					if (null != req && !req.isEmpty()) {

						String tranType = req.substring(22, 32).trim();
						String tieBack = req.substring(2, 22).trim();
						constants.tranTypes.add(tranType);
						boolean proceed = CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_SALE.equalsIgnoreCase(tranType) || CyberSourceConstants.CC_CREDIT.equalsIgnoreCase(tranType);
						//boolean proceed = true;
						if (proceed) {
							
							LogMqData logMqData = new LogMqData();
							logMqData.setTieBack(tieBack);
							logMqData.setTimeStamp(line.substring(0, 23));
							logMqData.setTranType(tranType);
							logMqData.setRequestObj(req);
							
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

							//constants.mqCsResponses.put(key, value)


				}
			}
		

		return lineCtr;
	}
	
	public int countProcessStartLines(String name) {
		int lineCtr = 0;
		try {
			Path path = FileSystems.getDefault().getPath(constants.logFilePath, name);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.build_msg_obj_pattern.matcher(line);
				if (matcher.find()) {
					// conditional processing - remove in live scenario
					Calendar calendar = Calendar.getInstance();
					try {
						calendar.setTime(constants.df.parse(line.substring(0, 19)));
						// if (calendar.get(Calendar.HOUR_OF_DAY) >= 22) {

						lineCtr++;

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

	public Map<String, Map<String, String>> readFileLines(String name) {
		try {
			Path path = FileSystems.getDefault().getPath(constants.logFilePath, name);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			Map<String, Map<String, String>> lines = new LinkedHashMap<>();
			String line = null;
			String key = "";
			while ((line = reader.readLine()) != null) {
				Matcher matcher = constants.data_pattern.matcher(line);
				if (matcher.find()) {
					key = decideKey(line.substring(0, 19), matcher.group(0), line, lines);
					
					if(!lines.containsKey(key)){
						lines.put(key, new HashMap<>());
					}
					
					reduceMap(key, line, lines);
				}
			}
			return lines;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return null;
	}

	private String decideKey(String possibleKeyTmstmp, String possibleKeyStr, String line, Map<String, Map<String, String>> lines) {
		
		//conditional processing - remove in live scenario
		Calendar calendar = Calendar.getInstance();
				
		int ctr = 1;
		String thisKey = possibleKeyTmstmp + "-" + possibleKeyStr;
		
		if(null  == lines || lines.isEmpty()){
			return thisKey;
		}

		if (null == lines.keySet() || lines.keySet().isEmpty()) {
			return thisKey;
		}
		
		if(line.contains("Building message object.")){
			synchronized (lines) {
				// check if this key already exist in the dataset
				if (lines.keySet().contains(thisKey)) {
					Map<String, String> tmp = lines.get(thisKey);
					// change the key with appending tieback to it
					String newKey = thisKey + "|" + String.valueOf(Math.random());
					lines.remove(thisKey);

					lines.put(newKey, tmp);
				}

			}
			
			return thisKey;
		}
		if (lines.keySet().contains(thisKey)) {					
			return thisKey;
		}
		

		// for the boundary cases
		// start setting this possible time stamp by 1 sec and search for an
		// existing key in the set		
		try {
			calendar.setTime(constants.df.parse(possibleKeyTmstmp));

			for (; ctr <= 60 ;ctr++) {
				calendar.setTimeInMillis(calendar.getTimeInMillis() - 1000);
				thisKey = constants.df.format(calendar.getTime()) + "-" + possibleKeyStr;

				if (lines.keySet().contains(thisKey)) {					
					return thisKey;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		thisKey = possibleKeyTmstmp + "-" + possibleKeyStr;
		return thisKey;
	}

	private void reduceMap(String key, String line, Map<String, Map<String, String>> data) {

		//String [] tiebacks = {"1CJDP","1CK8L","1CK81","1CS25","1DB57","1DB58","52KSQ","52FSJ","52DGK","52LJV","52LJW","52LKM","52LKT","52QDH","52QDX","52QD0","50NMW","26BS3","16WJ3","9MC8H"};
		//List<String> tiebackList = Arrays.asList(tiebacks);
		
		if(line.contains("Building message object complete. Message")){
			try {
				
				//for(String tie: tiebackList){
				//	if(line.contains(tie)){
				//		System.out.println(tie + "=>" + line);
				//	}
				//}
				
				data.get(key).put("MessageObject", (line.split("=>"))[1]);
			} catch (Exception e) {
				//e.printStackTrace();
				Map<String, String> lineData = new HashMap<>();
				lineData.put("MessageObject", (line.split("=>"))[1]);
				data.put(key, lineData);
			}
			return;
		}

		//matcher = constants.cs_call_ret_data_pattern.matcher(line);
		//if (matcher.find()) {
		else if(line.contains("Rerturned from CyberSource Call. ErrorList")){
			try {
				data.get(key).put("CSErrorList", (line.split("=>"))[1]);
			} catch (Exception e) {
				//e.printStackTrace();
				Map<String, String> lineData = new HashMap<>();
				lineData.put("CSErrorList", (line.split("=>"))[1]);
				data.put(key, lineData);
			}
			return;
		}

		//matcher = constants.cs_call_stat_data_pattern.matcher(line);
		//if (matcher.find()) {
		else if(line.contains("Rerturned from CyberSource Call. Status =>")){
			try {
				//System.out.println(key + "=>" + line);
				data.get(key).put("CSStatus", (line.split("=>"))[1]);
			} catch (Exception e) {
				e.printStackTrace();
				Map<String, String> lineData = new HashMap<>();
				lineData.put("CSStatus", (line.split("=>"))[1]);
				data.put(key, lineData);
			}
			return;
		}
		else if(line.contains("Rertruned from CyberSource Call. ErrorList =>")){
			
				try {
					data.get(key).put("CSStatus", (line.split("=>"))[1]);
				} catch (Exception e) {
					//e.printStackTrace();
					Map<String, String> lineData = new HashMap<>();
					lineData.put("CSStatus", (line.split("=>"))[1]);
					data.put(key, lineData);
				}
				return;
		}
		else if(line.contains("Response Type")){
			try {
				data.get(key).put("ResponseType", (line.split("Response Type:"))[1]);
			} catch (Exception e) {
				//e.printStackTrace();
				Map<String, String> lineData = new HashMap<>();
				lineData.put("ResponseType", (line.split("Response Type:"))[1]);
				data.put(key, lineData);
			}
			return;
		}

		//matcher = constants.write_resp_done_data_pattern.matcher(line);
		//if (matcher.find()) {
		else if(line.contains("Writing Response Done")){
			try {
				data.get(key).put("WritingResponseDone", "true");
			} catch (Exception e) {
				//e.printStackTrace();
				Map<String, String> lineData = new HashMap<>();
				lineData.put("WritingResponseDone", "true");
				data.put(key, lineData);
			}
			return;
		}

		//matcher = constants.db_log_done_data_pattern.matcher(line);
		//if (matcher.find()) {
		else if(line.contains("Logging Done")){			
			try {
				data.get(key).put("LoggingDone", "true");
			} catch (Exception e) {
				//e.printStackTrace();
				Map<String, String> lineData = new HashMap<>();
				lineData.put("LoggingDone", "true");
				data.put(key, lineData);
			}
			return;
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
				//content = data.toString() + '\n';
				content = data.getKey() + '\n';
				writer.write(content, 0, content.length());
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void writeStatusToLogFile(String filename, Map<String, Map<String, String>> lines){
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath("D:\\ipage-workspace\\mq-logs\\", filename),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			String content = "";
			for (String key : lines.keySet()) {
				if (null != key && null != lines.get(key) && null != lines.get(key).get("MessageObject")) {
					if (!lines.get(key).get("MessageObject").contains("PreAuth")) {

						String tieback = lines.get(key).get("MessageObject");
						tieback = tieback.split("\\|")[1];
						try{
							tieback = tieback.substring(3, 17);
						}catch(Exception e){
							tieback = "";
						}
						tieback = tieback.trim();
						
						content = tieback + "," + lines.get(key).get("CSStatus") + '\n';
						writer.write(content, 0, content.length());

					}
				}

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
		
		try{
			no = constants.tranTypesStats.get(tranType);
			no++;
			constants.tranTypesStats.put(tranType, no);
		}catch(Exception e){
			constants.tranTypesStats.put(tranType, 1);
		}

		if (CyberSourceConstants.CC_PREAUTH.equalsIgnoreCase(tranType)
				&& constants.writeResponseTypes.contains(CyberSourceConstants.CC_PREAUTH)) {

			try {

				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(2);
				
				System.out.println("responseMessage: " + responseMessage);
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

				System.out.println("responseMessage: " + responseMessage);				
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

				System.out.println("responseMessage: " + responseMessage);
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

				System.out.println("responseMessage: " + responseMessage);
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
		}else{
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
		
		//readLogsNWriteToResponseQ();
		readLogsNWriteToFile();
		//statistics();

		//getAllBuildObjects();
		
		// readLogs.test();

	}
	
	private static void readLogsNWriteToFile(){
		ReadMQLogs readLogs = new ReadMQLogs();

		 //String[] files = { "4_1_server.log.2015-10-26",
		 //"4_2_server.log.2015-10-26", "5_1_server.log.2015-10-26",
		 //"5_2_server.log.2015-10-26", "6_1_server.log.2015-10-26",
		 //"6_2_server.log.2015-10-26" };

		String[] files = { "6_1_server.log.2015-10-27" };

		for (String file : files) {

			System.out.println("reading file: " + file);

			Map<String, Map<String, String>> lines = new HashMap<>();
			lines = readLogs.readFileLines(file);
			readLogs.writeStatusToLogFile("1026status_" + file + ".txt", lines);
		}

		
		System.out.println();
		
	}
	
	private static void readLogsNWriteToResponseQ(){
		ReadMQLogs readLogs = new ReadMQLogs();

		//String[] files = { "4_1_server.log.2015-10-26", "4_2_server.log.2015-10-26", "5_1_server.log.2015-10-26",
		//		"5_2_server.log.2015-10-26", "6_1_server.log.2015-10-26", "6_2_server.log.2015-10-26" };
		
		String[] files = { "4_1_server.log.2015-10-26" };
		

		int ctr = 0;
		for (String file : files) {
			
			System.out.println("reading file: " + file);
			
			Map<String, Map<String, String>> lines = new HashMap<>();
			lines = readLogs.readFileLines(file);
			
			ctr = 0;

			
			for (String key : lines.keySet()) {
				//System.out.println(key + "=>" + lines.get(key));
				
				if(null != key && null != lines.get(key) && null != lines.get(key).get("MessageObject")){
					if(!lines.get(key).get("MessageObject").contains("PreAuth")){
						
						//System.out.println(lines.get(key).get("MessageObject") + "=>" + lines.get(key).get("CSStatus"));
						
						//for(String tie: tiebackList){
						//	if(lines.get(key).get("MessageObject").contains(tie)){
						//		System.out.println(tie + "=>" + lines.get(key).get("CSStatus"));
								//System.out.println(key + "=>" + lines.get(key));
						//	}
						//}
						
						
					 //System.out.println(key + "=>" + lines.get(key).get("MessageObject"));
					 readLogs.writeToResponseQueue(lines.get(key));
					 ctr++;
					 
					 
					}
				}
			//readLogs.writeStatusToLogFile("1026status.txt", lines);
			}
			
			
			System.out.println();
			//System.out.println("size of logs: " + lines.size());
			System.out.println("size of non-preauths: " + ctr);
			//System.out.println("all keys: " + lines.keySet());
		}
		
		
		//System.out.println("writeCtr: " + readLogs.writeCtr);
		//System.out.println("nullResponses: " + readLogs.nullResponses);
		//System.out.println("constants.tranTypesStats: " + constants.tranTypesStats);
		
	}
	
	private static void getAllBuildObjects(){
		
		ReadMQLogs readLogs = new ReadMQLogs();

		String[] files = { "4_1_server.log.2015-10-26", "4_2_server.log.2015-10-26", "5_1_server.log.2015-10-26",
				"5_2_server.log.2015-10-26", "6_1_server.log.2015-10-26", "6_2_server.log.2015-10-26" };
		

		for (String file : files) {
			readLogs.countBuildObjects(file);
			System.out.printf("Total Requests in %s=>%d\n", file, constants.mqDataList.size());

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
				// readLogs.writeToLogFile("1026LogData.txt",
				// constants.mqDataList);
			}
		}
		
	}
	
	private static void statistics(){
		ReadMQLogs readLogs = new ReadMQLogs();

		String[] files = { "4_1_server.log.2015-10-26", "4_2_server.log.2015-10-26", "5_1_server.log.2015-10-26",
				"5_2_server.log.2015-10-26", "6_1_server.log.2015-10-26", "6_2_server.log.2015-10-26" };

		// count valid lines
		for (String file : files) {
			System.out.println();
			System.out.printf("Total Messages in %s: %d\n", file, readLogs.countProcessStartLines(file));
			System.out.printf("Non-PreAuth Messages processed in %s: %d\n", file, readLogs.countValidLines(file));

			System.out.println("Distinct Trans Types: " + constants.tranTypes);
			

			// Map<String, Map<String, String>> lines =
			// readLogs.readFileLines(file);
			// System.out.println("size of logs: " + lines.size());
		}
		
		if (null != constants.dataList && !constants.dataList.isEmpty()) {
			
			Collections.sort(constants.dataList, new Comparator<LogTiebacks>() {
				@Override
				public int compare(LogTiebacks o1, LogTiebacks o2) {
					try {
						if(constants.df.parse(o1.getTimeStamp()).before(constants.df.parse(o2.getTimeStamp()))){
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

	private void test() {
		String str = "2015-10-25 21:37:23,957 INFO  [stdout] Making CyberSource Call.";
		String str1 = "2015-10-25 21:37:23,957 INFO  [stdout] (pool-8-thread-46) Making CyberSource Call.";
		String str2 = "2015-10-25 21:37:23,975 INFO  [stdout] (pool-8-thread-33) Rerturned from CyberSource Call. ErrorList => []";
		String csCallStatDataPattern = "2015-10-25 21:37:23,975 INFO  [stdout] (pool-8-thread-36) Rerturned from CyberSource Call. Status => 100|ACCEPT|Successful transaction.";
		String str4 = "2015-10-25 21:37:24,039 INFO  [stdout] (pool-8-thread-49) Rerturned from CyberSource Call. ErrorList => []";

		String responseType = "2015-10-25 05:56:29,618 INFO  [stdout] (pool-8-thread-20) Response Type: 2";

		String msgObj = "2015-10-25 05:56:28,296 INFO  [stdout] (pool-8-thread-17) Building message object complete. Message => ID:c54040404040404040404040404040404040404040404040 | 01EE50H7220E7956      PreAuth   IBC             102415                                                            000748.03                                                                                                                                                                                                                                            20E7956                                                                                                ";

		// resp_type_data_pattern

		Matcher matcher = constants.data_pattern.matcher(str);

		if (matcher.find()) {
			System.out.println("found matching in str");
		}

		matcher = constants.data_pattern.matcher(str1);
		if (matcher.find()) {
			System.out.println("found matching in str1: " + matcher.group(0) + ", " + str1.substring(0, 23));

		}

		matcher = constants.data_pattern.matcher(str2);
		if (matcher.find()) {
			System.out.println("found matching in str2: " + matcher.group(0));
		}

		matcher = constants.cs_call_stat_data_pattern.matcher(csCallStatDataPattern);
		if (matcher.find()) {
			System.out.println(
					"found matching in str3: " + matcher.group(0) + ", " + (csCallStatDataPattern.split("=>"))[1]);
		}

		matcher = constants.data_pattern.matcher(str4);
		if (matcher.find()) {
			System.out.println("found matching in str4: " + matcher.group(0));
		}

		matcher = constants.resp_type_data_pattern.matcher(responseType);
		if (matcher.find()) {
			System.out.println("found matching in responseType: " + matcher.group(0) + ", "
					+ (responseType.split("Response Type:"))[1]);
		}

		matcher = constants.build_obj_comp_data_pattern.matcher(msgObj);
		if (matcher.find()) {
			System.out
					.println("found matching in message object: " + matcher.group(0) + ", " + (msgObj.split("=>"))[1]);
		}

	}

	private static class constants {
		
		public static final String logFilePath = "D:\\ipage-workspace\\mq-logs\\1027-server-log\\";
		
		public static final Pattern data_pattern = Pattern.compile("\\b(pool-\\w*-thread-\\w*)\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern build_msg_obj_pattern = Pattern.compile("\\bBuilding message object.\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
		
		public static final Pattern build_obj_comp_data_pattern = Pattern.compile(
				"\\bBuilding message object complete. Message\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern cs_call_ret_data_pattern = Pattern.compile(
				"\\bRerturned from CyberSource Call. ErrorList\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern cs_call_ret_data_pattern1 = Pattern.compile(
				"\\bRertruned from CyberSource Call. ErrorList\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern cs_call_stat_data_pattern = Pattern.compile(
				"\\bRerturned from CyberSource Call. Status\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

		public static final Pattern resp_type_data_pattern = Pattern.compile("\\bResponse Type\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
		public static final Pattern write_resp_done_data_pattern = Pattern.compile("\\bWriting Response Done\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
		public static final Pattern db_log_done_data_pattern = Pattern.compile("\\bLogging Done\\b",
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

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
		
		
		//public static SortedSet<Map.Entry<String, String>> sortedset = new TreeSet<Map.Entry<String, String>>(new Comparator<Map.Entry<String, String>>() {
		//	@Override
		//	public int compare(Map.Entry<String, String> e1, Map.Entry<String, String> e2) {

		//		if (null == e1.getValue()) {
		//			return 1;
		//		}
		//		if (null == e2.getValue()) {
		//			return -1;
		//		}
		//		int res = e1.getValue().compareTo(e2.getValue());
		//		return res != 0 ? res : 1;

		//	}
		//});
	}

}
