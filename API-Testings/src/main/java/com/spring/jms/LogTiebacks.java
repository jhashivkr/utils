package com.spring.jms;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class LogTiebacks implements Comparable<LogTiebacks> {

	private String tieBack;
	private String timeStamp;
	private String tranType;

	public LogTiebacks(String tieBack, String timeStamp, String tranType) {
		super();
		this.tieBack = tieBack;
		this.timeStamp = timeStamp;
		this.tranType = tranType;
	}

	public LogTiebacks() {
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

	@Override
	public int compareTo(LogTiebacks obj) {

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
		return "LogMqData [" + tieBack + '|' + timeStamp + '|' + tranType + "]";
	}
	
	
}
