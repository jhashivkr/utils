package com.cc.log.analyze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;

import com.cc.log.obj.LogMqData;
import com.spring.jms.LogTiebacks;

import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.cybersource.creditcard.constants.CyberSourceConstants;

public class CountLines {
	
	public int countValidLines(String fileName) {
		int lineCtr = 0;
		try {
			Path path = FileSystems.getDefault().getPath(Constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = Constants.request_obj_pattern.matcher(line);
				if (matcher.find()) {
					// conditional processing - remove in live scenario
					Calendar calendar = Calendar.getInstance();
					try {
						calendar.setTime(Constants.df.parse(line.substring(0, 19)));
						// if (calendar.get(Calendar.HOUR_OF_DAY) >= 22) {

						CreditCardInfo ccInfo = Utilities.createCreditCardInfo((line.split("=>"))[1]);
						// ccInfo.setRequest(ByteBuffer.wrap((line.split("=>"))[1].getBytes()));
						if (null != ccInfo) {

							String tranType = ccInfo.getRequestField(MQReqField.TranType);
							Constants.tranTypes.add(tranType);
							if (!CyberSourceConstants.CC_PREAUTH.equalsIgnoreCase(tranType)) {
								LogTiebacks logTiebacks = new LogTiebacks();
								logTiebacks.setTieBack(ccInfo.getRequestField(MQReqField.TieBack));
								logTiebacks.setTimeStamp(line.substring(0, 23));
								logTiebacks.setTranType(tranType);
								lineCtr++;

								Constants.dataList.add(logTiebacks);

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
			Path path = FileSystems.getDefault().getPath(Constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			Constants.keyTiebacks = new LinkedHashMap<>();
			while ((line = reader.readLine()) != null) {

				if (line.contains("Request object=>")) {

					String req = (line.split("=>"))[1].trim();
					req = req.split("\\|")[1].trim();
					if (null != req && !req.isEmpty()) {

						String tranType = req.substring(22, 32).trim();
						String tieBack = req.substring(2, 22).trim();
						Constants.tranTypes.add(tranType);
						boolean proceed = true;
						// boolean proceed =
						// CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType)
						// ||
						// CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType)
						// ||
						// CyberSourceConstants.CC_SALE.equalsIgnoreCase(tranType)
						// ||
						// CyberSourceConstants.CC_CREDIT.equalsIgnoreCase(tranType);
						// boolean proceed =
						// CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType)
						// ||
						// CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType);
						if (proceed) {

							LogMqData logMqData = new LogMqData();
							logMqData.setTieBack(tieBack);
							logMqData.setTimeStamp(line.substring(0, 23));
							logMqData.setTranType(tranType);
							logMqData.setRequestObj(req);

							String key = "";
							Matcher matcher1 = Constants.data_pattern.matcher(line);
							if (matcher1.find()) {
								key = line.substring(0, 19) + "-" + matcher1.group(0);
								logMqData.setKey(key);
							}
							lineCtr++;

							String tieback = Utilities.getTieback(line);
							if (!Constants.keyTiebacks.containsKey(tieback)) {
								Constants.keyTiebacks.put(tieback, key);
							}

							Constants.mqDataList.add(logMqData);

						}
					}

				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return lineCtr;
	}

	public int countProcessStart(String fileName) {
		int lineCtr = 0;
		try {
			Path path = FileSystems.getDefault().getPath(Constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				if (line.contains("CyberSource Call Starts.")) {

					lineCtr++;

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
			Path path = FileSystems.getDefault().getPath(Constants.logFilePath, fileName);
			InputStream in = Files.newInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = Constants.db_log_done_data_pattern.matcher(line);
				if (matcher.find()) {
					String tieBack = line.split("\\|")[0].trim();

					if (null != tieBack && !tieBack.isEmpty()) {

						LogMqData logMqData = new LogMqData();
						logMqData.setTieBack(tieBack);
						logMqData.setTimeStamp(line.substring(0, 23));
						Matcher matcher1 = Constants.data_pattern.matcher(line);
						if (matcher1.find()) {
							String key = line.substring(0, 19) + "-" + matcher1.group(0);
							logMqData.setKey(key);
						}
						lineCtr++;

						Constants.mqDataList.add(logMqData);

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
			Matcher matcher = Constants.cs_call_stat_data_pattern.matcher(line);
			if (matcher.find()) {
				String req = (line.split("=>"))[1].trim();

				// Constants.mqCsResponses.put(key, value)

			}
		}

		return lineCtr;
	}

	
}
