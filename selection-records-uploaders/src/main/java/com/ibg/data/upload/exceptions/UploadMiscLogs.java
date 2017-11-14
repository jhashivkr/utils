package com.ibg.data.upload.exceptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ibg.db.ServiceLocator;
import com.ibg.utils.PropertyReader;


public class UploadMiscLogs {

	private static BufferedWriter bufWriter;
	private PropertyReader prop;

	public UploadMiscLogs() {
		try {
			prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
			Calendar cldr = new GregorianCalendar();
			String timeStamp = cldr.get(Calendar.YEAR) + "" + (cldr.get(Calendar.MONTH) + 1) + "" + cldr.get(Calendar.DAY_OF_MONTH) + ""
					+ cldr.get(Calendar.HOUR_OF_DAY) + "" + cldr.get(Calendar.MINUTE);

			String logFileName = prop.getJsonLogsDir() + "misc-logs-" + timeStamp + ".log";

			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeBuffer(String msg) {
		try {
			bufWriter.write("\n" + msg + "\n");
		} catch (IOException e) {
			System.out.println("error: " + e);
			e.printStackTrace();
		}
	}

	public static void closeWriter() {

		if (bufWriter != null) {
			try {
				bufWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
