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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.db.ServiceLocator;
import com.ibg.file.loader.CollectFiles;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.FlatFileProps;
import com.ibg.utils.PropertyReader;

public class LCDEweyNLMSolrAnalysis {

	public static final Pattern optionPattern = Pattern.compile("<option value=\\s*");
	public static final Pattern noOptionPattern = Pattern.compile("<option value=\"No\"");
	public static final Pattern valuePattern = Pattern.compile("\\bvalue=\\b");

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
			String htmlFileName = prop.getLcDataHome() + "\\html\\dewey_solr_qry.html";
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
		new LCDEweyNLMSolrAnalysis().startLoadProcess();
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
			StringBuilder qbldr = new StringBuilder();

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
						// if option pattern matches
						String qstr = "";
						if (optionPattern.matcher(sCurrentLine).find() && !noOptionPattern.matcher(sCurrentLine).find()) {
							// find the indexes of =" and ">
							int st = sCurrentLine.indexOf('=') + 2;
							int end = sCurrentLine.indexOf('>');
							// get the value of the option
							String val = sCurrentLine.substring(st, end - 1) + "  ";
							// for forming LC / DW query
							// if classification system is LC

							if (val.contains("TO")) {
								String[] str = val.split("TO");
								qstr = str[0].trim() + " TO " + str[1].trim();
							} else {
								qstr = val.trim();
								qstr = qstr + " TO " + qstr;
							}
							//for LC and NLM
							//qstr = "LC_CL_PRIMARY: [" + qstr + "]";
							
							//for dewey
							qstr = "SJCG_DEWY_DEC_NBR: [" + qstr + "]";

							long numFound = solrUtility(qstr);
							System.out.println("numFound: " + numFound);

							qbldr.delete(0, qbldr.length());
							// query
							qbldr.append("http://solr-ipage-qa.ingramtest.com:8080/solr/ipage/select?wt=json&indent=on&q=").append(qstr);
							qbldr.append("&fl=EAN,Title\t");
							qbldr.append(numFound);
							sb.append(qbldr.toString()).append('\n');

						}

					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}

					errorFound = true;

				}// while

				br.close();

				if (errorFound) {
					bufferWriter.write("Errors found in parsing : " + dataFileName + "\n");
				}

				bufferWriter.write(new Date() + " ===== END Loading TABLE. TOTAL ROWS FOUND =" + insertdRows + "\n");

			} catch (Exception e) {
				e.printStackTrace();
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

	private long solrUtility(String q) throws SearchException {

		SolrQuery qry = new SolrQuery();
		QueryResponse response = null;
		SolrJServerFactory solrJServerFactory = (SolrJServerFactory) ServiceLocator.getApplicationContext().getBean("solrServerFactory");
		SolrServer server;

		String searchEans;
		int totEans;

		server = solrJServerFactory.getDefaultInstance();

		try {

			qry.set("q", q);
			qry.addField("EAN");

			long requestStart = System.nanoTime();
			try {
				// System.out.println("qry: " + qry);
				qry.setTimeAllowed(10000);
				response = server.query(qry, METHOD.POST);

				return response.getResults().getNumFound();

			} catch (Exception e) {
				e.printStackTrace();
			}

			long requestStop = System.nanoTime();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.shutdown();
		}

		return 0;
	}
}
