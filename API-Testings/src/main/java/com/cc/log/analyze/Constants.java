package com.cc.log.analyze;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.cc.log.obj.LogMqData;
import com.spring.jms.LogTiebacks;

import ibg.cybersource.creditcard.constants.CyberSourceConstants;

final class Constants {

	public static final String logFilePath = "D:\\ipage-workspace\\mq-logs\\11_11_server.log\\";

	public static final Pattern data_pattern = Pattern.compile("\\b(pool-\\w*-thread-\\w*)\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

	public static final Pattern request_obj_pattern = Pattern.compile("\\b\\w*Request object=>\\w*\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

	public static final Pattern cs_call_stat_data_pattern = Pattern.compile("\\bCyberSource Call.Status\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

	public static final Pattern db_log_done_data_pattern = Pattern.compile("\\bDb Logging Done\\b",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);

	public static final String csError = "900|REJECT|Validation Error";
	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
	public static Map<String, String> changedKeys;

}
