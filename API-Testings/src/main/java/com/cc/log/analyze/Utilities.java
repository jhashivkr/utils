package com.cc.log.analyze;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;

import ibg.common.cybersource.emsqueue.CreditCardInfo;

public class Utilities {

	public static CreditCardInfo createCreditCardInfo(Map<String, String> line, String correlId) {

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

	public static CreditCardInfo createCreditCardInfo(String line) {

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

	public static String getTieback(String line) {

		String key = (line.split("\\|"))[0].trim();
		try {
			key = key.substring(key.lastIndexOf(')') + 1, key.length());
		} catch (Exception e) {
			key = "";
		}
		key = key.trim();

		return key;
	}

	public static String createKey(String line) {

		String timeStamp = line.substring(0, 19);
		Calendar calendar = Calendar.getInstance();
		try {

			calendar.setTime(Constants.df.parse(timeStamp));
			if (!(calendar.get(Calendar.HOUR_OF_DAY) >= 15 && calendar.get(Calendar.HOUR_OF_DAY) < 19)) {
				return null;
			}
		} catch (ParseException e1) {
			System.out.println("line: " + line);
			e1.printStackTrace();
		}

		String req = (line.split("\\|"))[0].trim();
		try {
			req = req.substring(req.lastIndexOf(')') + 1, req.length());
		} catch (Exception e) {
			req = "";
		}
		req = req.trim();

		String key = "";
		if (null != req && !req.isEmpty()) {
			Matcher matcher = Constants.data_pattern.matcher(line);
			if (matcher.find()) {
				key = line.substring(0, 19) + "-" + matcher.group(0) + '|' + req;
			}

		}
		// System.out.print(key + ",");
		return key;
	}

	public static String createShortKey(String line) {

		String key = "";
		Matcher matcher = Constants.data_pattern.matcher(line);
		if (matcher.find()) {
			key = line.substring(0, 19) + "-" + matcher.group(0);
		}
		return key;
	}
}
