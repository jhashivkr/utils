package com.ibg.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadErrorTest {

	// private static Pattern infoPattern =
	// Pattern.compile("(.+?)List Id does not exists for: (.+?)");

	private ReadErrorTest() {

	}

	private void readErrorFileAndLoadData() {

		Pattern infoPattern = Pattern.compile("List Id does not exists for:(.+?)");

		String[] files = { "201311221148 - 81200_T_00.json.log", "20131122131 - 51186000_T_00.json.log", "201311221444 - 75283000_T_00.json.log",
				"201311221545 - ACU_T_00.json.log", "201311221648 - SIMFRAS_T_00.json.log", "20131122169 - NOVA_T_00.json.log",
				"20131122171 - SIMFRAS_T_01.json.log", "201311251037 - 11100_T_00.json.log", "20131125115 - 11100_T_01.json.log",
				"201311251219 - 57110_T_01.json.log", "201311251354 - 57110_T_00.json.log", "201311251416 - 53068000_T_00.json.log",
				"201311251442 - WAG320_T_00.json.log" };

		List<String> fileLst = Arrays.asList(files);
		String errorFlatFile = null;
		String errorCSV = null;
		String[] errFlName = null;
		String sCurrentLine;
		String[] result = null;
		Matcher matcher = null;
		int lineCtr = 1;
		StringBuilder strBlder = null;
		FileWriter writer = null;
		FileReader errorFileReader = null;
		BufferedReader bufferReader = null;
		BufferedWriter bufWriter = null;

		try {

			for (String file : fileLst) {
				lineCtr = 1;
				errFlName = null;
				errorFileReader = null;
				bufferReader = null;
				writer = null;
				bufWriter = null;

				errorFlatFile = "E:/Ingram/Tasks/Academics-OASIS-Migration/json-selection-records-data/json/logs/" + file;
				errFlName = ((file.split("-"))[1].trim()).split(".json");
				errorCSV = "E:/Ingram/Tasks/Academics-OASIS-Migration/json-selection-records-data/json/log_csv/" + errFlName[0] + ".csv";

				System.out.println("Start Reading error file -- " + errorFlatFile);

				errorFileReader = new FileReader(errorFlatFile);
				bufferReader = new BufferedReader(errorFileReader);

				strBlder = new StringBuilder();
				strBlder.append("#").append(",").append("_id").append(",").append("oidContact").append(",").append("oidIDList").append("\n");

				bufferReader.readLine();

				while ((sCurrentLine = bufferReader.readLine()) != null) {
					if (sCurrentLine.endsWith("\n"))
						sCurrentLine = sCurrentLine + " ";
					result = sCurrentLine.split(",");

					// if (null != result && sCurrentLine.trim().length() > 0) {
					if (sCurrentLine.trim().length() > 0 && null != result && result.length > 2) {

						if ((null != result[1] && !result[1].isEmpty()) && (null != result[2] && !result[2].isEmpty())) {
							if (infoPattern.matcher(removeQuotes(result[1].trim())).matches()) {
								matcher = infoPattern.matcher(removeQuotes(result[1].trim()));
								result[1] = matcher.replaceAll("");
								strBlder.append(lineCtr++).append(",").append(result[0]).append(",").append(result[1]).append(",").append(result[2])
										.append("\n");
							}
						}// if
					}// if
				}// while

				// dump all the exception messages
				// errorFileReader.

				System.out.println("Start creating error csv -- " + errorCSV);
				writer = new FileWriter(new File(errorCSV));
				bufWriter = new BufferedWriter(writer);

				bufWriter.write(strBlder.toString());

				bufWriter.close();
				writer.close();

			}// for
		} catch (Exception e) {
			System.out.println(e.toString());
			System.out.println(strBlder.toString());
		} finally {
			try {
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		System.out.println("-- END --");
	} // End Method

	private String removeQuotes(String input) {
		if (input != null) {
			if (input.startsWith("\"") && input.endsWith("\""))
				input = input.substring(1, input.length() - 1);
			return input.trim();
		}
		return null;
	}

	public static void main(String[] args) {
		new ReadErrorTest().readErrorFileAndLoadData();
	}
}
