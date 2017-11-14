package com.ibg.parsers.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DateField {

	private String $date;
	@JsonIgnore
	private Calendar cldr;

	@JsonIgnore
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	public DateField() {
		cldr = new GregorianCalendar();
	}

	public String get$date() {

		return $date;
	}

	public void set$date(String $date) {
		this.$date = $date;

		if (null != $date) {

			try {
				cldr.setTimeInMillis(Long.parseLong($date));
			} catch (NumberFormatException nfe) {
				cldr.setTimeInMillis(cldr.getTimeInMillis());
			}

			this.$date = df.format(cldr.getTime());

		}
	}

	@Override
	public String toString() {
		return $date;
	}

}
