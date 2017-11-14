package com.cc.log.obj;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import ibg.common.cybersource.emsqueue.CreditCardInfo;

public class LogMqData implements Comparable<LogMqData> {

	private String tieBack;
	private String timeStamp;
	private String tranType;
	private String requestObj;
	private String responseObj;
	private String csError;
	private String csStatus;
	private String responseType;
	private String key;
	private boolean responseSentStatus;
	private boolean dbLoggingStatus;
	private String dbErrorLog;
	private String correlId;
	
	public LogMqData(String tieBack, String timeStamp, String tranType) {
		super();
		this.tieBack = tieBack;
		this.timeStamp = timeStamp;
		this.tranType = tranType;
	}

	public LogMqData() {
		super();
	}

	/**
	 * @return the tieBack
	 */
	public String getTieBack() {
		return tieBack;
	}

	/**
	 * @param tieBack
	 *            the tieBack to set
	 */
	public void setTieBack(String tieBack) {
		this.tieBack = tieBack;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * @return the tranType
	 */
	public String getTranType() {
		return tranType;
	}

	/**
	 * @param tranType the tranType to set
	 */
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	
	
	/**
	 * @return the requestObj
	 */
	public String getRequestObj() {
		return requestObj;
	}

	/**
	 * @param requestObj the requestObj to set
	 */
	public void setRequestObj(String requestObj) {
		this.requestObj = requestObj;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	

	/**
	 * @return the responseObj
	 */
	public String getResponseObj() {
		return responseObj;
	}

	/**
	 * @param responseObj the responseObj to set
	 */
	public void setResponseObj(String responseObj) {
		this.responseObj = responseObj;
	}
	

	/**
	 * @return the csError
	 */
	public String getCsError() {
		return csError;
	}

	/**
	 * @param csError the csError to set
	 */
	public void setCsError(String csError) {
		this.csError = csError;
	}

	/**
	 * @return the csStatus
	 */
	public String getCsStatus() {
		return csStatus;
	}

	/**
	 * @param csStatus the csStatus to set
	 */
	public void setCsStatus(String csStatus) {
		this.csStatus = csStatus;
	}

	/**
	 * @return the responseType
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType the responseType to set
	 */
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	
	/**
	 * @return the responseSentStatus
	 */
	public boolean isResponseSentStatus() {
		return responseSentStatus;
	}

	/**
	 * @param responseSentStatus the responseSentStatus to set
	 */
	public void setResponseSentStatus(boolean responseSentStatus) {
		this.responseSentStatus = responseSentStatus;
	}

	/**
	 * @return the dbLoggingStatus
	 */
	public boolean isDbLoggingStatus() {
		return dbLoggingStatus;
	}

	/**
	 * @param dbLoggingStatus the dbLoggingStatus to set
	 */
	public void setDbLoggingStatus(boolean dbLoggingStatus) {
		this.dbLoggingStatus = dbLoggingStatus;
	}

	/**
	 * @return the dbErrorLog
	 */
	public String getDbErrorLog() {
		return dbErrorLog;
	}

	/**
	 * @param dbErrorLog the dbErrorLog to set
	 */
	public void setDbErrorLog(String dbErrorLog) {
		this.dbErrorLog = dbErrorLog;
	}

	/**
	 * @return the correlId
	 */
	public String getCorrelId() {
		return correlId;
	}

	/**
	 * @param correlId the correlId to set
	 */
	public void setCorrelId(String correlId) {
		this.correlId = correlId;
	}

	@Override
	public int compareTo(LogMqData obj) {

		try {
			if(df.parse(this.timeStamp).before(df.parse(obj.getTimeStamp()))){
				return 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}

	public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogMqData [tieBack=" + tieBack + ", timeStamp=" + timeStamp + ", tranType=" + tranType + ", requestObj="
				+ requestObj + ", responseObj=" + responseObj + ", csError=" + csError + ", csStatus=" + csStatus
				+ ", responseType=" + responseType + ", key=" + key + ", responseSentStatus=" + responseSentStatus
				+ ", dbLoggingStatus=" + dbLoggingStatus + ", dbErrorLog=" + dbErrorLog + ", correlId=" + correlId
				+ "]";
	}
}
