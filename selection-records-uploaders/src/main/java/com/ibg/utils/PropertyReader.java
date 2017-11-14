package com.ibg.utils;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("propertyReader")
public class PropertyReader {

	@Value("${json.logs.dir}")
	private String jsonLogsDir;

	@Value("${list.logs.dir}")
	private String listLogsDir;

	@Value("${dataHome}")
	private String dataHome;

	@Value("${file.process.seq}")
	private String fileProcessSeq;

	@Value("${file.parse.logs}")
	private String fileParserLog;

	@Value("${json.bad.data.home}")
	private String jsonBadDataHome;

	@Value("${load.status.logs}")
	private String loadStatusLogs;

	@Value("${load.db.env}")
	private String loadDbEnv;

	@Value("${ftp.loc}")
	private String ftpLoc;

	@Value("${ftp.port}")
	private Integer ftpPort;

	@Value("${ftp.uid}")
	private String ftpUid;

	@Value("${ftp.pass}")
	private String ftpPass;

	@Value("${ftp.dump.loc}")
	private String ftpDumpLoc;

	@Value("${lcDataHome}")
	private String lcDataHome;

	@Value("${dyDataHome}")
	private String dyDataHome;

	@Value("${cis.customers}")
	private String cisCustomers;
	
	@Value("${cis.cust.data.file.name}")
	private String cisCustDataFileName;

	public String getJsonLogsDir() {
		return jsonLogsDir;
	}

	public String getListLogsDir() {
		return listLogsDir;
	}

	public String getDataHome() {
		return dataHome;
	}

	public String getFileProcessSeq() {
		return fileProcessSeq;
	}

	public String getFileParserLog() {
		return fileParserLog;
	}

	public String getJsonBadDataHome() {
		return jsonBadDataHome;
	}

	public String getLoadStatusLogs() {
		return loadStatusLogs;
	}

	public String getLoadDbEnv() {
		return loadDbEnv;
	}

	public String getFtpLoc() {
		return ftpLoc;
	}

	public Integer getFtpPort() {
		return ftpPort;
	}

	public String getFtpUid() {
		return ftpUid;
	}

	public String getFtpPass() {
		return ftpPass;
	}

	public String getFtpDumpLoc() {
		return ftpDumpLoc;
	}

	public String getLcDataHome() {
		return lcDataHome;
	}

	public String getDyDataHome() {
		return dyDataHome;
	}

	public List<String> getCisCustomers() {
		try {
			String[] backLoadCustomers = cisCustomers.split(",");

			return Arrays.asList(backLoadCustomers);
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getCisCustDataFileName(){
		return cisCustDataFileName;
	}

}
