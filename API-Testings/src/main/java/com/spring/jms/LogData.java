package com.spring.jms;

public class LogData {

	private String tranType;
	private String billTo;
	private String div;
	private String mqMessageObject;
	private String cyberSourceErrorList;
	private String cyberSourceStatus;
	private String responseType;
	private String mqWriteResponseStart;
	private String mqWriteResponseStartDone;
	private String dbLoggingStart;
	private String dbLoggingDone;
	private String timeStamp;

	public LogData(String tranType, String billTo, String div, String mqMessageObject, String cyberSourceErrorList,
			String cyberSourceStatus, String responseType, String mqWriteResponseStart, String mqWriteResponseStartDone,
			String dbLoggingStart, String dbLoggingDone, String timeStamp) {
		super();
		this.tranType = tranType;
		this.billTo = billTo;
		this.div = div;
		this.mqMessageObject = mqMessageObject;
		this.cyberSourceErrorList = cyberSourceErrorList;
		this.cyberSourceStatus = cyberSourceStatus;
		this.responseType = responseType;
		this.mqWriteResponseStart = mqWriteResponseStart;
		this.mqWriteResponseStartDone = mqWriteResponseStartDone;
		this.dbLoggingStart = dbLoggingStart;
		this.dbLoggingDone = dbLoggingDone;
		this.timeStamp = timeStamp;
	}

	public LogData() {
		super();
	}

	/**
	 * @return the tranType
	 */
	public String getTranType() {
		return tranType;
	}

	/**
	 * @param tranType
	 *            the tranType to set
	 */
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	/**
	 * @return the billTo
	 */
	public String getBillTo() {
		return billTo;
	}

	/**
	 * @param billTo
	 *            the billTo to set
	 */
	public void setBillTo(String billTo) {
		this.billTo = billTo;
	}

	/**
	 * @return the div
	 */
	public String getDiv() {
		return div;
	}

	/**
	 * @param div
	 *            the div to set
	 */
	public void setDiv(String div) {
		this.div = div;
	}

	/**
	 * @return the mqMessageObject
	 */
	public String getMqMessageObject() {
		return mqMessageObject;
	}

	/**
	 * @param mqMessageObject
	 *            the mqMessageObject to set
	 */
	public void setMqMessageObject(String mqMessageObject) {
		this.mqMessageObject = mqMessageObject;
	}

	/**
	 * @return the cyberSourceErrorList
	 */
	public String getCyberSourceErrorList() {
		return cyberSourceErrorList;
	}

	/**
	 * @param cyberSourceErrorList
	 *            the cyberSourceErrorList to set
	 */
	public void setCyberSourceErrorList(String cyberSourceErrorList) {
		this.cyberSourceErrorList = cyberSourceErrorList;
	}

	/**
	 * @return the cyberSourceStatus
	 */
	public String getCyberSourceStatus() {
		return cyberSourceStatus;
	}

	/**
	 * @param cyberSourceStatus
	 *            the cyberSourceStatus to set
	 */
	public void setCyberSourceStatus(String cyberSourceStatus) {
		this.cyberSourceStatus = cyberSourceStatus;
	}

	/**
	 * @return the responseType
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType
	 *            the responseType to set
	 */
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	/**
	 * @return the mqWriteResponseStart
	 */
	public String getMqWriteResponseStart() {
		return mqWriteResponseStart;
	}

	/**
	 * @param mqWriteResponseStart
	 *            the mqWriteResponseStart to set
	 */
	public void setMqWriteResponseStart(String mqWriteResponseStart) {
		this.mqWriteResponseStart = mqWriteResponseStart;
	}

	/**
	 * @return the mqWriteResponseStartDone
	 */
	public String getMqWriteResponseStartDone() {
		return mqWriteResponseStartDone;
	}

	/**
	 * @param mqWriteResponseStartDone
	 *            the mqWriteResponseStartDone to set
	 */
	public void setMqWriteResponseStartDone(String mqWriteResponseStartDone) {
		this.mqWriteResponseStartDone = mqWriteResponseStartDone;
	}

	/**
	 * @return the dbLoggingStart
	 */
	public String getDbLoggingStart() {
		return dbLoggingStart;
	}

	/**
	 * @param dbLoggingStart
	 *            the dbLoggingStart to set
	 */
	public void setDbLoggingStart(String dbLoggingStart) {
		this.dbLoggingStart = dbLoggingStart;
	}

	/**
	 * @return the dbLoggingDone
	 */
	public String getDbLoggingDone() {
		return dbLoggingDone;
	}

	/**
	 * @param dbLoggingDone
	 *            the dbLoggingDone to set
	 */
	public void setDbLoggingDone(String dbLoggingDone) {
		this.dbLoggingDone = dbLoggingDone;
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

}
