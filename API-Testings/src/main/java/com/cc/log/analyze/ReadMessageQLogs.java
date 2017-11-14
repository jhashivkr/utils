package com.cc.log.analyze;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cc.log.obj.LogMqData;
import com.spring.jms.LogTiebacks;

import ibg.cc.mq.message.RequestMessage;
import ibg.cc.mq.message.ResponseMessage;
import ibg.common.cybersource.emsqueue.EMSCybersourceService;
import ibg.lib.activity.ReqCallback;

public class ReadMessageQLogs {

	

	

	

	public static void main(String[] args) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");

		
		// countNewProcess();
		readLogsNWriteToResponseQ();
		// statistics();

		// getAllBuildObjects();

		// getAllDBLogObjects();

		// readLogs.test();

	}

	private static void countNewProcess() {
		String[] files = { "6_1_server.log.2015-10-28" };
		System.out.println("total process: " + new CountLines().countProcessStart(files[0]));
	}

	private static void readLogsNWriteToResponseQ() {

		String[] files = { "6_1_server.log.2015-11-11", "6_2_server.log.2015-11-11" };		
		
		LogReader logReader = new LogReader();
		logReader.setLogFiles(files);
		Thread logReaderTh = new Thread(logReader);
		logReaderTh.start();
		
		//Thread logProcessTh = new Thread(new LogProcessor());
		//logProcessTh.start();
		
		//@SuppressWarnings("unchecked")
		//ReqCallback reqCallback = () -> {
			//LogThreadExecutor.getReqFutures().add((Future<Runnable>) LogThreadExecutor.getqExecutor().submit(new LogProcessor()));
			
		//	LogThreadExecutor.getReqFutures().add((Future<Runnable>) LogThreadExecutor.getqExecutor().scheduleAtFixedRate(new LogProcessor(), 1000, 2000, TimeUnit.MILLISECONDS));
		//};

		//reqCallback.executeNewTask();
		
		//System.out.println("writeCtr: " + readLogs.getWriteCtr());
		//System.out.println("nullResponses: " + readLogs.getNullResponses());
		//System.out.println("Constants.tranTypesStats: " + Constants.tranTypesStats);

	}

	

	private static void getAllBuildObjects() {

		// String[] files = { "4_1_server.log.2015-10-28",
		// "4_2_server.log.2015-10-28",
		// "5_1_server.log.2015-10-28","5_2_server.log.2015-10-28",
		// "6_1_server.log.2015-10-28", "6_2_server.log.2015-10-28" };

		String[] files = { "6_1_server.log.2015-10-28" };
		int totLines = 0;
		for (String file : files) {
			Constants.mqDataList.clear();
			totLines = new CountLines().countBuildObjects(file);

			System.out.println("size of logs | totLines: " + Constants.mqDataList.size() + " | " + totLines);
			if (null != Constants.mqDataList && !Constants.mqDataList.isEmpty()) {

				Collections.sort(Constants.mqDataList, new Comparator<LogMqData>() {
					@Override
					public int compare(LogMqData o1, LogMqData o2) {
						try {
							if (Constants.df.parse(o1.getTimeStamp()).before(Constants.df.parse(o2.getTimeStamp()))) {
								return 1;
							}
						} catch (ParseException e) {
							e.printStackTrace();
							return 0;
						}
						return 0;
					}

				});
				// readLogs.writeToLogFile("1028LogData.txt",
				// Constants.mqDataList);
				// readLogs.writeTiebacksToFile("1028Tiebacks.txt");
			}
		}

	}

	private static void getAllDBLogObjects() {

		String[] files = { "6_1_server.log.2015-10-28", "6_2_server.log.2015-10-28" };

		for (String file : files) {
			new CountLines().countLoggingErrors(file);

			System.out.println("size of logs: " + Constants.mqDataList.size());
			if (null != Constants.mqDataList && !Constants.mqDataList.isEmpty()) {

				Collections.sort(Constants.mqDataList, new Comparator<LogMqData>() {
					@Override
					public int compare(LogMqData o1, LogMqData o2) {
						try {
							if (Constants.df.parse(o1.getTimeStamp()).before(Constants.df.parse(o2.getTimeStamp()))) {
								return 1;
							}
						} catch (ParseException e) {
							e.printStackTrace();
							return 0;
						}
						return 0;
					}

				});
				// readLogs.writeToLogFile("1028LogData.txt",
				// Constants.mqDataList);
			}
		}

	}

	private static void statistics() {
		CountLines readLogs = new CountLines();

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

			System.out.println("Distinct Trans Types: " + Constants.tranTypes);

			// Map<String, Map<String, String>> lines =
			// readLogs.analyzeFileLines(file);
			// System.out.println("size of logs: " + lines.size());
		}

		if (null != Constants.dataList && !Constants.dataList.isEmpty()) {

			Collections.sort(Constants.dataList, new Comparator<LogTiebacks>() {
				@Override
				public int compare(LogTiebacks o1, LogTiebacks o2) {
					try {
						if (Constants.df.parse(o1.getTimeStamp()).before(Constants.df.parse(o2.getTimeStamp()))) {
							return 1;
						}
					} catch (ParseException e) {
						e.printStackTrace();
						return 0;
					}
					return 0;
				}

			});
			WriteToFile.writeToFile("1026Tiebacks.txt", Constants.dataList);
		}
	}

	private static void testFlow() {
		// String str = "01EE50W1L20R4046 PreAuth IBC 1603 AMEX 000574.20
		// 20R4046 4423484884955000001382 ";
		String str = "01EE50W1L20R4046      PreAuth   IBC             1603                                                              000574.20                                                                                                                                                                                                                                            20R4046                                                                                                ";

		RequestMessage requestMessage = new RequestMessage();
		ResponseMessage responseMessage = null;
		EMSCybersourceService service = null;
		int msgNo = 0;
		requestMessage.setMsgBody(ByteBuffer.wrap(str.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		requestMessage.setCorrelId("c54040404040404040404040404040404040404040404040");

		// System.out.println("CyberSource Call Starts. ");
		service = new EMSCybersourceService();
		responseMessage = service.getRequestProcessed(requestMessage);

		// CCRequestUtility ccRequestUtility = new CCRequestUtility();
		// CreditCardInfo ccInfo = new
		// CreditCardInfo(ByteBuffer.wrap(str.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		// CCRequest ccRequest;
		// try {
		// ccRequest = ccRequestUtility.createRequest(str);

		// String subId = ccRequest.getSubscriptionId();
		// String requestId = ccRequest.getRequestId();
		// String merchantID = ccRequest.getMerchantId();
		// String merchantRefCode = ccRequest.getMechantRefferenceCode();

		// if (StringUtils.isBlank(merchantID)) {
		// System.out.println("blank merchant Id");
		// } else if (subId.trim().length() > 30) {
		// System.out.println("max merchant Id length");
		// }
		// if (StringUtils.isBlank(merchantRefCode)) {
		// System.out.println("merchant ref code missing");
		// } else if (merchantRefCode.trim().length() > 50) {
		// System.out.println("merchant ref code length ");
		// }
		// } catch (CyberSourceException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}
	
	

}
