package com.ibg.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.uploaders.AcdmListUploader;
import com.ibg.data.uploaders.ErrorAnalyzerService;
import com.ibg.data.uploaders.SelectionRecUploadMgr;
import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.JdbcProductDAO;
import com.ibg.db.ServiceLocator;
import com.ibg.parsers.json.DateField;
import com.ibg.solr.json.AcdmExpandedViewDoc;
import com.ibg.solr.json.AcdmSimpleViewDoc;
import com.ibg.solr.json.AcdmSolrRecordExpandedView;
import com.ibg.solr.json.AcdmSolrRecordSimpleView;
import com.ibg.solr.json.SearchFields;
import com.ibg.solr.json.SolrKeyType;
import com.ibg.solr.json.SolrRecord;
import com.ibg.solr.json.SolrResponseData;
import com.ibg.solr.json.SolrResponseWorker;


public class Test extends ActiveJdbcCon {
	private static ClassPathXmlApplicationContext context = null;

	static {
		context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	final static Logger logger = LoggerFactory.getLogger(Test.class);
	static DBEnvConnections env;
	static DataSource dataSource;

	private static void uploadListMaster() {
		AcdmListUploader listUploaderAj = (AcdmListUploader) ServiceLocator.getBean("_opaplst");

		listUploaderAj.startDataUpload(null);
	}

	private static void listProduct() {
		JdbcProductDAO productDAO = (JdbcProductDAO) ServiceLocator.getBean("productDAO");
		productDAO.printAll();
	}

	private static void uploadSelectionRecords() {
		try {

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			DataSource dataSource = env.getDataSource("stage");
			
			SelectionRecUploadMgr selectionRecUploadMgr = ((SelectionRecUploadMgr) ServiceLocator.getBean("selRecUploadMgr"));
			selectionRecUploadMgr.setDataSource(dataSource);
			selectionRecUploadMgr.prepareListItemData();
		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		}

		
	}

	private static void analyzeErrorRecords() {

		ErrorAnalyzerService errorAnalyzer = ((ErrorAnalyzerService) ServiceLocator.getBean("errorAnalyzeService"));
		errorAnalyzer.setDataSource(dataSource);
		errorAnalyzer.prepareListItemData();
		
	}

	private static void testDateField(String date) {
		DateField df = new DateField();
		df.set$date(date);
		System.out.println(df.get$date());
	}

	private static void testGnmdComps() {

		String BASE_URL = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=";
		String BROWSE_ROW_CHECK_STATUS = "ipage/browserow.p&IDList=cart&user_id=?&cust-group=?&EANList=?&oidOrderList=?&eBookDetailList=?";

		GanimedeCallUrl gnmdUrl = new GanimedeCallUrl(BASE_URL, BROWSE_ROW_CHECK_STATUS);

		gnmdUrl.setString(2, "martin");
		gnmdUrl.setString(3, "11100");
		gnmdUrl.setString(4, "9781401237462,9781299187764,9783836549042,9781282292062");
		gnmdUrl.setString(5, ",,,");
		gnmdUrl.setString(6, ":,MIL:3,:,:");

		GanimedeCallUrl gnmdUrl1 = new GanimedeCallUrl(BASE_URL, BROWSE_ROW_CHECK_STATUS);

		gnmdUrl1.setString(2, "smorgan");
		gnmdUrl1.setString(3, "WAG320");
		gnmdUrl1.setString(4, "9781401237462,9781299187764,9783836549042,9781282292062");
		gnmdUrl1.setString(5, ",,,");
		gnmdUrl1.setString(6, ":,MIL:3,MIL:4,:");

		System.out.println("gnmdUrl: " + gnmdUrl.getGnmdUrl());
		System.out.println("gnmdUrl1: " + gnmdUrl1.getGnmdUrl());

	}

	public static void main(String[] args) {

		// testGnmdComps();

		// uploadListMaster();

		// uploadSelectionRecords();

		// analyzeErrorRecords();

		// testDateField("1352355228000");
		// testDateField("1354723481000");
		// testDateField("1354776470000");
		// testDateField("1386312470000");
		// testDateField("1379953270156");

		/*
		 * 
		 * Pattern oidIdListPattern = Pattern.compile("(.+?)\\|(.+?)");
		 * 
		 * String oidIdList = "ABCDSA|";
		 * 
		 * if (null != oidIdList && !oidIdList.isEmpty()) { if
		 * (!(oidIdListPattern.matcher(oidIdList).matches())) {
		 * System.out.println("not matches: " + oidIdList + "\n"); }else
		 * System.out.println("matches: " + oidIdList); }
		 */

		// System.out.println("shiv" + "|" + "jha");
		/*
		 * boolean stat = true; int ctr = 0; * ctr += stat ? 1: 0;
		 * System.out.println(ctr);
		 * 
		 * String [] listId = null; String oidIDList = "KKA3110";
		 * //"KKA3110|DN"; String oidContact = null;
		 * 
		 * try {
		 * 
		 * listId = oidIDList.split("\\|"); oidContact = listId[0]; oidIDList =
		 * listId[1]; listId = null;
		 * 
		 * } catch (Exception ex) { oidContact = null; oidIDList = null; }
		 * 
		 * System.out.printf("oidContact, oidIDList: %s,%s\n", oidContact,
		 * oidIDList);
		 */

		// solrJsonTest();
		// solrExpandedViewModelJsonTest();
		// solrSimpleViewModelJsonTest();
		
		try {

			env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			dataSource = env.getDataSource("stage");
		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		}

		solrExpandedViewModelJsonTest1();
		solrSimpleViewModelJsonTest1();

	}

	private static void solrJsonTest() {

		String searchEans = "9780553574470%20OR%209780687048915%20OR%209789991115610%20OR%209781402035678%20OR%209789264009424%20OR%209780802094551%20OR%209781568028507%20OR%209780872864795%20OR%209780787690533%20OR%209781741143331%20OR%209780020346241";

		String solrUrl = "http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=" + searchEans + "&fl="
				+ SearchFields.EXPANDED_VIEW_ACDM.getFields() + "&wt=json&omitHeader=true&sort=EAN asc";

		// solrUrl =
		// "http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=" +
		// searchEans + "&wt=json&omitHeader=true&sort=EAN asc";
		solrUrl = solrUrl.replaceAll(" ", "%20");

		@SuppressWarnings("unchecked")
		SolrResponseWorker<SolrResponseData> solrWorker = ((SolrResponseWorker<SolrResponseData>) ServiceLocator.getBean("solrRespWorker"));
		// solrWorker.setDataBeanType(SolrRecord.class);
		solrWorker.setUrl(solrUrl);

		System.out.println("###############################JSON with No Model Data Fetching###############################");

		try {
			SolrResponseData data = (SolrResponseData) solrWorker.getSolrResponse();

			// System.out.println(null != data ? data.toString() : "EMPTY");

			if (null != data) {
				// print the key set
				System.out.println("**********************************************");
				for (SolrKeyType obj : data.getSolrRespKeySet()) {
					System.out.println(obj.getKey() + ":" + obj.getDataType());
				}
				System.out.println("**********************************************");

				for (Map<String, List<String>> doc : data.getDocs()) {
					System.out.println("**********************************************");
					System.out.println(doc.keySet());
					// System.out.println(doc);
					System.out.println("EAN | ALT_EAN " + doc.get("EAN") + doc.get("ALT_EAN"));
					System.out.println("**********************************************");
				}
			}

		} catch (Exception ex) {
			System.out.println(ex);
		}

		System.out.println("##############################################################");

	}

	private static void solrExpandedViewModelJsonTest() {

		StringBuilder solrUrl = new StringBuilder();

		String searchEans = "9780553574470%20OR%209780687048915%20OR%209789991115610%20OR%209781402035678%20OR%209789264009424%20OR%209780802094551%20OR%209781568028507%20OR%209780872864795%20OR%209780787690533%20OR%209781741143331%20OR%209780020346241";

		solrUrl.append("http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=").append(searchEans);
		solrUrl.append("&fl=").append(SearchFields.EXPANDED_VIEW_ACDM.getFields());

		solrUrl.append("&wt=json");
		// solrUrl.append("&omitHeader=true");
		solrUrl.append("&sort=EAN asc");

		// solrUrl =
		// "http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=" +
		// searchEans + "&wt=json&omitHeader=true&sort=EAN asc";

		@SuppressWarnings({ "unchecked", "rawtypes" })
		SolrResponseWorker<SolrRecord> solrWorker = ((SolrResponseWorker<SolrRecord>) ServiceLocator.getBean("solrRespWorker"));
		solrWorker.setDataBeanType(SolrRecord.class);
		solrWorker.setUrl(solrUrl.toString().replaceAll(" ", "%20"));

		System.out.println("###############################JSON Expanded View Model Data Fetching###############################");

		try {
			@SuppressWarnings("unchecked")
			SolrRecord<AcdmExpandedViewDoc> data = (SolrRecord<AcdmExpandedViewDoc>) solrWorker.getSolrModelResponse();

			if (null != data) {
				// System.out.println("All Data: " + data);

				// System.out.println("header info starts");
				// System.out.println(data.getResponseHeader().getParams());
				// System.out.println("header info ends");
				// System.out.println("numFound: " +
				// data.getResponse().getNumFound() + ", start: " +
				// data.getResponse().getStart());

				// for(AcdmExpandedViewDoc doc: data.getResponse().getDocs()){

				// }

				List<AcdmExpandedViewDoc> docs = data.getResponse().getDocs();
				// System.out.println("All docs: " + docs);

				// for (Iterator<AcdmExpandedViewDoc> objs =
				// docs.listIterator(); objs.hasNext();) {
				// System.out.println(objs.next());
				// AcdmExpandedViewDoc obj = objs.next();
				// System.out.printf("%s,%s\n", obj.getEAN(), obj.getALT_EAN());

				// System.out.printf("%s\n", objs.next());
				// }

			}

		} catch (Exception ex) {
			System.out.println(ex);
		}

		System.out.println("##############################################################");

	}

	private static void solrSimpleViewModelJsonTest() {

		StringBuilder solrUrl = new StringBuilder();

		String searchEans = "9780553574470%20OR%209780687048915%20OR%209789991115610%20OR%209781402035678%20OR%209789264009424%20OR%209780802094551%20OR%209781568028507%20OR%209780872864795%20OR%209780787690533%20OR%209781741143331%20OR%209780020346241";

		solrUrl.append("http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=").append(searchEans);
		solrUrl.append("&fl=").append(SearchFields.SIMPLE_VIEW_ACDM.getFields());

		solrUrl.append("&wt=json");
		// solrUrl.append("&omitHeader=true");
		solrUrl.append("&sort=EAN asc");

		@SuppressWarnings({ "unchecked", "rawtypes" })
		SolrResponseWorker<SolrRecord> solrWorker = ((SolrResponseWorker<SolrRecord>) ServiceLocator.getBean("solrRespWorker"));
		solrWorker.setDataBeanType(SolrRecord.class);
		solrWorker.setUrl(solrUrl.toString().replaceAll(" ", "%20"));

		System.out.println("###############################JSON Simple Model Data Fetching###############################");

		try {
			@SuppressWarnings("unchecked")
			SolrRecord<AcdmSimpleViewDoc> data = (SolrRecord<AcdmSimpleViewDoc>) solrWorker.getSolrModelResponse();

			if (null != data) {
				// System.out.println("All Data: " + data);

				System.out.println("header info starts");
				System.out.println(data.getResponseHeader().getParams());
				System.out.println("header info ends");
				System.out.println("numFound: " + data.getResponse().getNumFound() + ", start: " + data.getResponse().getStart());

				List<AcdmSimpleViewDoc> docs = data.getResponse().getDocs();
				// System.out.println("All docs: " + docs);

				for (Iterator<AcdmSimpleViewDoc> docObj = docs.listIterator(); docObj.hasNext();)
					System.out.println(docObj.next().toString());

			}

		} catch (Exception ex) {
			System.out.println(ex);
		}

		System.out.println("##############################################################");

	}

	private static void solrExpandedViewModelJsonTest1() {

		StringBuilder solrUrl = new StringBuilder();

		String searchEans = "9780553574470%20OR%209780687048915%20OR%209789991115610%20OR%209781402035678%20OR%209789264009424%20OR%209780802094551%20OR%209781568028507%20OR%209780872864795%20OR%209780787690533%20OR%209781741143331%20OR%209780020346241";

		solrUrl.append("http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=").append(searchEans);
		solrUrl.append("&fl=").append(SearchFields.EXPANDED_VIEW_ACDM.getFields());

		solrUrl.append("&wt=json");
		// solrUrl.append("&omitHeader=true");
		solrUrl.append("&sort=EAN asc");

		@SuppressWarnings({ "unchecked" })
		SolrResponseWorker<AcdmSolrRecordExpandedView> solrWorker = ((SolrResponseWorker<AcdmSolrRecordExpandedView>) ServiceLocator.getBean("solrRespWorker"));
		solrWorker.setDataBeanType(AcdmSolrRecordExpandedView.class);
		solrWorker.setUrl(solrUrl.toString().replaceAll(" ", "%20"));

		System.out.println("###############################JSON Expanded View Model Data Fetching###############################");

		try {
			AcdmSolrRecordExpandedView data = (AcdmSolrRecordExpandedView) solrWorker.getSolrModelResponse();

			if (null != data) {
				System.out.println("All Data: " + data);

				System.out.println("header info starts");
				System.out.println(data.getResponseHeader().getParams());
				System.out.println("header info ends");
				System.out.println("numFound: " + data.getResponse().getNumFound() + ", start: " + data.getResponse().getStart());

				for (AcdmExpandedViewDoc doc : data.getResponse().getDocs()) {
					//System.out.println(doc.toString());
					System.out.printf("%s,%s,%s\n",doc.getEAN(), doc.getISBN(), doc.getALT_EAN());

				}

			}

		} catch (Exception ex) {
			System.out.println(ex);
		}

		System.out.println("##############################################################");

	}
	
	private static void solrSimpleViewModelJsonTest1() {

		StringBuilder solrUrl = new StringBuilder();

		String searchEans = "9780553574470%20OR%209780687048915%20OR%209789991115610%20OR%209781402035678%20OR%209789264009424%20OR%209780802094551%20OR%209781568028507%20OR%209780872864795%20OR%209780787690533%20OR%209781741143331%20OR%209780020346241";

		solrUrl.append("http://solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?q=").append(searchEans);
		solrUrl.append("&fl=").append(SearchFields.SIMPLE_VIEW_ACDM.getFields());

		solrUrl.append("&wt=json");
		// solrUrl.append("&omitHeader=true");
		solrUrl.append("&sort=EAN asc");

		@SuppressWarnings({ "unchecked" })
		SolrResponseWorker<AcdmSolrRecordSimpleView> solrWorker = ((SolrResponseWorker<AcdmSolrRecordSimpleView>) ServiceLocator.getBean("solrRespWorker"));
		solrWorker.setDataBeanType(AcdmSolrRecordSimpleView.class);
		solrWorker.setUrl(solrUrl.toString().replaceAll(" ", "%20"));

		System.out.println("###############################JSON Simple Model Data Fetching###############################");

		try {
			AcdmSolrRecordSimpleView data = (AcdmSolrRecordSimpleView) solrWorker.getSolrModelResponse();

			if (null != data) {
				// System.out.println("All Data: " + data);

				System.out.println("header info starts");
				System.out.println(data.getResponseHeader().getParams());
				System.out.println("header info ends");
				System.out.println("numFound: " + data.getResponse().getNumFound() + ", start: " + data.getResponse().getStart());

				for (AcdmSimpleViewDoc doc : data.getResponse().getDocs()) {
					//System.out.println(doc.toString());
					System.out.printf("%s,%s,%s\n",doc.getEAN(), doc.getISBN(), doc.getALT_EAN());

				}

			}

		} catch (Exception ex) {
			System.out.println(ex);
		}

		System.out.println("##############################################################");

	}
}
