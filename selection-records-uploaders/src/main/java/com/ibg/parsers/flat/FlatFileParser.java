package com.ibg.parsers.flat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FlatFileParser {

	private BufferedWriter bufferWriter;
	private String dataFileName = null;
	private List<String> flatFileFields = null;

	public FlatFileParser() {

	}

	public FlatFileParser(String dataFileName, List<String> flatFileFields) {
		this.dataFileName = dataFileName;
		this.flatFileFields = flatFileFields;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public void setFlatFileFields(List<String> flatFileFields) {
		this.flatFileFields = flatFileFields;
	}

	public void setBufferWriter(BufferedWriter bufferWriter) {
		this.bufferWriter = bufferWriter;
	}

	public List<Map<String, String>> getData() {
		return readNTagFileData();
	}

	private List<Map<String, String>> readNTagFileData() {
		int rowNumber = 0;
		int insertdRows = 0;
		boolean errorFound = false;
		Map<String, String> rowData = null;
		List<Map<String, String>> tableData = new LinkedList<Map<String, String>>();

		BufferedReader br = null;

		try {

			bufferWriter.write(new Date() + " ===== START Reading data from File=" + dataFileName + "\n");
			System.out.println(new Date() + " ===== START Reading data from File=" + dataFileName);

			br = new BufferedReader(new FileReader(dataFileName));
			String sCurrentLine;
			String[] result = null;

			while ((sCurrentLine = br.readLine()) != null) {
				rowNumber++;
				if (sCurrentLine.endsWith("\t"))
					sCurrentLine = sCurrentLine + " ";
				result = sCurrentLine.split("\\t");

				if (result.length == flatFileFields.size()) {

					rowData = new LinkedHashMap<String, String>();
					int position = 0;
					String data = "";

					for (String key : flatFileFields) {
						data = removeQuotes(result[position++]);
						data = "TRUE".equalsIgnoreCase(data) ? "true" : ("FALSE".equalsIgnoreCase(data) ? "false":data);
						//rowData.put(key, removeQuotes(result[position++]));
						rowData.put(key, data);
					}
					tableData.add(rowData);
					insertdRows++;
				} else {
					errorFound = true;
					bufferWriter.write(rowNumber + " ROWNUMBER. COLUMNS SHOULD BE " + flatFileFields.size() + " BUT this row contains "
							+ result.length + ".\n Complete row=" + sCurrentLine + "\n");
				}

			}

			br.close();

			if (errorFound) {
				bufferWriter.write("Errors found in parsing : " + dataFileName + "\n");
				System.out.println("Errors found in parsing : " + dataFileName);
			}

			bufferWriter.write(new Date() + " ===== END Loading TABLE. TOTAL ROWS TO BE INSERTED =" + insertdRows + "\n");
			System.out.println(new Date() + " ===== END Loading TABLE. TOTAL ROWS TO BE  INSERTED =" + insertdRows);

		} catch (Exception e) {
			System.err.println("ERROR occured at line Number=" + rowNumber + ", Error=" + e.toString());
			try {
				bufferWriter.write("ERROR occured at line Number=" + rowNumber + ", Error=" + e.toString() + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return tableData;

	}

	private String removeQuotes(String input) {
		if (input != null) {
			if (input.startsWith("\"") && input.endsWith("\""))
				input = input.substring(1, input.length() - 1);
			return input.trim();
		}
		return null;
	}
}
