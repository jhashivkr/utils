package com.ibg.data.downloaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class BufferDataWriter {

	private String custGrp;
	private String dataType;
	private BufferedWriter bufWriter;
	private String dataFileName;
	private String fileTmplt = "ipage_<ts>_<cg>_<dt>.txt";

	public BufferDataWriter(String custGrp, String dataType, String dataFileName) {
		this.custGrp = custGrp;
		this.dataType = dataType;
		this.dataFileName = dataFileName;
	}

	private void createDataFile() {

		try {
			StringBuilder bldr = new StringBuilder();

			Calendar cldr = new GregorianCalendar();

			bldr.append(dataFileName).append(custGrp).append('/').append(fileTmplt);
			dataFileName = bldr.toString();
			
			bldr.delete(0, bldr.length());
			
			bldr.append(cldr.get(Calendar.YEAR)).append((cldr.get(Calendar.MONTH) + 1)).append(cldr.get(Calendar.DAY_OF_MONTH))
					.append(cldr.get(Calendar.HOUR_OF_DAY)).append(cldr.get(Calendar.MINUTE));
			dataFileName = dataFileName.replace("<ts>", bldr.toString());
			dataFileName = dataFileName.replace("<cg>", custGrp);
			dataFileName = dataFileName.replace("<dt>", dataType);

			File logFile = new File(dataFileName);

			if (!logFile.getParentFile().exists()) {
				logFile.getParentFile().mkdirs();
			}

			bufWriter = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void writeBufferedData(String data) {

		try {
			if (null != bufWriter) {
				bufWriter.write(data);
			} else {
				createDataFile();
				bufWriter.write(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void closeWriter() {
		try {
			if (null != bufWriter) {
				bufWriter.close();
				bufWriter = null;
			}
		} catch (IOException e) {
			bufWriter = null;
			e.printStackTrace();
		}
	}

}
