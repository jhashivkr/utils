package com.ibg.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.ibg.db.ServiceLocator;
import com.ibg.file.loader.CollectFiles;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.FlatFileProps;
import com.ibg.utils.PropertyReader;

public class LCFileLoaderTester {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	private PropertyReader prop;
	private BufferedWriter htmlBufWriter = null;
	private List<String> selectList;

	public void startLoadProcess() {

		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
		selectList = new LinkedList<String>();

		try {
			String htmlFileName = prop.getLcDataHome() + "\\html\\lc_html.html";
			htmlBufWriter = new BufferedWriter(new FileWriter(new File(htmlFileName)));

			LCFileCollector doneFiles = new LCFileCollector();
			Set<File> lcDataFiles = doneFiles.getLcFiles();

			System.out.println("lcDataFiles: " + lcDataFiles);

			for (File lcFile : lcDataFiles) {
				try {

					loadData(lcFile, "_lc");

				} catch (Exception e) {
					System.out.println("Exception: " + e);
				}
			}
			htmlBufWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//System.out.println("selects: \n" + selectList);

	}

	private void loadData(File file, String obj) {

		BufferedWriter bufWriter = null;

		try {

			String logFileName = prop.getFileParserLog() + obj + "-.log";

			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields(obj).split("\\,"));

			FlatFileParser parser = new FlatFileParser(file.getAbsolutePath(), flds);
			parser.setBufferWriter(bufWriter);

			List<Map<String, String>> data = parser.getData();

			bufWriter.close();

			htmlBufWriter.write(parser.getHtmlString());

			//System.out.println("html: " + parser.getHtmlString());
			

			// print the data
			// for (Map<String, String> rowData : data) {
			// System.out.println(rowData);
			// }

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		} finally {
			try {
				if (bufWriter != null) {
					bufWriter.close();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new LCFileLoaderTester().startLoadProcess();
	}

	private class LCFileCollector extends CollectFiles<FileFilter> {

		private Set<File> doneFiles = new LinkedHashSet<File>();

		public LCFileCollector() {

		}

		public Set<File> getLcFiles() {
			getAllLcFiles();
			return doneFiles;
		}

		protected void getAllLcFiles() {

			String startLocation = null;

			startLocation = prop.getLcDataHome();
			if (null == startLocation) {
				System.out.print(USER_MSG);
				System.out.flush();
				startLocation = scanner.nextLine();
			}

			if (null != startLocation && (startLocation.trim().length() > 0)) {
				getAllDataFiles(startLocation);
				return;
			} else {
				System.out.println("Invalid Input ... Try Again");
				getAllLcFiles();
			}

		}

		protected void loopThruAndGetDataFiles(File location) {

			try {

				Set<File> listFiles = new LinkedHashSet<File>(Arrays.asList(location.listFiles()));
				for (File file : listFiles) {

					if (file.isFile()) {
						addFile(file);
					} else if (file.isDirectory()) {
						loopThruAndGetDataFiles(file);
					}

				}// for
			} catch (Exception ex) {
				System.out.println("Error from DoneFileCollector: " + ex);
			}

			return;

		}

		private void addFile(File file) {
			if (null != file) {
				if (FileFilterUtil.txt.equalsIgnoreCase(FileFilterUtil.getExtension(file.getAbsolutePath()))) {
					doneFiles.add(file);
				}
			}
		}

	}// LCFileCollector

	private class FlatFileParser {

		private BufferedWriter bufferWriter;
		private String dataFileName = null;
		private List<String> flatFileFields = null;
		private StringBuilder sb;

		private Pattern wordLetterSplitPat = Pattern.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		private Pattern letterPat = Pattern.compile("\\b\\d*");
		private Pattern numberPat = Pattern.compile("\\d+");

		public FlatFileParser(String dataFileName, List<String> flatFileFields) {
			this.dataFileName = dataFileName;
			this.flatFileFields = flatFileFields;
		}

		public void setBufferWriter(BufferedWriter bufferWriter) {
			this.bufferWriter = bufferWriter;
		}

		public List<Map<String, String>> getData() {
			return readNTagFileData();
		}

		public String getHtmlString() {
			sb.append("</select>\n");
			return sb.toString();
		}

		private List<Map<String, String>> readNTagFileData() {
			int rowNumber = 0;
			int insertdRows = 0;
			boolean errorFound = false;
			Map<String, String> rowData = null;
			List<Map<String, String>> tableData = new LinkedList<Map<String, String>>();
			sb = new StringBuilder();

			BufferedReader br = null;

			try {

				bufferWriter.write(new Date() + " ===== START Reading data from File=" + dataFileName + "\n");
				System.out.println(new Date() + " ===== START Reading data from File=" + dataFileName);

				br = new BufferedReader(new FileReader(dataFileName));
				String sCurrentLine;
				String[] result = null;

				while ((sCurrentLine = br.readLine()) != null) {
					rowNumber++;
					if (sCurrentLine.endsWith("\t")) {
						sCurrentLine = sCurrentLine + " ";
					}

					try {
						result = sCurrentLine.split("\\t");
					} catch (Exception e) {
						continue;
					}

					rowData = new LinkedHashMap<String, String>();
					if (result.length == flatFileFields.size()) {
						int position = 0;
						String data = "";

						// split key on - and store as solr range, ex - AC1 TO
						// AC999
						String[] keyArr = result[0].split("\\-");
						if (null != keyArr) {
							try {
								
								
								keyArr[0] = removePrimaryPrntheses(keyArr[0]);
								if (letterPat.matcher(keyArr[0]).find()) {
									String[] splt = keyArr[0].split(wordLetterSplitPat.pattern());
									result[0] = keyArr[0] + " TO " + splt[0] + removePrimaryPrntheses(keyArr[1]);
								}
								result[1] = result[0] + " - " + result[1];
							} catch (Exception e) {
							}

						}

						sb.append("<option value=\"").append(result[0]).append("\">").append(result[1]).append("</option>\n");

						for (String key : flatFileFields) {
							data = removeQuotes(result[position++]);
							rowData.put(key, data);
						}
						tableData.add(rowData);
						insertdRows++;

					}// if
						// for sub class headers
					else if (result.length < flatFileFields.size() && (null != result[0] && !result[0].isEmpty())) {
						rowData.put(flatFileFields.get(0), removeQuotes(result[0]));
						tableData.add(rowData);
						insertdRows++;

						if (sb.length() > 0) {
							sb.append("</select>\n");
						}

						selectList.add(result[0].split(" ")[1]);
						
						// result[0] = WordUtils.capitalizeFully(result[0]);
						result[0] = result[0].replaceAll("\\s+", "");

						sb.append("<select id=\"LC").append(result[0]).append("\" class=\"lcSubclass\">\n");
						
						sb.append("<option>No Selection</option>\n");
						
					} else {
						errorFound = true;
						bufferWriter.write(rowNumber + " ROWNUMBER. COLUMNS SHOULD BE " + flatFileFields.size() + " BUT this row contains "
								+ result.length + ".\n Complete row=" + sCurrentLine + "\n");
					}

				}// while

				br.close();

				if (errorFound) {
					bufferWriter.write("Errors found in parsing : " + dataFileName + "\n");
				}

				bufferWriter.write(new Date() + " ===== END Loading TABLE. TOTAL ROWS FOUND =" + insertdRows + "\n");

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

		private String removePrimaryPrntheses(String input) {
			if (null != input) {
				input = input.replaceAll("\\(", "");
				input = input.replaceAll("\\)", "");

				return input.trim();
			}
			return null;
		}
	}

}
