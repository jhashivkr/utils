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

public class NLMHtmlFileLoaderTester {

	public static final Pattern optionPattern = Pattern.compile("<option>\\s*");
	public static final Pattern selectPattern = Pattern
			.compile("<select id=\\s*");

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"academic-applicationContext.xml");
	}

	private PropertyReader prop;
	private BufferedWriter htmlBufWriter = null;
	private List<String> selectList;

	public void startLoadProcess() {

		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
		selectList = new LinkedList<String>();

		try {
			String htmlFileName = prop.getLcDataHome()
					+ "\\html\\nlm_html.html";
			htmlBufWriter = new BufferedWriter(new FileWriter(new File(
					htmlFileName)));

			LCFileCollector doneFiles = new LCFileCollector();
			Set<File> lcDataFiles = doneFiles.getLcFiles();

			System.out.println("nlmDataFiles: " + lcDataFiles);

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

	}

	private void loadData(File file, String obj) {

		BufferedWriter bufWriter = null;

		try {

			String logFileName = prop.getFileParserLog() + obj + "-.log";

			bufWriter = new BufferedWriter(
					new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator
					.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields(obj)
					.split("\\,"));

			FlatFileParser parser = new FlatFileParser(file.getAbsolutePath(),
					flds);
			parser.setBufferWriter(bufWriter);

			parser.getData();

			bufWriter.close();

			htmlBufWriter.write(parser.getHtmlString());

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
		new NLMHtmlFileLoaderTester().startLoadProcess();
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

				Set<File> listFiles = new LinkedHashSet<File>(
						Arrays.asList(location.listFiles()));
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
				if (FileFilterUtil.txt.equalsIgnoreCase(FileFilterUtil
						.getExtension(file.getAbsolutePath()))) {
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

		private Pattern wordLetterSplitPat = Pattern
				.compile("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		private Pattern letterPat = Pattern.compile("\\b\\d*");
		private Pattern numberPat = Pattern.compile("\\d+");

		public FlatFileParser(String dataFileName, List<String> flatFileFields) {
			this.dataFileName = dataFileName;
			this.flatFileFields = flatFileFields;
		}

		public void setBufferWriter(BufferedWriter bufferWriter) {
			this.bufferWriter = bufferWriter;
		}

		public void getData() {
			readNTagFileData();
		}

		public String getHtmlString() {
			return sb.toString();
		}

		private void readNTagFileData() {
			int rowNumber = 0;
			int insertdRows = 0;
			boolean errorFound = false;
			Map<String, String> rowData = null;
			List<Map<String, String>> tableData = new LinkedList<Map<String, String>>();
			sb = new StringBuilder();

			BufferedReader br = null;

			try {

				bufferWriter.write(new Date()
						+ " ===== START Reading data from File=" + dataFileName
						+ "\n");
				System.out
						.println(new Date()
								+ " ===== START Reading data from File="
								+ dataFileName);

				br = new BufferedReader(new FileReader(dataFileName));
				String sCurrentLine;
				String[] result = null;

				while ((sCurrentLine = br.readLine()) != null) {
					rowNumber++;
					if (sCurrentLine.endsWith("\t")) {
						sCurrentLine = sCurrentLine + " ";
					}

					try {
						if (selectPattern.matcher(sCurrentLine).find()) {
							// find the indexes of =" and ">
							int end = sCurrentLine.indexOf('>');
							StringBuilder bldr = new StringBuilder(sCurrentLine);
							bldr.insert(end, " class=\"nlmSubIndex1\"");
							sCurrentLine = bldr.toString();
						}
						// if option pattern matches
						else if (optionPattern.matcher(sCurrentLine).find()) {
							// blow up by spaces
							String[] strSplit = sCurrentLine.split("\\ ");
							// the index 1 and 2 are code
							String option = "<option value=\"" + strSplit[1]
									+ strSplit[2] + "\">";
							int ctr = 1;
							for (; ctr < strSplit.length;) {
								option += " " + strSplit[ctr++];
							}
							sCurrentLine = option;
							// System.out.println(bldr.toString());

						}

						sb.append(sCurrentLine).append('\n');
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}

					errorFound = true;

				}// while

				br.close();

				if (errorFound) {
					bufferWriter.write("Errors found in parsing : "
							+ dataFileName + "\n");
				}

				bufferWriter.write(new Date()
						+ " ===== END Loading TABLE. TOTAL ROWS FOUND ="
						+ insertdRows + "\n");

			} catch (Exception e) {
				e.printStackTrace();
				try {
					bufferWriter.write("ERROR occured at line Number="
							+ rowNumber + ", Error=" + e.toString() + "\n");
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
