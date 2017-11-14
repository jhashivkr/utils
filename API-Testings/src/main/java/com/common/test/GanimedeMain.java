package com.common.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.endeca.navigation.ERecSearch;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;
import com.endeca.navigation.UrlGen;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.legstar.coxb.transform.HostTransformException;
import com.legstar.host.invoke.HostInvokerException;
import com.legstar.messaging.LegStarAddress;

import ibg.academics.administration.dto.CisUser;
import ibg.academics.administration.dto.UserConfigField;
import ibg.academics.administration.dto.UserConfigOption;
import ibg.academics.administration.service.UserAdminService;
import ibg.academics.administration.service.UserConfigService;
import ibg.academics.auth.dto.AcademicArrowActions;
import ibg.academics.cis.queue.writer.AcdmListCreator;
import ibg.academics.dao.AcademicOrderInfoDao;
import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicOrderInfo;
import ibg.academics.enums.SortByFields;
import ibg.academics.order.OrderDetail;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.accountprofile.accountinfo.service.AccountInfoService;
import ibg.accountprofile.service.AccountProfileInfoService;
import ibg.actions.product.search.acdm.SearchHelper;
import ibg.administration.CISUserLoginData;
import ibg.browserow.dto.AcademicItemData;
import ibg.cc.mq.writers.StringPadder;
import ibg.common.AcdmTitlePriceObj;
import ibg.common.DBUtility;
import ibg.common.MarketSegment;
import ibg.common.SortType;
import ibg.common.VariableData;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.common.tibco.MessagePublisher;
import ibg.common.util.DateUtility;
import ibg.common.util.SelectableOptions;
import ibg.customer.creditstatus.eb036.EB036Input;
import ibg.customer.electronicpayment.eb059.EB059Input;
import ibg.customer.electronicpayment.eb059.EB059Output;
import ibg.customer.electronicpayment.service.ElectronicPaymentService;
import ibg.customer.report.academics.selectables.ProfileFromToSelectables;
import ibg.customer.report.academics.selectables.ProfileSelectables;
import ibg.customer.selectionlist.details.ListDetails;
import ibg.customer.selectionlist.details.ListDetailsService;
import ibg.cybersource.creditcard.services.CyberSourceCommonService;
import ibg.data.common.ProductTypes;
import ibg.data.customer.selectionlist.SelectionListDetailDAO;
import ibg.data.endeca.NValueDAO;
import ibg.ganimede.handler.OrderDetailResultHandler;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.lib.activity.BrowseRowGnmdUrls;
import ibg.lib.activity.EanCheckStatusGnmdUrls;
import ibg.lib.activity.GanimedeThreadExecutor;
import ibg.lib.activity.HttpCallUrl;
import ibg.lib.activity.ReqCallback;
import ibg.lib.activity.ResponseMapPurger;
import ibg.lib.activity.auth.PopulateUserRoleActions;
import ibg.lib.activity.browserowp.db.objects.ItemActionInfoObj;
import ibg.lib.activity.browserowp.db.objects.ItemBrowseRowTabDataObj;
import ibg.lib.activity.browserowp.db.objects.JsonCheckStatusObj;
import ibg.lib.activity.browserowp.db.response.ObjectMapperWraper;
import ibg.lib.activity.browserowp.db.response.ResponseMapService;
import ibg.lib.activity.browserowp.db.response.ResponsePurgeList;
import ibg.nacdm.selectionlist.service.SelectionListDataService;
import ibg.nonacademics.service.MigrationServiceLocator;
import ibg.power.helper.PowerSearchDataFactory;
import ibg.product.information.EBPDataObject;
import ibg.product.information.ProductCisDetails;
import ibg.product.search.AcdmFilterHelper;
import ibg.product.search.Br1101Helper;
import ibg.product.search.ListAcdmDataMap;
import ibg.product.search.ProductPriceUtility;
import ibg.product.search.UserEbookDispOptions;
import ibg.product.search.acdm.SearchConstants;
import ibg.product.search.acdm.SolrQueryAcdm;
import ibg.product.search.acdm.SolrQueryReuseService;
import ibg.publisher.companyinformation.service.CompanyInformationService;
import ibg.publisher.offerdetail.service.PublisherOfferDetailService;
import ibg.publisher.po.order.header.service.PublisherPoOrderHdrService;
import ibg.publisher.publisherinformation.eb002.EB002Input;
import ibg.publisher.publisherinformation.eb002.EB002Output;
import ibg.publisher.publisherinformation.eb072.EB072Input;
import ibg.publisher.publisherinformation.eb073.EB073Input;
import ibg.publisher.publisherinformation.eb073.EB073Output;
import ibg.publisher.publisherinformation.eb074.EB074Input;
import ibg.publisher.publisherinformation.eb074.EB074Output;
import ibg.publisher.publisherinformation.eb074.Eb074__details1;
import ibg.publisher.publisherinformation.eb074.Eb074__details2;
import ibg.publisher.publisherinformation.eb074.Eb074__details3;
import ibg.publisher.publisherinformation.eb075.EB075Input;
import ibg.publisher.publisherinformation.eb075.EB075Output;
import ibg.publisher.publisherinformation.eb076.EB076Input;
import ibg.publisher.publisherinformation.eb076.EB076Output;
import ibg.publisher.publisherinformation.eb077.EB077Input;
import ibg.publisher.publisherinformation.eb077.EB077Output;
import ibg.publisher.publisherinformation.eb082.EB082Input;
import ibg.publisher.publisherinformation.eb082.Eb082__details1;
import ibg.publisher.publisherinformation.eb083.EB083Input;
import ibg.publisher.publisherinformation.eb083.EB083Output;
import ibg.publisher.publisherinformation.eb083.Eb083__details;
import ibg.publisher.publisherinformation.eb083.Ibeb083NProgramInvoker;
import ibg.publisher.publisherinformation.eb084.EB084Input;
import ibg.publisher.publisherinformation.eb084.EB084Output;
import ibg.publisher.publisherinformation.eb084.Ibeb084NProgramInvoker;
import ibg.publisher.publisherinformation.services.PublisherService;
import ibg.publisher.simplifiededi.eb030.EB030Input;
import ibg.publisher.simplifiededi.eb031.EB031Input;
import ibg.publisher.simplifiededi.eb031.EB031Output;
import ibg.publisher.simplifiededi.eb032.EB032Input;
import ibg.publisher.simplifiededi.eb032.EB032Output;
import ibg.publisher.simplifiededi.eb032.Ibeb032NProgramInvoker;
import ibg.publisher.simplifiededi.eb034.EB034Input;
import ibg.publisher.simplifiededi.eb034.EB034Output;
import ibg.publisher.simplifiededi.eb034.Ibeb034NProgramInvoker;
import ibg.publisher.simplifiededi.eb035.EB035Input;
import ibg.publisher.summary.service.EB023InputWrapper;
import ibg.publisher.summary.service.EB077InputWrapper;
import ibg.publisher.summary.service.PubCategoryRemainderService;
import ibg.publisher.summary.service.PublisherSummaryService;
import ibg.publisher.summaryinfo.eb023.EB023Input;
import ibg.publisher.titletrackingreport.TitleSubfieldObj;
import ibg.publisher.tradecd.service.TradeClassificationCodeService;
import ibg.selectionlist.SelectionList;
import ibg.selectionlist.SelectionListItem;
import ibg.selectionlist.SelectionListItemID;
import ibg.selectionlist.pojos.SelList;
import ibg.selectionlist.pojos.SelListItem;
import solr.api.SchemaEntry;
import solr.api.SchemaLoader;
import solr.api.SearchConfiguration;
import solr.api.SearchException;
import solr.api.endeca.NValueRepositoryLoader;
import solr.api.endeca.RecordFilterExpressionParser;

public class GanimedeMain {

	private static ClassPathXmlApplicationContext context = null;
	@Autowired
	private AcademicOrderInfoDao orderInfoDao;

	public static void main(String[] args) {

		// context = new
		// ClassPathXmlApplicationContext("academic-applicationContext.xml");
		context = new ClassPathXmlApplicationContext("migration-application-context.xml");

		//
		// powerSearch();
		// checkStatusTest();
		// loginCallTest();
		// loginCallTestValues();
		// loginCallTestSummary();
		// loginCallTestDependencies();
		// loginCallTestLogin();

		// profileTab();
		// powerSearchData();
		// dewey();

		// testMrcDownload();

		// printPrePub();

		// userSettingsEligibilityIstCall("umichadmin");// fbr1309

		// browseRowCheckStatusTest();

		// couttsDemandTest();

		// interdisciplinary("icareastudies"),
		// readershiplevel("icreadership"),
		// dewey("icdcsubject"),
		// lc("iclcsubject"),
		// binding("sfbt"),
		// country("country"),
		// lang("language");
		// powerSearchDataTest("icareastudies");

		// sampleTest();
		// testSolrResult();
		// testNValLoader();

		// loadSchema();
		// testRecordFilterExpression();

		// testBrowseRowDataHistory();
		// testBrowseRowDataFlow();

		// new TestBrowseRowProductCreation();

		// new TestProductEBook();

		// testUserConfig();
		// testListItemPopulation();

		// searchOrderTest();

		// productPriceTest();

		// productCisEbpGroupTest();
		// productEbookTest();
		// testSRSortFilter();
		// testListDisplay();
		// testSearchOrderDataPop();

		// testSROrderInfo();

		// "NATALIEB",urlg.getParam("listTypeId"),"10537",
		// AcademicList academicList = null;
		// AcademicListService service = (AcademicListService)
		// AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		// academicList = service.getAcademicListByUser("CROSSIN", "UD",
		// "1312508");

		// academicList = service.getAcademicList(1312508l);

		// List<AcademicListItem> academicItemList =
		// service.getAllBylistId(1312508l);
		// System.out.println("academicItemList: " + academicItemList);

		// Map<Long, AcademicItemData> academicItemDataMap =
		// ListAcdmDataMap.getAcademicItemDataMap(academicList);
		// System.out.println("academicItemDataMap: " + academicItemDataMap);

		String url = "Sf=Binding%20Formatfilter_data_sepContainsfilter_data_seppaperback&dobj=09/27/2014_12/12/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=PUBDT&Ns=TTL_PUB_DT&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode matchany|mode matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		url = "Sf=Fundfilter_data_sepContainsfilter_data_sepabcdfilter_rec_sepOrdered Byfilter_data_sepContainsfilter_data_sepshivfilter_rec_sepSelected Byfilter_data_sepContainsfilter_data_sepshiv&dobj=09/27/2014_12/12/2014&sortSrcType=3&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=PUBDT&Ns=Action&N=0&Nso=0&listTypeId=&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		url = "Sf=Copyright Yearfilter_data_sepLess Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/27/2014_12/12/2014&sortSrcType=3&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=PUBDT&Ns=Action&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		url = "Sf=Copyright Yearfilter_data_sepGreater Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/27/2014_12/12/2014&sortSrcType=2&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		url = "Sf=Copyright Yearfilter_data_sepGreater Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/27/2014_12/12/2014&sortSrcType=2&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		UrlGen urlg = new UrlGen(url, "UTF-8");

		// simple list display with basic sorting

		// fund sort test
		url = "dobj=01/01/2014_12/31/2014&sortSrcType=1&sortOrder=Title&Ns=Title&N=0&Nso=0&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");
		// TestListSortingFilter.testListPrepFlow("KATHYB","IN","0001","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url, "", "");

		// TestListSortingFilter.testListPrepFlow("JFU2707","SR","48105000","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url, "", "");

		// Contrib_1

		// url =
		// "dobj=04/26/2014_10/31/2014&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		// price sorting test
		// url =
		// "dobj=04/26/2014_10/31/2014&sortSrcType=5&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=PRCE&Ns=PRICE_*_CL&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		// fund sort test
		url = "dobj=04/26/2014_10/31/2014&sortSrcType=2&sortOrder=budget&Ns=budget&N=0&Nso=0&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");
		// TestListSortingFilter.testListPrepFlow("NATALIEB","IN","10537","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url);

		// url =
		// "dobj=10/18/2014_12/12/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=CIS_FORMAT_DESC&Ns=CIS_FORMAT_DESC&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		url = "Sf=Copyright Yearfilter_data_sepGreater Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=10/18/2014_12/12/2014&sortSrcType=2&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");
		// TestListSortingFilter.testListPrepFlow("CROSSIN","IN","11100","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url);

		url = "Sf=Copyright Yearfilter_data_sepGreater Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/27/2014_12/12/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=CIS_FORMAT_DESC&Ns=CIS_FORMAT_DESC&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");
		// TestListSortingFilter.testListPrepFlow("CROSSIN","AO","11100","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url);

		// TestListSortingFilter.testListPrepFlow("CROSSIN","AO","11100","",urlg.getParam("Ns"),
		// null,urlg.getParam("sortSrcType"),null, url);

		url = "Sf=Copyright Yearfilter_data_sepGreater Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/14/2013_09/19/2014&sortSrcType=2&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		// url =
		// "Sf=Copyright Yearfilter_data_sepGreater
		// Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/14/2013_09/19/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=ITEM_OHND&Ns=ITEM_OHND&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");
		// TestListSortingFilter.testListPrepFlow("ADU2604","SN","73638000","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url);

		// TestListSortingFilter.testListPrepFlow("CROSSIN","AO","11100","",AcdmSortType.translateCode("BNDG").toString(),
		// null,"1",urlg.getParam("dobj"));

		// TestListSortingFilter.testListPrepFlow("ADU2604","SN","73638000",
		// "",null, null,null,null,url);
		// TestListSortingFilter.testListPrepFlow("SSELLECK","SC","UCOLORADO",
		// "",null, null,null,null,url);
		// TestListSortingFilter.testListPrepFlow("CROSSIN","SC","11100",
		// "",null, null,null,null,url);

		// test PLP sorting
		// url =
		// "Sf=Binding%20Formatfilter_data_sepContainsfilter_data_seppaperback&dobj=09/27/2014_12/12/2014&sortSrcType=5&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=PRCE&Ns=PRICE_*_CL&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		url = "Sf=Binding%20Formatfilter_data_sepContainsfilter_data_seppaperback&dobj=09/27/2014_12/12/2014&sortSrcType=2&simpleSearchType=Title&Ntk=ISBN|Keyword&sortOrder=PRCE&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		// TestListSortingFilter.testListPrepFlow("CROSSIN","AO","11100","",urlg.getParam("Ns"),
		// urlg.getParam("Sf"),urlg.getParam("sortSrcType"),urlg.getParam("dobj"),
		// url);

		// AcademicListType academicListType =
		// AcademicListType.getListTypeById("IN");
		// System.out.println(SortByFieldListType.getSortByFieldList(academicListType));

		// AcademicListService service = (AcademicListService)
		// AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		// Map<Long, AcademicItemData> academicItemDataMap = null;
		// academicItemDataMap = service.getAcademicItemData(917312l,
		// 19583156l);

		// System.out.println("academicItemDataMap: " + academicItemDataMap);

		// System.out.println("sortKey: " +
		// AcdmSortType.translateCode("TTL_PUB_DT").toString());

		// AcademicListService service = (AcademicListService)
		// AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		// Set<String> eansList = service.findEanForListId(917312);
		// System.out.println("eanList: " + eansList);

		// TestListSortingFilter.testAvailableFormats("STONE5OC", "SR",
		// "81200");

		// UserAdminService userAdminService = (UserAdminService)
		// AcademicServiceLocator.getService(ServiceName.USER_ADMIN_SERVICE);
		// UserProfileInfo userInfo = userAdminService.getAplcUserInfo("ADMINI",
		// true);

		// CisUser cisUser = userAdminService.getCisUserInfo("ADMINI");
		// System.out.println("userInfo: " + userInfo + "\n" +
		// userInfo.getAplcUserRole());
		// System.out.println("cisUser: " + cisUser);

		// url =
		// "Sf=Selected
		// Byfilter_data_sepContainsfilter_data_sepCROSSINfilter_rec_sepCopyright
		// Yearfilter_data_sepGreater
		// Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=10/18/2014_12/12/2014&sortSrcType=2&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		// urlg = new UrlGen(url, "UTF-8");
		// /System.out.println("Sf: " + urlg.getParam("Sf"));
		// AcdmFilterHelper hlpr = ((AcdmFilterHelper)
		// AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
		// Map<String, List<String>> filterMap =
		// hlpr.filterQueries(urlg.getParam("Sf"));
		// System.out.println("solr: " + filterMap.get("solr"));
		// System.out.println("orderQry: " + filterMap.get("orderQry"));
		// System.out.println("listQry: " + filterMap.get("listQry"));
		// System.out.println("listQryMap: " + filterMap.get("listQryMap"));

		// UserAdminService userAdminService = (UserAdminService)
		// AcademicServiceLocator.getService(ServiceName.USER_ADMIN_SERVICE);
		// CisUser cisUser = userAdminService.getCisUserInfo("ADMINI");

		// System.out.println("cisUSer: " + cisUser);
		// if (SearchConstants.orderPattern.matcher("Edit Order Info").find()){
		// System.out.println("matched");
		// }

		// String ntt =
		// "Can Democracy Be Saved?: Participation, Deliberation and Social &
		// Movements";
		// ntt = ntt.replace(",", " ");
		// ntt = ntt.replaceAll("\\&", " AND ");
		// String[] exactTitle = SearchUtility.getTitleExactEndecaParams(ntt);
		// String[] exactTitle = SearchUtility.getTitleStartOfEndecaParams(ntt);
		// ntt = StringUtility.replaceAllSubstrings(exactTitle[0], "%CE%BE",
		// " ").trim();
		// ntt = ntt.trim();

		// remove the * at the end
		// if(ntt.endsWith("*")){
		// ntt = ntt.substring(0, ntt.lastIndexOf('*'));
		// }
		// ntt = ntt.toUpperCase();
		// System.out.println("ntt: " + ntt);

		// AcademicConstants.Roles.AUTH_ACQUISITIONS
		// AcademicConstants.Roles.AUTH_SELECTOR
		// AcademicConstants.Roles.AUTH_ACDM_BASIC
		// testActionsAvailableToUser("MARK7DE",
		// AcademicListType.SEARCH_RESULTS.getListTypeId(),
		// AcademicConstants.Roles.AUTH_ACQUISITIONS);

		// HttpCallUrl httpUrl = new
		// HttpCallUrl("CUSTOMER_DEMAND_NOT_STANDING_ORDER");

		// httpUrl.setString("sandbox", "");
		// httpUrl.setString("EAN","12345678");
		// url = httpUrl.getGanimedeUrl(false);

		// System.out.println("url: " + url);

		// AcademicList academicList = ((AcademicListService)
		// AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE)).findAcademicList("CROSSIN",
		// "AS");
		// System.out.println("academicList: " + academicList);

		// boolean isProdEnv =
		// "".equals(VariableData.getProperty("variabledata.environment.name"))
		// ? true : false;
		// String baseGnmdUrl = isProdEnv ? "BASE_URL_PROD" : "BASE_URL";
		// System.out.println("from variable data: " +
		// VariableData.getProperty("ganimedeBaseURL"));
		// System.out.println("from ganimede properties: " +
		// AcademicServiceLocator.getApplEnv().getProperty(baseGnmdUrl));

		// System.out.println("sortKey: " +
		// AcdmSortType.valueOf("TTL_PUB_DT").getFieldName() + ", " +
		// AcdmSortType.valueOf("TTL_PUB_DT").getFieldValue());

		// testEneQry();
		// testSearchOrder();
		// testGanimedeCall();
		// testProfileFromToSelectables("48105000");

		// Map<String, Boolean> ebookOption;
		// UserEbookDispOptions userEbookDispOptions = new
		// UserEbookDispOptions();
		// ebookOption = userEbookDispOptions.getUserEbookDispOptions("ACDM",
		// "CEV0108");

		// System.out.println("ebookOption: " + ebookOption);

		//new GanimedeMain().testCheckoutOrderInfo();

		// UserAdminService userAdminService = (UserAdminService)
		// AcademicServiceLocator.getService(ServiceName.USER_ADMIN_SERVICE);
		// UserAdminServiceTest userAdminServiceTest = (UserAdminServiceTest)
		// context.getBean("userAdminServiceTest");

		// System.out.println("user list: " +
		// userAdminServiceTest.getCisUserList());

		// CISUser user = userAdminService.getUserDetailsForUserQ("KATHYB");
		// System.out.println("user: " + user);
		// List<Map<String, String>> ganimedeResponse =
		// UserLoginGnmdeCalls.loginCallTest("KATHYB", user.getCustomerNo());
		// printJsonResponse(ganimedeResponse);
		// printJsonToFile("KATHYB", "login", ganimedeResponse);

		// UserLoginGnmdeCalls.loginCallTestValues("KATHYB");
		// UserLoginGnmdeCalls.loginCallTestSummary("KATHYB");
		// UserLoginGnmdeCalls.loginCallTestDependencies("KATHYB");
		// UserLoginGnmdeCalls.loginCallExchangeRateTest("KATHYB");
		// UserLoginGnmdeCalls.loginCallEBPDataTest("KATHYB");

		// UserLoginGnmdeCalls.loginCallTest("ALSWAN");

		// TestListSortingFilter.testAvailableFormats("RSU4NO",
		// "SR","51390000");
		// TestListSortingFilter.testAvailableFormats("GWIERSMA", "SR",
		// "UCOLORADO");

		// UserAdminService userAdminService = (UserAdminService)
		// AcademicServiceLocator.getService(ServiceName.USER_ADMIN_SERVICE);
		// CISUser user = userAdminService.getUserDetailsForUserQ("KATHYB");
		// System.out.println("user: " + user);

		// TestListSortingFilter.testListPrepFlow("CROSSIN", "SR", "11100", "",
		// "Title_Exact", "", "1", "", "",
		// "9780199330102 OR 9780415717748 OR 9781107669314 OR
		// 9780804790932&qt=ISBN_SI",
		// "");

		// System.out.println("eanOprMap: " + eanOprMap);

		// testISBNEbookEligibility();

		// System.out.println(SolrSearchFieldsService.getAcdmList());
		// System.out.println(SolrSearchFieldsService.getAcdmPriceFields());

		// selectionListTest();

		// String srtSeqCd = "I T P E"; // I - 0, T - 2, P - 4, E - 6
		// System.out.println("I position: " + srtSeqCd.indexOf("I"));
		// System.out.println("T position: " + srtSeqCd.indexOf("T"));
		// System.out.println("P position: " + srtSeqCd.indexOf("P"));
		// System.out.println("E position: " + srtSeqCd.indexOf("E"));

		// String STAT_CD = "CA CB CD CO CX KC KP CG CJ CL CQ CR CV CW KK KM";
		// String stat = "CG";

		// if (STAT_CD.contains(stat)) {
		// System.out.println("yes I am here");
		// }
		// testEb003("000553923");// 000474696, 000334305, 000553923, 000373845

		// Calendar cldr = new GregorianCalendar();
		// System.out.println((cldr.get(Calendar.MONTH) + 1) + "/" +
		// cldr.get(Calendar.DAY_OF_MONTH) + "/" + cldr.get(Calendar.YEAR));

		// testEb030();
		 //testEb031();
		// testEb032();
		// testEb034();
		// testEb035();

		 //testEb036();

		// NonAcdmSqlPropReader propReader = (NonAcdmSqlPropReader)
		// AcademicServiceLocator.getBean("nonAcdmSqlReader");
		// System.out.println("qry.pub.eb032.read.byean: " +
		// propReader.getProperty("qry.pub.eb032.read.byean"));

		// String dt = "03/04/1995";
		// StringBuilder bldr = new StringBuilder();
		// System.out.println("new date: " + bldr.append(dt.substring(0,
		// 2)).append("/01/").append(dt.substring(6, 10)).toString());

		// testSolrDataToJson();

		// String filterString =
		// "Copyright Yearfilter_data_sepGreater Thanfilter_data_sep1990";
		// if (null != filterString) {
		// AcdmFilterHelper filterHlpr = ((AcdmFilterHelper)
		// AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
		// Map<String, List<String>> filterMap =
		// filterHlpr.filterQueries(filterString);
		// System.out.println("filterMap: " + filterMap);
		// System.out.println("solrFilterQry: " + filterMap.get("solr"));
		// }

		// String date[] = SearchConstants.dateMMDDYYYYTagParser("10/01/2000");
		// System.out.println(date[0] + "-" + date[1] + "-" + date[2]);

		// testEb023();
		// testEb083();
		// testEb084();

		// testEb077();

		// testEb076();

		// testEb074Fetch();
		//testEb074Insert();
		// testEb073();

		// testEb059();

		// String str =
		// "SELECT ITEM_ID, CART_VEND_QTY, CTN_RTN_DISP_CD, EAN_ID,
		// IPS_PUB_DISC_CD, ITEM_NM, ITM_RMND_CTGY_CD, PROD_RTN_DISP_CD,
		// PUB_RTN_CD FROM IT.ITEM WHERE PUB_NUM_ID = :PUB_NUM_ID AND ( ISBN_ID
		// = :PRODUCT_CODE OR EAN_ID = :PRODUCT_CODE )";
		// str = str.replaceAll("\\:\\S*", "?");
		// System.out.println(str);

		// SalesDemandCommonDAO commonDao = new SalesDemandCommonDAO();
		// SelectionListDetailDAO detailDAO = new
		// SelectionListDetailDAO(DBUtility.getConnection("default", false));
		// SelectionList list = detailDAO.getListByID(listID);
		// try {
		// int currentCalendarMonth = commonDao.getCurrentCalendarMonth();

		// System.out.println("currentCalendarMonth: " + currentCalendarMonth);
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

		// getAcdmUserLoginGanimedeDetails();

		// StringBuilder strBuilder = new
		// StringBuilder(VariableData.getProperty("ganimedeBaseURL"));
		// strBuilder.append(AcademicServiceLocator.getApplEnv().getProperty("EAN_CHECK_STATUS"));

		// System.out.println("strBuilder: " + strBuilder.toString());
		// String data = "abcd '1'=1";
		// String data1 = escapeCharacters(data);
		// System.out.println("escape characters: " + data1);
		// System.out.println("remove escape characters: " +
		// removeEscapeCharacters(data1));

		// System.out.println("from searchconstants: " +
		// SearchConstants.escapeCharacters("abcd \"'1'=1"));
		// System.out.println("from searchconstants: " +
		// SearchConstants.removeEscapeCharacters(data1));

		// DBUtilityJDBCTmpltTest dbUtilJdbcTestDao = (DBUtilityJDBCTmpltTest)
		// context.getBean("dbUtilJdbcTestDao");
		// System.out.println("list detail: " +
		// dbUtilJdbcTestDao.getAllList("CROSSIN"));

		// testDateFormats();
		// String [] keys =
		// {"9781557095077","9781557094827","9781557095060","9781557095046","9781557095039","9781557094803"};
		// Set<String> searchKeySet = new HashSet<>(Arrays.asList(keys));
		// searchKeySet = (Set<String>)getItemIdFromSolr(searchKeySet);

		// System.out.println("searchKeySet = > " + searchKeySet);
		// Pattern pattern = Pattern.compile(" OR B.PROD_DSPL_ID = '\\w*'");
		// String str = "AND (B.EAN_ID = '9785553332501' OR B.ISBN_ID =
		// '9785553332501' OR B.PROD_DSPL_ID = '9785553332501')";
		// Matcher matcher = pattern.matcher(str);
		// if (matcher.find()) {
		// System.out.println("found match => " + matcher.start() + ":" +
		// matcher.replaceFirst(""));
		// }else{
		// System.out.println("found Not match");
		// }

		System.out.println("today's date: " + SearchConstants.getMMDDYYYYTodaysDate());

		//try {
		//	returnRun("shiv", "shiv1", "shiv12", "shiv123");
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		//try {
		//	returnRun1("shiv kumar jha");
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		//System.out.println("100: " + CyberSourceStatusConstants.STATUS_MESSAGE_MAP.get("100"));
		//System.out.println("100: " + CyberSourceStatusConstants.STATUS_MESSAGE_MAP);
		
		//int ctr = 0;
		//String css[] = {"","recentListsRowDark"};
		//for(;ctr <= 10; ctr++){
			//System.out.printf(" => %d | %d | %d | %d | %s | %d\n", ctr << 2, ctr >> 2, ctr>>> 2, ctr % 2, css[ctr % 2], ctr);
		//	System.out.println("random: " + css[((int)(Math.random() * 10)) % 2]);
		//}
		
		//Calendar calendar = Calendar.getInstance();
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//System.out.println("time: " + df.format(calendar.getTime()));
		
		//System.out.println("randomAlphaNumeric: " + randomAlphaNumeric(6));
		
		//int rand = 0;
		//System.out.println("rand: " + rand % 2);
		
		
		

		// for the boundary cases
		// start setting this possible time stamp by 1 sec and search for an
		// existing key in the set
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		System.out.println("date: " + df.format(new Date()));
		
		try {
			String thisKey = "";
			Calendar calendar1 = Calendar.getInstance();
			DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			calendar1.setTime(df1.parse("2015-11-03 23:08:12,156"));
			
			int ctr = 1;
			for (; ctr <= 60; ctr++) {
				calendar1.setTimeInMillis(calendar1.getTimeInMillis() - 1000);
				thisKey = df1.format(calendar1.getTime()) + "-" + "pool-6-thread-94";
				System.out.println(thisKey);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//nameParsing();	
		
		//threadPoolTest();
		
		testMessageParsing();
	}
	
	private static void testMessageParsing(){
		String msg = "01CI20G54641SQ69  DAACAuthCharge    20G5464     1043906_305937                                                    000021.21000001.22                                                                                                                                                                                                                                   20G5464     000005.004479134553365000001370                                                            ";
				
		CreditCardInfo ccinfo = new CreditCardInfo(ByteBuffer.wrap(msg.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		
		//CyberSourceCommonService cyberSourceCommonService = new CyberSourceCommonService();
		//JSONObject inputJSON = cyberSourceCommonService.getJsonStringFromCCinfo(ccinfo);
		
		System.out.println("ccinfo: " + ccinfo);
		System.out.println("subscription id:" + ccinfo.getRequestField(MQReqField.Request_Id) + ", " + ccinfo.getRequestField(MQReqField.Merch_Bill_To));
		//System.out.println("inputJSON: " + inputJSON);
	}
	
	
	@SuppressWarnings("unchecked")
	private static void threadPoolTest(){
		
		List<Future<Runnable>> reqFutures = new LinkedList<Future<Runnable>>();
		BlockingQueue<Runnable> workerQueue = new ArrayBlockingQueue<>(100);
		ThreadFactory queueThreadFactory  = new CustomThreadFactoryBuilder().setNamePrefix("MQ-Worker").setDaemon(true).build();
		
		ExecutorService qExecutor = null;		
		//ThreadPoolExecutor qExecutor = null; 
				
		qExecutor = new ThreadPoolExecutor(10, 20, 5000, TimeUnit.MILLISECONDS, workerQueue, queueThreadFactory);
		//qExecutor.prestartAllCoreThreads();
				
			for(int ctr = 1; ctr < 10; ctr++){
				reqFutures.add((Future<Runnable>) qExecutor.submit(new AddProcessor(ctr, ctr + 10)));
			}
		
	}
	
	
	private static void nameParsing(){
		
		String firstName = "";
		String lastName = "";
		String middleName = "";
		String cardHolderName = "Eduardo Arino de la Rubia"; //"Eduardo Arino de la Rubia, Shiv Kumar Jha, Shiv  Jha";
		try {
			cardHolderName = cardHolderName.trim();
			String[] nameParts = cardHolderName.split("\\ ");

			//firstName
			try{
				if (null != nameParts[0] && !nameParts[0].isEmpty()) {
					firstName = nameParts[0];
				}
			}catch(Exception e){
				firstName = "";
			}
			
			//lastName
			try {
				if (null != nameParts[nameParts.length - 1] && !nameParts[nameParts.length - 1].isEmpty()) {
					lastName = nameParts[nameParts.length - 1];
				}
			} catch (Exception e) {
				lastName = "";
			}
			
			//middle name
			try{
				int ctr = 1;
				for(;ctr < nameParts.length - 1;ctr++ ){
					middleName += nameParts[ctr] + ' '; 
				}
				middleName = middleName.trim();
			}catch(Exception e){
				middleName = "";
			}			
			
		} catch (Exception e) {

		}
		
		System.out.println();
		
		System.out.println("firstName: " + firstName);
		System.out.println("middleName: " + middleName);
		System.out.println("lastName: " + lastName);
	}
	
	public static void messageTest(){
		String data = "01CI20D76140KTFP  DAACAuthchargsIBC             F                                    ERROR";
		byte[] result = new byte[299];
		
		try{
			String hexData = asciiToHex(data);
			System.out.println("ASCII to HEX: " + hexData);
			System.out.println("ASCII to HEX: " + asHex(data.getBytes()));
			
			System.out.println("HEX TO ASCII: " + hexToASCII(hexData));
			
		}catch(Exception e){
			
		}
		try {
			ByteBuffer msgBody = ByteBuffer.wrap(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			String tmp = (new String(msgBody.array(), java.nio.charset.StandardCharsets.UTF_8)).trim();
			
			result = tmp.getBytes("Cp1047");
			System.out.println("EBCDIC - Cp1047 (Latin-1 character set for EBCDIC hosts): msgBody =>" + new String(result));
			
			result = tmp.getBytes("Cp500");
			System.out.println("EBCDIC - Cp500 (Latin-1 character set for EBCDIC hosts): msgBody =>" + new String(result));
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
				
		
		
		try {
			result = data.getBytes("CP037");
			System.out.println("EBCDIC - CP037 (USA, Canada (Bilingual, French), Netherlands, Portugal, Brazil, Australia): " + new String(result));
			
			result = data.getBytes("Cp1047");
			System.out.println("EBCDIC - Cp1047 (Latin-1 character set for EBCDIC hosts): " + new String(result));
			
			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    String ebcdic = "IBM-1047";
	    String ascii  = "ISO-8859-1";
		
		
		try {
			result = data.getBytes(ebcdic);
			System.out.println("EBCDIC: " + new String(result));
			
			System.out.printf("EBCDIC: %s\n",asHex(result));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String ZEROPAD = "0";
		int msgNo = 5;
		System.out.println("padded msgno: " + StringPadder.leftPad(Integer.toString(msgNo), ZEROPAD, 5));
		System.out.println("padded msgno: " + new String(StringPadder.leftPad(Integer.toString(msgNo), ZEROPAD, 5).getBytes()));
		
		String emailStr = "shiv.jha@ingramcontent.com";
		String email = ((StringUtils.split(emailStr, ";"))[0]).trim();
		
		System.out.println("email: " + email);
		
		

	}
	
	// To get the alphanumeric random String of desire length.
		// @count length of string required
	private static String randomAlphaNumeric(int count) {
			
			String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

			StringBuilder builder = new StringBuilder();

			while (count-- != 0) {

				int character = (int) (Math.random() * ALPHA_NUMERIC_STRING
						.length());

				builder.append(ALPHA_NUMERIC_STRING.charAt(character));

			}

			return builder.toString();

		}
	
	public static String asHex(byte[] buf) {
	    char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
	    char[] chars = new char[2 * buf.length];
	    for (int i = 0; i < buf.length; ++i)
	    {
	        chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
	        chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
	    }
	    return new String(chars);
	}
	
	private static String asciiToHex(String asciiValue)
	{
	    char[] chars = asciiValue.toCharArray();
	    StringBuffer hex = new StringBuffer();
	    for (int i = 0; i < chars.length; i++)
	    {
	        hex.append(Integer.toHexString((int) chars[i]));
	    }
	    return hex.toString();
	}
	
	private static String hexToASCII(String hexValue)
	   {
	      StringBuilder output = new StringBuilder("");
	      for (int i = 0; i < hexValue.length(); i += 2)
	      {
	         String str = hexValue.substring(i, i + 2);
	         output.append((char) Integer.parseInt(str, 16));
	      }
	      return output.toString();
	   }
	
	
	
	private static void returnRun1(String args) throws Exception {
		
		RunnableFuture<Integer> rf = new FutureTask<Integer>(new WordLengthCallable(args));
		//Runnable rf = new FutureTask<Integer>(new WordLengthCallable(args));
		
		new Thread(rf).start();
		//new Thread(RunnableFuture<Integer> rf -> new FutureTask<Integer>(new WordLengthCallable(args))).start();
		
		rf.get();
		
	    System.out.printf("returnRun1: The length is %s%n", rf.get());
	    System.exit(0);
	  }
	
	private static void returnRun(String ...args) throws Exception {
	    ExecutorService pool = Executors.newFixedThreadPool(3);
	    Set<Future<Integer>> set = new HashSet<Future<Integer>>();
	    for (String word: args) {
	      Callable<Integer> callable = new WordLengthCallable(word);
	      Future<Integer> future = pool.submit(callable);
	      set.add(future);
	    }
	    int sum = 0;
	    for (Future<Integer> future : set) {
	      sum += future.get();
	    }
	    System.out.printf("returnRun: The sum of lengths is %s%n", sum);
	    System.exit(sum);
	  }

	private static Collection<String> getItemIdFromSolr(Collection<String> searchKeySet) {

		try {

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator
					.getBean(AcademicServiceLocator.BeanName.SOLR_QRY));

			qry.set("fl", "ITEM_ID".intern());
			qry.set("qt", "ISBN_SI");
			qry.setRows(searchKeySet.size());

			if (searchKeySet.size() <= 1000) {

				qry.set("q", StringUtils.join(searchKeySet, " OR "));

				QueryResponse response = SolrQueryReuseService.getSearchEansSet(qry);
				SolrDocumentList result = response.getResults();

				if (result.getNumFound() <= 0) {
					return null;
				}

				searchKeySet.clear();
				searchKeySet = solrDocToSet(result, "ITEM_ID");
			} else {
				try {
					List<String> searchEansList = new LinkedList<String>(searchKeySet);
					Set<String> searchEansTmp = new LinkedHashSet<String>();

					searchKeySet.clear();
					int start = 0;
					int end = 1000;
					int limit = Math.abs(searchEansList.size() / end);
					limit = (searchEansList.size() > (limit * end)) ? limit + 1 : limit;

					for (; limit > 0;) {
						searchEansTmp.addAll(searchEansList.subList(start, end));

						qry.set("q", StringUtils.join(searchEansTmp, " OR "));

						QueryResponse response = SolrQueryReuseService.getRearrangedEansList(searchEansTmp, qry);
						SolrDocumentList result = response.getResults();

						if (result.getNumFound() > 0) {
							searchKeySet = solrDocToSet(result, "ITEM_ID");
						}

						limit--;
						start = end + 1;
						end += 1000;
						end = (end > searchEansList.size()) ? searchEansList.size() : end;
						searchEansTmp.clear();
					} // for

				} catch (Exception e) {
					// if fails - restart with reducing the size of eans sent ot
					// solr
					// e.printStackTrace();
				}

			} // else

			return searchKeySet;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private static Set<String> solrDocToSet(SolrDocumentList result, String searchKey) {

		Set<String> searchEansSet = new HashSet<String>();
		for (SolrDocument doc : result) {
			try {

				searchEansSet.add(doc.get(searchKey).toString());

			} catch (Exception e) {
				continue;
			}

		} // for

		return searchEansSet;
	}

	private static void testCreateCoopReportPdf() {
		// CreateCoopReportPdf process = new
		// CreateCoopReportPdf(args[0].toString());
	}

	private static void testEB002() {
		try {

			EB002Input inRecord = new EB002Input();
			inRecord.setEb002__action__cd("I");
			inRecord.setEb002__page__direction("N");
			inRecord.setEb002__pubn("S002");
			inRecord.setEb002__rtn__lmt(Short.valueOf("25").shortValue());
			inRecord.setEb002__bp__tp__only(" ");

			CompanyInformationService service = (CompanyInformationService) MigrationServiceLocator.getService(
					ibg.nonacademics.service.MigrationServiceLocator.ServiceName.COMPANY_INFORMATION_SERVICE);

			EB002Output eb002Output = service.fetchCompanyInformation(inRecord);
			System.out.println("eb002Output: " + eb002Output.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testDateFormats() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		try {
			// 2015-04-15 18:10:50.0
			DateFormat mmddyyyyDateFormat = new SimpleDateFormat("MM-dd-yyyy");

			System.out.println("MMDDYYYY: " + mmddyyyyDateFormat.parse("2015-04-15"));//

			System.out.println("db2yyyyMMdd - DateUtility: " + DateUtility.getDb2yyyyMMddFormat().parse("2015-04-15"));
			System.out.println("db2yyyyMMdd - my: " + defaultDateFormat.parse("2015-04-15 00:00:00.0"));

			System.out.println("date: " + DateUtility.convertDateToTuxedoFormat("2015/07/10"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// pub date
		try {

			System.out
					.println("Date to mm-dd-yyyy format: " + SearchConstants.getFormattedDate("2015-04-15 18:10:50.0"));
			System.out.println("Date to mm-dd-yyyy format: " + SearchConstants.getFormattedDate("2014-10-01"));

			// System.out.println("Date to mm-dd-yyyy format - 1: " +
			// dateFormat.format(defaultDateFormat.parse("2015-04-15
			// 18:10:50.0")));
			// System.out.println("Date to mm-dd-yyyy format - 2: " +
			// dateFormat.format(SearchConstants.db_df.parse("2015-04-15
			// 18:10:50.0")));

			// productDetail.setAcdmPubDate(dateFormat.format(defaultDateFormat.parse(pubDateMap.get(cisUserLoginData.getApprovalCenter()).toString())));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void regExGetdataWithinSquareBrackets() {
		// patterm tp bring data within []
		Pattern pattern = Pattern.compile("\\[(.*?)\\]", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Pattern pattern1 = Pattern.compile("\\['([^\\]]*)'\\]",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		String infoKey = "itemInfo['check|0']";

		Matcher matcher = pattern1.matcher(infoKey);
		if (matcher.find()) {
			String[] keyArr = matcher.group(1).split("[|]");
			System.out.println("keyArr: " + keyArr[0] + ", " + keyArr[1]);
		}
	}

	private static String escapeCharacters(String input) {

		// Pattern escapeCharPattern =
		// Pattern.compile("['=+\\-&|!(){}\\[\\]\\^~?:\\\\]",
		// Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		Pattern escapeCharPattern = Pattern.compile("[\\\"'=+]",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		StringBuffer searchTermBuilder = new StringBuffer();
		try {
			Matcher searchTermMatcher = escapeCharPattern.matcher(input);

			while (searchTermMatcher.find()) {
				// capture whole match and replace - $0
				searchTermMatcher.appendReplacement(searchTermBuilder, "\\\\$0");
			}
			searchTermMatcher.appendTail(searchTermBuilder);
		} catch (Exception e) {
			return "";
		}

		return searchTermBuilder.toString();
	}

	private static String removeEscapeCharacters(String input) {
		Pattern escapeCharPattern = Pattern.compile("[\\\\+]",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

		StringBuffer searchTermBuilder = new StringBuffer();
		try {
			Matcher searchTermMatcher = escapeCharPattern.matcher(input);

			while (searchTermMatcher.find()) {
				// capture whole match and replace - $0
				searchTermMatcher.appendReplacement(searchTermBuilder, "");
			}
			searchTermMatcher.appendTail(searchTermBuilder);
		} catch (Exception e) {
			return "";
		}
		return searchTermBuilder.toString();
	}

	private static void printJsonResponse(List<Map<String, String>> ganimedeResponse) {

		try {

			if (null != ganimedeResponse && !ganimedeResponse.isEmpty()) {

				ObjectMapperWraper mapper = (ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER);
				String jsonResp = mapper.write(ganimedeResponse);

				System.out.println("jsonResp: " + jsonResp);

			}

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void testEb076() {
		// retrieve remaindering categories
		EB076Input eb076inRecord = new EB076Input();

		eb076inRecord.setEb076__of__bp__id("000553923");// 000553923, S002
		eb076inRecord.setEb076__add__assc__id("zuckerp");
		eb076inRecord.setEb076__itm__rmnd__ctgy__cd__in("NONE");
		eb076inRecord.setEb076__itm__rmnd__ctgy__dn__in("NONE");
		eb076inRecord.setEb076__add__inq__cd("INQ");

		PubCategoryRemainderService categoryRemainderService = (PubCategoryRemainderService) MigrationServiceLocator
				.getService(ibg.nonacademics.service.MigrationServiceLocator.ServiceName.PUB_CATEGORY_REMAINDER);
		EB076Output eb076outRecord = categoryRemainderService.getEB076Output(eb076inRecord);

		System.out.println("eb076Output: " + eb076outRecord);
	}

	private static void testEb077() {

		short maxResults = 25;
		boolean isPaidPubl = true;

		String salesPeriod = "4";

		EB077InputWrapper inRecordWrapper = new EB077InputWrapper();
		EB077Input inRecord = new EB077Input();

		inRecord = new ibg.publisher.publisherinformation.eb077.EB077Input();

		EB077Output eb077outRecord = new EB077Output();

		// test data - tuxedo
		// ISBN_ID ITEM_ID EAN_ID
		// 1889833029 24,803 9781889833026
		// 0939218186 76,315 9780939218189
		// 1557094055 87,705 9781557094056
		// 1557091129 115,935 9781557091123
		// 1557091110 129,632 9781557091116
		// 1557091226 129,696 9781557091222
		// 1429020652 129,935 9781429020657
		// 0918222516 115,992 9780918222510
		// 155709103X 116,046 9781557091031
		// 0918222982 116,087 9780918222985

		// test data - db2
		// ISBN_ID ITEM_ID EAN_ID
		// 0939218186 2227477 9780939218189
		// 1557094055 672066 9781557094056
		// 1557091129 671948 9781557091123
		// 0918222516 620237 9780918222510
		// 155709103X 671942 9781557091031
		// 0918222982 620254 9780918222985
		// 1557091110 671947 9781557091116
		// 1557091226 671955 9781557091222
		// 1557092117 672004 9781557092113
		// 1557092109 672003 9781557092106

		// SELECT * FROM BP.IPS_RMND_CTGY WHERE PUB_NUM_ID = 'S002' ORDER BY
		// ADD_TS DESC

		// SELECT BP_ID,BP_TP_CD,BP_SUB_TP_CD FROM PB.BUSINESS_PARTNER WHERE
		// LGCY_SYS_XREF_NBR='S002'

		// SELECT * FROM BP.BUSINESS_PARTNER where LGCY_SYS_XREF_NBR='S002'

		// SELECT * FROM BP.BUSINESS_PARTNER where BP_ID='000553923' OR
		// LGCY_SYS_XREF_NBR='000553923'

		// select IPS_PUB_DISC_CD, CTN_RTN_DISP_CD, PROD_RTN_DISP_CD,
		// ITM_RMND_CTGY_CD, ITEM_ID from IT.ITEM WHERE PUB_NUM_ID = 'S002' AND
		// (ISBN_ID = '2245477' OR TTL_ID = 2245477 OR EAN_ID = '2245477')

		// select PUB_NUM_ID,ISBN_ID,ITEM_ID,EAN_ID from IT.ITEM WHERE TTL_ID =
		// 2245477 AND PUB_NUM_ID = 'S002'

		// SELECT A.TTL_ID, A.BOOK_IND, A.GIFT_IND, A.MUSC_IND, A.VI_IND FROM
		// EB.PROD A, TL.BSAC_FBP C, TL.FRMT_TP D, TL.MDIA_TP E WHERE
		// A.FBP_BSAC_CD = C.FBP_BSAC_CD AND
		// A.FRMT_TP_CD = D.FRMT_TP_CD AND A.MDIA_TP_CD = E.MDIA_TP_CD AND
		// (A.ISBN_ID = '9780939218189' OR A.EAN_ID = '9780939218189' OR
		// A.TTL_ID = 9780939218189)

		// SELECT A.TTL_ID, A.BOOK_IND, A.GIFT_IND, A.MUSC_IND, A.VI_IND FROM
		// EB.PROD A, TL.BSAC_FBP C, TL.FRMT_TP D, TL.MDIA_TP E WHERE
		// A.FBP_BSAC_CD = C.FBP_BSAC_CD AND
		// A.FRMT_TP_CD = D.FRMT_TP_CD AND A.MDIA_TP_CD = E.MDIA_TP_CD AND
		// (A.ISBN_ID = '9780939218189' OR A.EAN_ID = '9780939218189' OR
		// A.TTL_ID = 9780939218189)

		// SELECT ITEM_ID, CART_VEND_QTY, CTN_RTN_DISP_CD, EAN_ID,
		// IPS_PUB_DISC_CD, ITEM_NM, ITM_RMND_CTGY_CD, PROD_RTN_DISP_CD,
		// PUB_RTN_CD
		// FROM IT.ITEM WHERE PUB_NUM_ID = 'S002' AND ( TTL_ID = 76315 OR
		// ISBN_ID = '76315' OR EAN_ID = '76315' )

		// SELECT A.TTL_ID, A.BOOK_IND, A.GIFT_IND, A.MUSC_IND, A.VI_IND FROM
		// EB.PROD A, TL.BSAC_FBP C, TL.FRMT_TP D, TL.MDIA_TP E WHERE
		// A.FBP_BSAC_CD = C.FBP_BSAC_CD AND A.FRMT_TP_CD = D.FRMT_TP_CD AND
		// A.MDIA_TP_CD = E.MDIA_TP_CD AND A.TTL_ID = 672066

		// SELECT A.TTL_ID, A.BOOK_IND, A.GIFT_IND, A.MUSC_IND, A.VI_IND FROM
		// EB.PROD A WHERE (A.ISBN_ID = '9780939218189' OR A.EAN_ID =
		// '9780939218189')

		// SELECT ITM_RMND_CTGY_DN FROM BP.IPS_RMND_CTGY WHERE PUB_NUM_ID =
		// 'S002' AND ITM_RMND_CTGY_CD = 'NOSOP'

		// select ISBN_ID,TTL_ID,EAN_ID from IT.ITEM WHERE PUB_NUM_ID = 'S002'

		// SELECT ITEM_ID, CART_VEND_QTY, CTN_RTN_DISP_CD, EAN_ID,
		// IPS_PUB_DISC_CD, ITEM_NM, ITM_RMND_CTGY_CD, PROD_RTN_DISP_CD,
		// PUB_RTN_CD FROM IT.ITEM WHERE PUB_NUM_ID = 'S002' AND TTL_ID = 672066

		// SELECT ITEM_ID, CART_VEND_QTY, CTN_RTN_DISP_CD, EAN_ID,
		// IPS_PUB_DISC_CD, ITEM_NM, ITM_RMND_CTGY_CD, PROD_RTN_DISP_CD,
		// PUB_RTN_CD FROM IT.ITEM WHERE PUB_NUM_ID = '000553923'
		// AND ( ISBN_ID = '9781557094056' OR EAN_ID = '9781557094056' )

		inRecord.setEb077__of__bp__id("000553923");
		inRecord.setEb077__product__code__i("672066");// 1557094055 672066
														// 9781557094056
		inRecord.setEb077__pub__rtn__cd__i("N");
		inRecord.setEb077__ips__pub__disc__cd__i("NOR");
		inRecord.setEb077__ctn__rtn__disp__cd__i("D");
		inRecord.setEb077__prod__rtn__disp__cd__i("D");
		inRecord.setEb077__itm__rmnd__ctgy__cd__i("NEMOB");
		inRecord.setEb077__trans__type("INQ");

		inRecordWrapper.setEb077Input(inRecord);
		inRecordWrapper.setLGCY_SYS_XREF_NBR("S002");

		PublisherSummaryService pubSummaryService = (PublisherSummaryService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.PUB_SUMMARY);

		System.out.println("testEb077: " + pubSummaryService.getEb077Output(inRecordWrapper));

		// EB077Input eb077inRecord = new EB077Input();

		// eb077inRecord.setEb077__of__bp__id(bpid);
		// eb077inRecord.setEb077__product__code__i(productCode);
		// eb077inRecord.setEb077__pub__rtn__cd__i(returnInd);
		// eb077inRecord.setEb077__ips__pub__disc__cd__i(discCat);
		// eb077inRecord.setEb077__ctn__rtn__disp__cd__i(fullDispCode);
		// eb077inRecord.setEb077__prod__rtn__disp__cd__i(singleDispCode);
		// eb077inRecord.setEb077__itm__rmnd__ctgy__cd__i(returnCat);
		// eb077inRecord.setEb077__trans__type(transType);

		// System.out.println("EB023Output - migrated: " +
		// pubSummaryService.getProdTemp(inRecord));

		// try {
		// legstar call
		// using new to create the instance of the bean and store them so
		// they can be accessed by the called page

		// Instantiate the CICS bean for use
		// Ibeb023NProgramInvoker invoker = new
		// Ibeb023NProgramInvoker("mainframe-invoker-config.xml.PROD");
		// LegStarAddress address = new LegStarAddress("TheMainframe");

		// Call the execute action on the bean.
		// EB023Output eb023Output = invoker.ibeb023n(address, "IBEB023N",
		// inRecord);

		// System.out.println("EB023Output - CICS: " + eb023Output);
		// } catch (HostInvokerException e) {
		// e.printStackTrace();
		// } catch (HostTransformException e) {
		// e.printStackTrace();
		// }

	}

	private static void testEb084() {
		try {
			EB084Input eb084inRecord = new EB084Input();
			eb084inRecord.setEb084__of__bp__id("000445699");

			LegStarAddress address = new LegStarAddress("TheMainframe");
			Ibeb084NProgramInvoker invoker084 = new Ibeb084NProgramInvoker("mainframe-invoker-config.xml");
			EB084Output eb084outRecord = invoker084.ibeb084n(address, "IBEB084N", eb084inRecord);

			System.out.println("eb084outRecord: " + eb084outRecord);

		} catch (HostInvokerException e) {
			e.printStackTrace();
		} catch (HostTransformException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static void testEb083() {

		try {
			List<TitleSubfieldObj> listArray = new ArrayList<TitleSubfieldObj>();
			listArray = getListUniqueTitleIDs(8092044, "TTL_DRVD_NM", "B810");

			System.out.println("listArray: " + listArray);

			EB083Input EB083inRecord = new EB083Input();
			EB083inRecord.setEb083__of__bp__id("000445699");
			EB083inRecord.setEb083__user__id("aclapp");
			// EB083inRecord.setEb083__cat__cd("ONE");
			// EB083inRecord.setEb083__ips__cat__eff__dt("0001/01/01");
			EB083inRecord.setEb083__rec__cnt(listArray.size());
			Eb083__details record = null;
			List<Eb083__details> titleArray = new ArrayList<Eb083__details>(7000);

			for (TitleSubfieldObj title : listArray) {
				record = new Eb083__details();
				record.setEb083__item__id(title.item_id);
				titleArray.add(record);
				EB083inRecord.getEb083__details().add(record);
			}
			// EB083inRecord.setEb083__details(titleArray);
			LegStarAddress address = new LegStarAddress("TheMainframe");
			Ibeb083NProgramInvoker invoker083 = new Ibeb083NProgramInvoker("mainframe-invoker-config.xml.PROD");
			EB083Output EB083outRecord = invoker083.ibeb083n(address, "IBEB083N", EB083inRecord);

			System.out.println("EB083outRecord: " + EB083outRecord);

		} catch (HostInvokerException e) {
			e.printStackTrace();
		} catch (HostTransformException e) {
			e.printStackTrace();
		}
	}

	private static void testEb003(String bpId) {
		AccountProfileInfoService accountProfileInfoService = (AccountProfileInfoService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.ACCOUNT_PROFILE);
		System.out.println("account profile: " + accountProfileInfoService.getAccountProfileInfo(bpId));

		// try {
		// legstar call
		// using new to create the instance of the bean and store them so
		// they can be accessed by the called page
		// Ibeb003NProgramInvoker invoker = new
		// Ibeb003NProgramInvoker("mainframe-invoker-config.xml");
		// LegStarAddress address = new LegStarAddress("TheMainframe");

		// EB003Input inRecord = new EB003Input();
		// inRecord.setEb003__bp__id(bpId);
		// Call the execute action on the bean.

		// EB003Output outRecord = invoker.ibeb003n(address, "IBEB003N",
		// inRecord);

		// System.out.println("outRecord: " + outRecord);
		// } catch (HostInvokerException e) {
		// e.printStackTrace();
		// } catch (HostTransformException e) {
		// e.printStackTrace();
		// }

	}

	private static void testEb059() {
		EB059Input eb059inRecord = new EB059Input();

		eb059inRecord.setEb059__bp__id("000206251");
		eb059inRecord.setEb059__tran__tp__cd(" ");
		eb059inRecord.setEb059__tran__dt(" ");
		eb059inRecord.setEb059__ing__ref__nbr(" ");
		eb059inRecord.setEb059__due__dt(" ");
		eb059inRecord.setEb059__cust__ref__nbr(" ");
		eb059inRecord.setEb059__shipto(" ");
		eb059inRecord.setEb059__tran__amt(BigDecimal.ZERO);
		eb059inRecord.setEb059__cnfm__dt(" ");
		eb059inRecord.setEb059__dupl__ref__cnt__in(0);
		eb059inRecord.setEb059__asi__tran__code__in(" ");
		eb059inRecord.setEb059__comp__no__in(" ");
		eb059inRecord.setEb059__div__no__in(" ");
		eb059inRecord.setEb059__rtn__lmt(200);
		eb059inRecord.setEb059__action__cd("I");
		eb059inRecord.setEb059__page__direction(" ");
		eb059inRecord.setEb059__srt__cd("TD");
		eb059inRecord.setEb059__past__ind(null);

		ElectronicPaymentService electronicPaymentService = (ElectronicPaymentService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.ELECTRONIC_PAYMENT_SERVICE);
		EB059Output eb059Output = electronicPaymentService.getPayAmountDueTotal(eb059inRecord);

		System.out.println("eb059Output => " + eb059Output);
	}

	

	private static void testEb073() {

		EB073Input eb073inRecord = new EB073Input();
		eb073inRecord.setEb073__of__bp__id("000553923");
		EB073Output eb073outRecord = ((TradeClassificationCodeService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.TRADE_CODE)).fetchTradecode(eb073inRecord);

		System.out.println("EB073Output: " + eb073outRecord);

		EB082Input EB082inRecord = new EB082Input();
		EB082inRecord.setEb082__of__bp__id("000553923");
		EB082inRecord.setEb082__trans__type("ADD");
		EB082inRecord.setEb082__user__id("zuckerp");
		EB082inRecord.setEb082__special__offer__code("WU07");
		EB082inRecord.setEb082__special__offer__desc("xxx");
		EB082inRecord.setEb082__start__dt("07/16/2015");
		EB082inRecord.setEb082__end__dt("01/01/2016");
		EB082inRecord.setEb082__rstc__use__ind("N");
		EB082inRecord.setEb082__disc__entry__type__i("Y");
		EB082inRecord.setEb082__returnable__i(" ");

		EB082inRecord.setEb082__item__sel__list__nm__i("");
		EB082inRecord.setEb082__include__selection__list("N");
		EB082inRecord.setEb082__rec__cnt1((short) 1);

		Eb082__details1 record1 = new Eb082__details1();
		record1.setEb082__class__of__trade("RG");
		EB082inRecord.getEb082__details1().add(record1);

		// EB082inRecord.setEb082__details1(record1Array);
		EB082inRecord.setEb082__rec__cnt2((short) 0);
		// EB082inRecord.setEb082__details2(record2Array);
		EB082inRecord.setEb082__rec__cnt3((short) 0);
		// EB082inRecord.setEb082__details3(record3Array);

		// SpecialOfferSetupByClassOfTradeService
		// specialOfferSetupByClassOfTradeService =
		// (SpecialOfferSetupByClassOfTradeService) MigrationServiceLocator
		// .getService(MigrationServiceLocator.ServiceName.SPECIAL_OFFER_SETUP_BYCLASSOFTRADE_SERVICE);
		// EB082Output EB082outRecord = specialOfferSetupByClassOfTradeService
		// .speOffSetupByClassOfTrade(EB082inRecord);

		// System.out.println("EB082outRecord: " + EB082outRecord);

		// retrieve special code at class of trade level
		EB075Input EB075inRecord = new EB075Input();
		EB075inRecord.setEb075__of__bp__id("000553923");
		EB075inRecord.setEb075__cat__cd__in("WU16"); // WU07, WU016, WU08
		EB075inRecord.setEb075__search__type("ONE");

		EB075Output EB075outRecord = ((PublisherOfferDetailService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.OFFER_DETAIL)).fetchOfferDetail(EB075inRecord);

		System.out.println("EB075outRecord: " + EB075outRecord);

	}
	
	private static void testEb074Insert() {

		String eb074_add_inq_cd = "UPD";
		String eb074_ips_pub_disc_cd = "CROM";

		System.out.println("eb074_add_inq_cd: " + eb074_add_inq_cd);

		EB074Input EB074inRecord = new EB074Input();

		Eb074__details1 record1 = null;
		record1 = new Eb074__details1();
		
		Eb074__details2 record2 = null;
		record2 = new Eb074__details2();
		
		Eb074__details3 record3 = null;
		record3 = new Eb074__details3();
		
		record1.setEb074__class__of__trade("RT");
		EB074inRecord.getEb074__details1().add(record1);
						
		BigDecimal disc_pct1 = new BigDecimal(40);
		record2.setEb074__ma__xamt2(999999999); 
		record2.setEb074__disc__pct2(disc_pct1);
		EB074inRecord.getEb074__details2().add(record2);
		
		BigDecimal disc_pct2 = new BigDecimal(60);
		record3.setEb074__ma__xamt3(999999999); 
		record3.setEb074__disc__pct3(disc_pct2);
		EB074inRecord.getEb074__details3().add(record3);


		EB074inRecord.setEb074__of__bp__id("000553923");
		EB074inRecord.setEb074__add__inq__cd(eb074_add_inq_cd);
		EB074inRecord.setEb074__ips__pub__disc__cd(eb074_ips_pub_disc_cd);
		EB074inRecord.setEb074__rec__cnt1(1);
		EB074inRecord.setEb074__rec__cnt2((short) 1);
		EB074inRecord.setEb074__rec__cnt3((short) 1);
		EB074inRecord.setEb074__user__id("mfleene");
		EB074inRecord.setOwnerBPId("S002");
		
		for (Eb074__details1 eb074__details1: EB074inRecord.getEb074__details1()) {
			String classOfTrade = eb074__details1.getEb074__class__of__trade();			
			System.out.println("classOfTrade: " + classOfTrade);
		}

		System.out.println("EB074inRecord: " + EB074inRecord.toString());
		PublisherService publisherService = (PublisherService) MigrationServiceLocator.getService(MigrationServiceLocator.ServiceName.PUBLISHER_SERVICE);

		publisherService.addUpdateClassOfTrade(EB074inRecord);
	}

	private static void testEb074Fetch() {

		EB074Input EB074inRecord = new EB074Input();

		EB074inRecord.setEb074__of__bp__id("000553923");
		EB074inRecord.setEb074__add__inq__cd("INQ");
		EB074inRecord.setEb074__ips__pub__disc__cd("AWB");

		Eb074__details1 record1 = new Eb074__details1();
		record1.setEb074__class__of__trade("RT");
		EB074inRecord.getEb074__details1().add(record1);

		EB074inRecord.setEb074__rec__cnt1(1);
		EB074inRecord.setEb074__rec__cnt2(0);
		EB074inRecord.setEb074__rec__cnt3(0);
		EB074inRecord.setEb074__user__id("zuckerp");
		EB074inRecord.setOwnerBPId("S002");

		EB072Input eb072inRecord = new EB072Input();
		eb072inRecord.setEb072__of__bp__id("000553920");
		eb072inRecord.setLgcySysNum("S002");
		eb072inRecord.setEb072__ips__pub__disc__cd__in("AWB");

		EB074Output eb074outRecord = new EB074Output();

		PublisherService publisherService = (PublisherService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.PUBLISHER_SERVICE);

		eb074outRecord = publisherService.fetchNonReturnabel(EB074inRecord, eb074outRecord);
		System.out.println("eb074outRecord: " + eb074outRecord);

		eb074outRecord = publisherService.fetchReturnabel(EB074inRecord, eb074outRecord);
		System.out.println("eb074outRecord: " + eb074outRecord);

		// EB072Output eb072outRecord =
		// publisherService.fetchDiscountCatCode(eb072inRecord);

		// System.out.println("eb072outRecord: " + eb072outRecord);

	}

	private static void testEb030() {

		EB030Input input = new EB030Input();

		input.setEb030__pub__num__id("4826"); // 3577, 4826, 0096
		input.setEb030__nk__ord__dt("12/31/9999");
		input.setEb030__nk__po__nbr("");
		input.setEb030__nk__po__seq(0);
		input.setEb030__rtn__lmt(200);
		input.setEb030__page__direction("");

		try {
			// migrated java call
			PublisherPoOrderHdrService publisherPoOrderHdrService = (PublisherPoOrderHdrService) MigrationServiceLocator
					.getService(MigrationServiceLocator.ServiceName.PUB_PO_HDR);
			System.out.println("pub orders: " + publisherPoOrderHdrService.getEB030Data(input));

			// legstar call
			// using new to create the instance of the bean and store them so
			// they can be accessed by the called page
			// Ibeb030NProgramInvoker invoker = new
			// Ibeb030NProgramInvoker("mainframe-invoker-config.xml.prod");
			// LegStarAddress address = new LegStarAddress("TheMainframe");

			// Call the execute action on the bean.

			// EB030Output outRecord = invoker.ibeb030n(address, "IBEB003N",
			// input);

			// System.out.println("outRecord: " + outRecord);
			// } catch (HostInvokerException e) {
			// e.printStackTrace();
			// } catch (HostTransformException e) {
			// e.printStackTrace();
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testEb034() {

		// EB034S__ORD__DT=04/15/2015
		// &EB034S__NK__PROD__ID=
		// &EB034S__NK__EAN__ID=
		// &EB034S__NK__ITEM__NM=
		// &EB034S__NK__PO__AMT=0
		// &EB034S__NK__PO__DISC=0
		// &EB034S__PAGE__DIRECTION=
		// &EB034S__RTN__LMT=25
		// &EB034S__SORT__SEQ__CD=I

		try {

			EB034Input input = new EB034Input();

			input.setEb034s__pub__num__id("0711");
			input.setEb034s__ord__dt("04/14/2015");
			input.setEb034s__nk__prod__id("");
			input.setEb034s__nk__ean__id("");
			input.setEb034s__nk__item__nm("");
			input.setEb034s__nk__po__amt(new BigDecimal(0));
			input.setEb034s__nk__po__disc(new BigDecimal(0));
			input.setEb034s__page__direction("");
			input.setEb034s__rtn__lmt(25);
			input.setEb034s__sort__seq__cd("E");

			// srtSeqCd = "I T P E";
			// pgDir = "P N";

			try {
				// migrated java call
				PublisherPoOrderHdrService publisherPoOrderHdrService = (PublisherPoOrderHdrService) MigrationServiceLocator
						.getService(MigrationServiceLocator.ServiceName.PUB_PO_HDR);
				System.out.println("EB034Output: " + publisherPoOrderHdrService.getEb34Data(input));

				// legstar call
				// using new to create the instance of the bean and store them
				// so
				// they can be accessed by the called page
				Ibeb034NProgramInvoker invoker = new Ibeb034NProgramInvoker("mainframe-invoker-config.xml.prod");
				LegStarAddress address = new LegStarAddress("TheMainframe");

				// Call the execute action on the bean.

				EB034Output outRecord = invoker.ibeb034n(address, "IBEB034N", input);

				System.out.println("outRecord: " + outRecord);
			} catch (HostInvokerException e) {
				e.printStackTrace();
			} catch (HostTransformException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void testEb031() {

		EB031Input input = new EB031Input();

		// tmoriarty - 3577, carnold1 - H575, khurst1 - 0616
		// EZ225EXR

		input.setEb031__pub__num__id("2694"); // 3577 0615 0616 H575
		input.setEb031__po__nbr__in("CZ251B7R");// CZ225DBR, CQ028A0N EZ118ERR
												// EZ225EXR
		input.setEb031__po__seq__in(0);

		// migrated java call
		PublisherPoOrderHdrService publisherPoOrderHdrService = (PublisherPoOrderHdrService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.PUB_PO_HDR);
		// System.out.println("eb031: " +
		// publisherPoOrderHdrService.getEb31Data(input));

		EB031Output eb31Output = publisherPoOrderHdrService.getEb31Data(input);

		// ebbbb__tot__unit__cnt=125, ebbbb__tot__ext__amt=1398.58,
		// ebbbb__poa__tot__cnt=114, ebbbb__poa__tot__amt
		System.out.println("ebbbb__tot__unit__cnt: " + eb31Output.getEb031__tot__unit__cnt());
		System.out.println("ebbbb__tot__ext__amt: " + eb31Output.getEb031__tot__ext__amt());
		System.out.println("ebbbb__poa__tot__cnt: " + eb31Output.getEb031__poa__tot__cnt());
		System.out.println("ebbbb__poa__tot__amt: " + eb31Output.getEb031__poa__tot__amt().setScale(2, BigDecimal.ROUND_HALF_UP));

		System.out.println();
	}

	private static void testEb032() {

		// tmoriarty - 3577
		// carnold1 - H575
		// khurst1 - 0616
		EB032Input input = new EB032Input();

		try {
			input.setEb032__pub__num__id("H575");// 0616, 3577, H575
			input.setEb032__po__nbr__in("EZ225EXR");// (khurst1 - NZ097F0R,
													// EZ104DJR, EZ118ERR),
													// (tmoriarty - CZ225DBR)
			input.setEb032__po__seq__in(0);
			input.setEb032__nk__ean__id("");
			input.setEb032__nk__item__nm("");
			input.setEb032__nk__prod__id("");
			input.setEb032__nk__po__ln__seq(0);
			input.setEb032__page__direction("");
			input.setEb032__sort__seq__cd("T");

			// srtSeqCd = "I T P E"; // I - 0, T - 2, P - 4, E - 6
			// pgDir = "P N"; // P - 0, N - 2

			PublisherPoOrderHdrService publisherPoOrderHdrService = (PublisherPoOrderHdrService) MigrationServiceLocator
					.getService(MigrationServiceLocator.ServiceName.PUB_PO_HDR);
			System.out.println("eb032: " + publisherPoOrderHdrService.getEb32Data(input));

			// legstar call
			// using new to create the instance of the bean and store them so
			// they can be accessed by the called page
			Ibeb032NProgramInvoker invoker = new Ibeb032NProgramInvoker("mainframe-invoker-config.xml.prod");
			LegStarAddress address = new LegStarAddress("TheMainframe");

			// Call the execute action on the bean.

			EB032Output outRecord = invoker.ibeb032n(address, "IBEB003N", input);

			System.out.println("invoker outRecord: " + outRecord);
		} catch (HostInvokerException e) {
			e.printStackTrace();
		} catch (HostTransformException e) {
			e.printStackTrace();
		}

	}

	private static void testEb035() {

		EB035Input input = new EB035Input();

		input.setEb035s__prod__id("1620321629");
		input.setEb035s__ord__dt("04/13/2015");
		input.setEb035s__rtn__lmt(25);
		input.setEb035s__nk__po__nbr("CZ103BZR");
		input.setEb035s__nk__po__seq(0);
		input.setEb035s__nk__po__ln__seq(0);
		input.setEb035s__page__direction("N");
		input.setEb035s__pub__num__id("A549");

		// try {
		// migrated java call
		PublisherPoOrderHdrService publisherPoOrderHdrService = (PublisherPoOrderHdrService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.PUB_PO_HDR);
		System.out.println("eb035: " + publisherPoOrderHdrService.getEb35Data(input));

		// legstar call
		// using new to create the instance of the bean and store them so
		// they can be accessed by the called page
		// Ibeb030NProgramInvoker invoker = new
		// Ibeb030NProgramInvoker("mainframe-invoker-config.xml.prod");
		// LegStarAddress address = new LegStarAddress("TheMainframe");

		// Call the execute action on the bean.

		// EB030Output outRecord = invoker.ibeb030n(address, "IBEB003N", input);

		// System.out.println("outRecord: " + outRecord);
		// } catch (HostInvokerException e) {
		// e.printStackTrace();
		// } catch (HostTransformException e) {
		// e.printStackTrace();
		// }
	}

	private static void testEb036() {

		EB036Input input = new EB036Input();

		input.setEb036__bp__id("000743206");// 000190544 000038998
		input.setEb036__line__limit(new Integer(200).shortValue());
		input.setEb036__line__count(1);
		input.setEb036__allopitm__flag("N");
		input.setEb036__in__flag("C"); // "N", Y, C
		input.setEb036__cm__flag("");
		input.setEb036__cb__flag("");
		input.setEb036__ua__flag("");
		input.setEb036__li__flag("");
		input.setEb036__product__code(""); // 9785553332501
		input.setEb036__customer__nbr("20R7716");
		input.setEb036__ingram__ref__nbr("");
		input.setEb036__purchase__order("");
		input.setEb036__trans__date__start("0001-01-01");
		input.setEb036__trans__date__end("9999-12-31");
		input.setEb036__due__date__start("0001-01-01");
		input.setEb036__due__date__end("9999-12-31");
		input.setEb036__search__method("A");
		input.setOwnerBpId("000743205"); // 000035343 000038998
		input.setLgcySysXrefNbr("20R7716");// 2039267 2019713

		// try {
		// migrated java call
		AccountInfoService acctService = (AccountInfoService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.ACCOUNT_INFO_SERVICE);

		try {
			System.out.println("EB036: " + acctService.doAdvcSearch(input));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void testEb023() {

		short maxResults = 5;
		boolean isPaidPubl = true;

		String salesPeriod = "8";

		EB023InputWrapper inRecordWrapper = new EB023InputWrapper();
		EB023Input inRecord = new EB023Input();

		inRecord.setEb023__flag("N"); // "N" "P"
		inRecord.setEb023__pub__date("10/01/2000");// 1990-05-11 "01/0001"
													// 10/2009 10/2001
		inRecord.setEb023__pub__num("S002"); // 6745,S002
		inRecord.setEb023__rtn__lmt(maxResults);
		inRecord.setEb023__page__direction(" ");

		inRecord.setEb023____xweeks(Short.parseShort(salesPeriod));
		// inRecord.setEb023__view__comp__ind(isPaidPubl ? "Y" : "N");
		inRecord.setEb023__pub__dt__in("01/01/0001");// "01/01/0001" 2000-05-11
		// inRecord.setEb023__sell__thru__pct__in(new BigDecimal(0.00));

		inRecord.setEb023__srt__cd("DT");

		inRecord.getEb023__prod__id__t().add("9785550029343"); // 1577314484
		inRecord.getEb023__prod__id__t().add("9780596100520");

		// inRecord.getEb023__prod__id__t().add("2624");// 9781101144541
		// inRecord.getEb023__prod__id__t().add("2487");// 9780002250634
		// inRecord.getEb023__prod__id__t().add("2516");// 9780002251884
		// inRecord.getEb023__prod__id__t().add("2750");// 9780002551656
		// inRecord.getEb023__prod__id__t().add("3087");

		inRecord.setEb023__prod__cl__cd("R"); // R, Q
		// inRecord.setEb023__title__nm__in(" ");
		// inRecord.setEb023__prod__id__in(" ");
		// inRecord.setEb023__subj__cat__in(" ");
		// inRecord.setEb023__pub__alpha__in(" ");
		// inRecord.setEb023__dept__in(" ");
		// inRecord.setEb023____xweeks__sales__in(0);

		inRecord.setEb023__start__record(new Integer(0).shortValue());
		inRecordWrapper.setEb023Input(inRecord);

		PublisherSummaryService pubSummaryService = (PublisherSummaryService) MigrationServiceLocator
				.getService(MigrationServiceLocator.ServiceName.PUB_SUMMARY);

		// for (Map<String, Object> dataMap :
		// pubSummaryService.getProdTemp(inRecordWrapper)) {
		// for (String key : dataMap.keySet()) {
		// System.out.println("Key:Value => " + key + ":" + dataMap.get(key));
		// }
		// System.out.println("-----------------------");
		// }

		// System.out.println("pubSummaryService.getBuildTemp");
		// for (Map<String, Object> dataMap : pubSummaryService
		// .getBuildTemp(inRecordWrapper)) {
		// for (String key : dataMap.keySet()) {
		// System.out.println("Key:Value => " + key + ":"
		// + dataMap.get(key));
		// }
		// System.out.println("-----------------------");
		// }

		System.out.println("EB023Output - migrated: " + pubSummaryService.getEb023Output(inRecordWrapper));

		// try {
		// legstar call
		// using new to create the instance of the bean and store them so
		// they can be accessed by the called page

		// Instantiate the CICS bean for use
		// Ibeb023NProgramInvoker invoker = new
		// Ibeb023NProgramInvoker("mainframe-invoker-config.xml.PROD");
		// LegStarAddress address = new LegStarAddress("TheMainframe");

		// Call the execute action on the bean.
		// EB023Output eb023Output = invoker.ibeb023n(address, "IBEB023N",
		// inRecord);

		// System.out.println("EB023Output - CICS: " + eb023Output);
		// } catch (HostInvokerException e) {
		// e.printStackTrace();
		// } catch (HostTransformException e) {
		// e.printStackTrace();
		// }

	}

	private static List<TitleSubfieldObj> getListUniqueTitleIDs(int listID, String sortBy, String pubNumber) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// try {

		// SelectionListDetailDAO detailDAO = new
		// SelectionListDetailDAO(DBUtility.getConnection("default", false));
		// SelectionList list = detailDAO.getListByID(listID);

		// System.out.println("list: " + list);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		List<TitleSubfieldObj> ttlIDSubInfo = new ArrayList<TitleSubfieldObj>(125);
		ibg.publisher.titletrackingreport.TitleSubfieldObj tObj;
		try {
			connection = DBUtility.getConnection("default", false);
			if ("".equals(pubNumber)) {
				preparedStatement = connection.prepareStatement(
						"SELECT TTL_ID, PROD_ID, ITEM_ID, DECODE(TTL_ARTC_LDNG_NM, NULL, NULL, TTL_ARTC_LDNG_NM || ' ') || TTL_DRVD_NM AS TITLE, DECODE(DGTL_IND, 'Y', 'Digital', DECODE(BOOK_IND, 'Y', 'Book', DECODE(GIFT_IND, 'Y', 'Gift', DECODE(MUSC_IND, 'Y', 'Music', DECODE(VI_IND, 'Y', 'Video', 'Periodical'))))) producttype FROM PROD where TTL_ID IN( SELECT unique(TTL_ID) FROM SEL_LIST_ITEM WHERE SEL_LIST_ID = ? AND DEL_IND = 'N' ) ORDER BY ?, TTL_ID");
				preparedStatement.setInt(1, listID);
				preparedStatement.setString(2, sortBy);
			} else {
				preparedStatement = connection.prepareStatement(
						"SELECT TTL_ID, PROD_ID, ITEM_ID, DECODE(TTL_ARTC_LDNG_NM, NULL, NULL, TTL_ARTC_LDNG_NM || ' ') || TTL_DRVD_NM AS TITLE, DECODE(DGTL_IND, 'Y', 'Digital', DECODE(BOOK_IND, 'Y', 'Book', DECODE(GIFT_IND, 'Y', 'Gift', DECODE(MUSC_IND, 'Y', 'Music', DECODE(VI_IND, 'Y', 'Video', 'Periodical'))))) producttype FROM PROD where TTL_ID IN( SELECT unique(TTL_ID) FROM SEL_LIST_ITEM WHERE SEL_LIST_ID ="
								+ listID + " AND DEL_IND = 'N' ) AND PUB_NUM_ID= " + "'" + pubNumber + "'"
								+ " ORDER BY " + sortBy + ", TTL_ID");
			}

			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				tObj = new TitleSubfieldObj();
				tObj.ttl_id = resultSet.getInt(1);
				tObj.prod_id = resultSet.getString(2);
				tObj.item_id = resultSet.getInt(3);
				tObj.title = resultSet.getString(4);
				tObj.prodType = resultSet.getString(5);
				ttlIDSubInfo.add(tObj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(resultSet, preparedStatement, connection);
		}
		return ttlIDSubInfo;
	}

	private static void selectionListTest() {
		try {

			SelectionListDetailDAO detailDAO = new SelectionListDetailDAO(DBUtility.getConnection("default", false));
			SelectionList list = detailDAO.getListByID(3208325);

			System.out.println("list: " + list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			SelectionListDataService selectionListDataService = (SelectionListDataService) AcademicServiceLocator
					.getService(ServiceName.SELECTION_LIST_SERVICE);
			// System.out.println("list by hibernate: " +
			// selectionListDataService.getListDetails(92265));//3208325
			SelList selList = selectionListDataService.getListDetails(92265);// 3208325
			// System.out.println("list by hibernate: " + selList);
			for (SelListItem item : selList.getSelListItems()) {
				System.out.println("list by hibernate: " + item);
			}

			// List<SelListItem> items =
			// selectionListDataService.getListItemDetails(3208325);
			// System.out.println("items: " + items);
			// for(SelListItem item: items){
			// System.out.println("list by hibernate: " + item);
			// }

			ListDetailsService listDetailsService = new ListDetailsService();
			// ListDetails listDetails =
			// listDetailsService.getListDetails(listID, SortType.DNE_PRIORITY,
			// user.getPrimaryWarehouse(), user.getSecondaryWarehouse(),
			// user.getMarketSegment());
			ListDetails listDetails = listDetailsService.getListDetails(3208325, SortType.EAN, "NV", "DD",
					MarketSegment.IBC);
			SelectionList list = listDetails.getList();

			System.out.println("list: " + list);

			// we have all the title id's (TTL_ID) for this list at this point
			List<SelectionListItemID> itemIDs = listDetails.getItemsByType().get(ProductTypes.ALL);
			List<Object> titleIds = new ArrayList<Object>();

			for (SelectionListItemID itemId : itemIDs) {
				System.out.println("list item-id: " + itemId.getListItemID() + "," + itemId.getTitleID());
				titleIds.add(itemId.getTitleID());
			}
			String prmrySolrFld = "TTL_ID";

			String url = "Sf=Quantityfilter_data_sepGreater Thanfilter_data_sep1&dobj=01/01/2014_12/31/2015&sortSrcType=1"
					+ "&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=Title&Ns=Title&N=0&Nso=0&listTypeId=SL&Ntt=blues*"
					+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
			UrlGen urlg = new UrlGen(url, "UTF-8");
			String sortKey = urlg.getParam("Ns") + "&Nso=" + urlg.getParam("Nso");
			// TestSelectionListSortingFilter.testListPrepFlow(ttlIds, "ALEXAP",
			// urlg.getParam("listTypeId"), "57110",
			// "", sortKey, filterString, urlg.getParam("sortSrcType"),
			// urlg.getParam("dobj"), url, searchPhrase, "");
			TestSelectionListSortingFilter.testListPrepFlow(titleIds, "mfleene", urlg.getParam("listTypeId"), "", "",
					sortKey, urlg.getParam("sortSrcType"), url, prmrySolrFld);

			listDetailsService = new ListDetailsService();
			List<SelectionListItem> productResults = listDetailsService.getItemDetails(itemIDs, Boolean.FALSE,
					Boolean.FALSE, MarketSegment.IBC);

			System.out.println("TitleID : Ean : Isbn : Product.Isbn : Product.Ean");
			for (SelectionListItem item : productResults) {
				System.out.println(item.getTitleID() + ":" + item.getEan() + ":" + item.getIsbn() + ":"
						+ item.getProduct().getIsbn() + ":" + item.getProduct().getEan());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testNewUserCreateProcess() {
		// update CIS
		// update CIS
		// UserQDataWriter qwriter = (UserQDataWriter)
		// AcademicServiceLocator.getBean(BeanName.QUSERDATAWRITE);
		// qwriter.createUserInfoToCis(userID);

		UserAdminService userAdminService = (UserAdminService) AcademicServiceLocator
				.getService(ServiceName.USER_ADMIN_SERVICE);
		CisUser cisUser = userAdminService.getCisUserInfo("test001");

		AcdmListCreator listCreator = (AcdmListCreator) AcademicServiceLocator.getBean(BeanName.ACDMLISTCREATE);

		System.out.println("cisUser: " + cisUser);
		System.out.println("cust group: " + cisUser.getCisCustomer().getCustGroup());
		System.out.println("is admin:" + cisUser.getAdministrator());

		listCreator.createAcademicList("test001", cisUser);
	}

	private static String escapeMetacharacters(String input) {

		String[] inArr = input.split("\\,");

		StringBuffer searchTermBuilder = new StringBuffer();

		for (String in : inArr) {
			Matcher searchTermMatcher = SearchConstants.escapeCharPattern.matcher(in);
			while (searchTermMatcher.find()) {
				searchTermMatcher.appendReplacement(searchTermBuilder, "\\\\$0");
			}
			searchTermMatcher.appendTail(searchTermBuilder).append(",");
		}

		searchTermBuilder.replace(searchTermBuilder.lastIndexOf(","), searchTermBuilder.lastIndexOf(",") + 1, "");

		return searchTermBuilder.toString();

	}

	private static void testISBNEbookEligibility() {
		// String[] isbnArr = new String[] { "9780199330102", "9780415717748",
		// "9781107669314", "9780804790932",
		// "9780143123866", "9780415719896", "9780824837143", "9781905739714",
		// "9780415657204",
		// "9780816689392", "9781781380260", "9780857202789", "9780199967841",
		// "9781439898598", "9780415737869",
		// "9789462081208", "9780747812210", "9780804138789", "9780804138598",
		// "9781775587194", "9781107025738", "9789814590686", "9781439873793",
		// "9781118658048", "9781783190799",
		// "9780415738804", "9789042929371", "9780415733397", "9781556594243",
		// "9789814571807", "9781780762784", "9781107667372", "9781780329215",
		// "9780199986118", "9780983579168",
		// "9780415723893", "9781447306313", "9780544114357", "9781409455806",
		// "9781847772671", "9781607812692", "9780199674213", "9780071804219",
		// "9781921833281", "9780199988488",
		// "9781107661035", "9780957661226", "9780307596871", "9780231166867",
		// "9781842176658", "9780199344574", "9780754665243", "9783943365160",
		// "9781118465257", "9780415841238",
		// "9781421412399", "9781606061459", "9781282796263", "9781137389060",
		// "9781780325507", "9780717161805", "9780226119984", "9782923975221",
		// "9780415826020", "9781909400047",
		// "9781933523309", "9781606061466", "9780415835770", "9780992726409",
		// "9780199368136", "9780415711388", "9789814571425", "9781780232720",
		// "9780870210389", "9781781907023",
		// "9781409406297", "9781848933804", "9781137374097", "9781622680603",
		// "9781610694346", "9781280571336", "9780802151575", "9781780764689",
		// "9781608198245", "9780199677856",
		// "9780804139090", "9781107677937", "9781782062042", "9789042038240",
		// "9781907372483", "9780745334653", "9780385347570", "9780300196580",
		// "9781608199761", "9781137403117",
		// "9780226143217", "9780786479634", "9781848930421", "9780199855766",
		// "9780813935348", "9780199997329", "9781621532408", "9781472529404",
		// "9780230274839", "9781847772633",
		// "9781317702160", "9781770410831", "9781118017746", "9780271059730",
		// "9780983753728", "9781472522016", "9789814583084", "9780415534505",
		// "9781137385970", "9781107017856",
		// "9780393920857", "9789462081277", "9780520270701", "9781848221352",
		// "9781476751443", "9781107056107", "9781463202361", "9781472530516",
		// "9780393089158", "9780521144117",
		// "9780470658048", "9780774827102", "9781412851879", "9780226139005",
		// "9780415818704", "9780199334551", "9780198708711", "9780199987467",
		// "9780415537551", "9781409452096",
		// "9780199593774", "9781446282328", "9781446273593", "9781107603707",
		// "9781137390165", "9781107040519", "9780385348065", "9780415666060",
		// "9780571307982", "9781306528528",
		// "9781782741312", "9780300196870", "9781780329475", "9780199370184",
		// "9780199940035", "9789814368872", "9781898281689", "9780718893422",
		// "9781780761947", "9780199314058",
		// "9781782974192", "9780745683973", "9781780764030", "9780812995787",
		// "9781780765167", "9780992811105", "9789004270039", "9780230220843",
		// "9780813347158", "9780896727991",
		// "9781781252925", "9781783160501", "9781552666555", "9780802871312",
		// "9781609181635", "9781107043763", "9781780769059", "9781137294777",
		// "9781137324740", "9781849054423",
		// "9780521196048", "9780415842914", "9780714848532", "9782503549521",
		// "9781783190980", "9781447309987", "9781137405494", "9780571306824",
		// "9781466507562", "9780415736367",
		// "9781107633896", "9780330454223", "9781118646793", "9780500239162",
		// "9781443855310", "9781466561595", "9780062218292", "9780415750004",
		// "9781137379511", "9781904856535",
		// "9781447307174", "9780071817967", "9780806144535", "9780745660875",
		// "9781627740029", "9780199603695", "9780838912331", "9780224087889",
		// "9780857420831", "9781927335277",
		// "9781138001275", "9780992633608", "9781597142656", "9780809148417",
		// "9781107053168", "9780393960587", "9789004255807", "9781842657744",
		// "9781608198702", "9780814724811",
		// "9780812245080", "9780806144733", "9780847830831", "9781442643550",
		// "9780199652839", "9781306662314", "9780739189634", "9781409454717",
		// "9780300205305", "9781781380284",
		// "9781118324578", "9780199333905", "9781937856748", "9781409469896",
		// "9781135008642", "9780061896453", "9781137380845", "9780691160603",
		// "9781551119922", "9781451645859",
		// "9780786476503", "9780300179385", "9781409443933", "9781848933675",
		// "9781780232768", "9780878468249", "9781482219777", "9781626194366",
		// "9781613743973", "9780806144122",
		// "9781455509645", "9780300185973", "9780415707350", "9780323088541",
		// "9780571315093", "9780415704700", "9781423492214", "9781137414052",
		// "9780521874526", "9780804138536",
		// "9781909682016", "9780226132341", "9781107600959", "9781107663688",
		// "9780415857451", "9781137323262", "9780226035086", "9780415500401" };

		String[] isbnArr = new String[] { "9781283651066" };
		List<String> list = Arrays.asList(isbnArr);
		Set<String> searchEansSet = new HashSet<String>(list);
		System.out.println("searchEansSet: " + searchEansSet);
		searchEansSet = Br1101Helper.eanCisEbpEligibility(searchEansSet, "MIL", "A");
		System.out.println("searchEansSet: " + searchEansSet);
	}

	private static GanimedeResponse getGanimedeResponse(String[] values, String... params) {

		try {
			HttpCallUrl gnmdUrl = new HttpCallUrl(params[0]);
			gnmdUrl.setString("sandbox", values[0]);
			gnmdUrl.setString("cust-group", values[1]);

			String url = gnmdUrl.getGanimedeUrl(true);

			GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();

			if (params.length > 1 && null != params[1]) {
				url = url + params[1];
			}
			System.out.println("url: " + url);
			return ganimedeWorker.getGanimedeResponse(replaceSpaceWithPercentage20(url));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String replaceSpaceWithPercentage20(String url) {
		String SPACE = " ";
		String REPLACE_BY_PERCENTAGE_20 = "%20";
		url = url.replaceAll(SPACE, REPLACE_BY_PERCENTAGE_20);
		return url;
	}

	private void testCheckoutOrderInfo() {
		// AcademicOrderInfoDao orderInfoDao = ((AcademicOrderInfoDao)
		// AcademicServiceLocator.getService(ServiceName.ORDER_INFO_SERVICE));

		List<AcademicOrderInfo> academicOrderInfos = orderInfoDao.getAcademicOrderInfo(1324869, 19583785);
		System.out.println("academicOrderInfos: " + academicOrderInfos);
	}

	private static void testMisc() {
		System.out.println("came here: ");
		ProductCisDetails.EBPBuilder bldr = new ProductCisDetails.EBPBuilder();
		System.out.println(bldr.getCisEBPData() + "\n" + bldr.getCisEBPDataMap());

		AcademicList academicList = ((AcademicListService) AcademicServiceLocator
				.getService(ServiceName.ACADEMIC_SERVICE)).findAcademicListByCustGroup("KATHYB", "IN", "0001");

		System.out.println("academicList: " + academicList);
		UserEbookDispOptions userEbookDispOptions = new UserEbookDispOptions();
		Map<String, Boolean> ebookOption = userEbookDispOptions.getUserEbookDispOptions("ACDM", "SCOTTPOPE");

		System.out.println("ebookOption: " + ebookOption);

		CISUserLoginData cisUserLoginData = fetchLoginCallData("SCOTTPOPE");// CEV0108//
																			// GWIERSMA
																			// //KATHYB
		System.out.println("user first currency: " + cisUserLoginData.getFirstCurrencyValue());
		System.out.println("user currency preference: " + cisUserLoginData.getCurrencyPreferences());
		System.out.println("user mil download agreement: " + cisUserLoginData.getMilDownloadAgreement());
		System.out.println("user ebook preferences: " + cisUserLoginData.getPlatformPref());

		System.out.println("------------------------------------------------------------");
		System.out.println("EBSCOLevel:EBSCOCategory:-" + cisUserLoginData.getEbsoLevel() + ", "
				+ cisUserLoginData.getEbscoCategory());

		System.out.println("------------------------------------------------------------");

		for (String platform : cisUserLoginData.getEbpDataObjectMap().keySet()) {
			System.out.println(platform + " Licenses:");
			for (EBPDataObject obj : cisUserLoginData.getEbpDataObjectMap().get(platform)) {
				System.out.println(obj.getCisEbpLicenceDesc() + ": " + obj.getCisEbpLicence());
			}
			System.out.println("------------------------------------------------------------");
		}

		// MELJOHNSON
		// AcademicList academicList = ((AcademicListService)
		// AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE)).findAcademicListByCustGroup("MELJOHNSON",
		// "AR", "NOVA");

		// System.out.println("academicList: " + academicList);

		// System.out.println("data: " + fetchLoginCallData("CEV0108"));
		// String req =
		// "9782895495987 9780970598745 9780972717359 blue cow 9780983981954";
		// String req =
		// "9782895495987 9780970598745 9780972717359 9780983981954";
		String req = "9782895495987";
		Object[] resp = SearchHelper.isSearchTermISBN(req);

		if ((Boolean) resp[0]) {
			System.out.println("ISBN Search " + resp[0]);
		}
		System.out.println(resp[0] + "\n" + resp[1]);

		req = "9782895495987+9780970598745+9780972717359+blue+cow+9780983981954";
		String[] subjectTerms = req.split("\\+");
		for (String subTrm : subjectTerms) {
			System.out.println("THEM_SEARCH:'" + subTrm + "'");
		}
	}

	private static void testProfileFromToSelectables(Object libraryGrp) {

		ProfileFromToSelectables profileFromToSelectables = new ProfileFromToSelectables("48105000");
		Set<SelectableOptions> profileFromTo = profileFromToSelectables.getDropDownData(null);
		for (SelectableOptions option : profileFromTo) {
			System.out.println("option: " + option);
		}

		ProfileSelectables profileSelectables = new ProfileSelectables("48105000");
		Set<SelectableOptions> profile = profileSelectables.getDropDownData(null);
		for (SelectableOptions option : profile) {
			System.out.println("option: " + option);
		}
	}

	public static void testGanimedeCall() {

		// 71024000
		// 48105000
		// 'B,F,N,M'
		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox="
		// +
		// "&program=ipage/programs/otunnel&statement=FIND arlg WHERE
		// arlg.company='CO' AND arlg.cust-group='71024000' "
		// +
		// "NO-LOCK. FOR EACH arcust OF arlg NO-LOCK, EACH opappm WHERE
		// opappm.oidCustomer = arcust.oidCustomer NO-LOCK, "
		// +
		// "EACH opappmh WHERE opappmh.oidApprovalPlan = opappm.oidApprovalPlan
		// AND opappmh.profileVersion = 0 AND "
		// +
		// "LOOKUP(opappmh.selection,'B') > 0 NO-LOCK BY opappm.approvalPlan:
		// RUN outputRow. END.&rowText="
		// +
		// "STRING(opappmh.description)'\\t'STRING(opappm.approvalPlan)&columnText='opappmh.description\\topappm.approvalPlan'";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=?&program=ipage/browserow.p"
		// +
		// "&IDList=SR&user_id=CROSSIN&cust-group=11100&EANList=9780839826057,9780613235693,9780977096480,9781424022854,9781466914230,"
		// +
		// "9781575677644,9780802448224,9780802470348,9781418967475,9780900384868,9780757830273,9780977096473,9780757812231,9781418900939,"
		// +
		// "9781424043811&oidOrderList=,,,,,,,,,,,,,,&eBookDetailList=:,:,:,:,:,:,:,:,:,:,:,:,:,:,:";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&program=ipage/so.p"
		// + "&mode=fiscal&cust-group=UCOLORADO&customer-no=28600A00";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre"
		// +
		// "&program=ipage/so-search.p&customer-no=28600A03&pono=o7566943";

		// url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=?"
		// +
		// "&program=ipage/programs/otunnel&statement=FOR EACH icpc2 WHERE
		// icpc2.company='CO' NO-LOCK: RUN outputRow. END."
		// +
		// "&rowText=STRING(icpc2.class2)'\\t'STRING(icpc2.description)&columnText='icpc2.class2\\ticpc2.description'";

		// url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=&program=ipage/opod-search"
		// +
		// "&mode=AUTO&cust-group=71024000&iFoundSearch=false&start-date=20140502&end-date=20140531&patronOrders=false";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox="
		// +
		// "&program=ipage/browserow.p&IDList=SR&user_id=ALEXAP&cust-group=57110&EANList=9780494583753,9781447489351,9781446535769,"
		// +
		// "9781249227854,9780978391218,9780971548084,9780875863665,9781446543641&oidOrderList=,,,,,,,&eBookDetailList=:,:,:,:,:,:,:,:";

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&program=ipage/browserow.p&IDList=SR"
				+ "&user_id=CROSSIN&cust-group=11303&EANList=9781482216004&oidOrderList=&eBookDetailList=:";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		printGanimedeResponseData(ganimedeResponse);
	}

	private static void testSROrderInfo() {
		// "NATALIEB",urlg.getParam("listTypeId"),"10537",
		AcademicList academicList = null;
		AcademicListService service = (AcademicListService) AcademicServiceLocator
				.getService(ServiceName.ACADEMIC_SERVICE);
		academicList = service.getAcademicListByUser("CROSSIN", "SR", "11100");

		Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(academicList);
		System.out.println("academicItemDataMap: " + academicItemDataMap);

		// academicList = service.getAcademicList(1312508l);

		// List<AcademicListItem> academicItemList =
		// service.getAllBylistId(1312508l);
		// System.out.println("academicItemList: " + academicItemList);

	}

	private static void testSearchOrderDataPop() {

		// 1 - TTL, EAN, AUTH, PUB
		// 2 - Order Date, Fund, Purchase Order

		String filterString = null;
		String sortKey = "budget";
		String sortSrcType = "2";

		OrderDetailResultHandler handler = new OrderDetailResultHandler();
		String dynamicURL = "&iFoundSearch=false&start-date=20140501&end-date=20140507&patronOrders=false";

		// url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=&program=ipage/opod-search"
		// +
		// "&mode=AUTO&cust-group=71024000&iFoundSearch=false&start-date=20140502&end-date=20140531&patronOrders=false";

		Object[] orderDetailListAndEans = handler.getOrderDetailMapWithSearchEans(dynamicURL,
				new String[] { "", "57110" });

		@SuppressWarnings("unchecked")
		Map<String, List<OrderDetail>> orderDetailMap = (Map<String, List<OrderDetail>>) orderDetailListAndEans[0];
		for (String key : orderDetailMap.keySet()) {
			for (OrderDetail ord : orderDetailMap.get(key)) {

				System.out.println("EAN : pubCurrency : pubList : netprice : order status=>" + ord.getEan() + " : "
						+ ord.getPublisherCurrency() + " : " + ord.getPublisherListPrice() + " : " + ord.getNetPrice()
						+ " : " + ord.getOrderStatus());
			}

		}

	}

	private static void testSearchOrder() {

		// 1 - TTL, EAN, AUTH, PUB
		// 2 - Order Date, Fund, Purchase Order

		String filterString = null;
		String sortKey = "budget";
		String sortSrcType = "2";

		OrderDetailResultHandler handler = new OrderDetailResultHandler();
		String dynamicURL = "&iFoundSearch=false&start-date=20140501&end-date=20140507&patronOrders=false";

		// actionType=displayList&listTypeId=SO&sortOptions=ON&simpleSearchType=ISBN&sortSrcType=2&Nty=1&Ntx=mode+matchany&Ntk=ISBN&Ns=Order+Date&N=0&dsplSearchTerm=Order+Detail+Results&Ntt=acdm_list_disp&productType=Book

		// http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=&program=ipage/opod-search&mode=AUTO&cust-group=57110&iFoundSearch=false&start-date=20150501&end-date=20150531&patronOrders=false

		// url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=&program=ipage/opod-search"
		// +
		// "&mode=AUTO&cust-group=71024000&iFoundSearch=false&start-date=20140502&end-date=20140531&patronOrders=false";
		Object[] orderDetailListAndEans = handler.getOrderDetailMapWithSearchEans(dynamicURL,
				new String[] { "", "57110" });

		@SuppressWarnings("unchecked")
		Map<String, List<OrderDetail>> orderDetailMap = (Map<String, List<OrderDetail>>) orderDetailListAndEans[0];
		// System.out.println("orderDetailMap: " +
		// orderDetailMap.keySet().size() + ", " + orderDetailMap);

		@SuppressWarnings("unchecked")
		Set<String> searchEans = new LinkedHashSet<String>();

		List<OrderDetail> orderDetailList = new LinkedList<OrderDetail>();
		for (String key : orderDetailMap.keySet()) {
			orderDetailList.addAll(orderDetailMap.get(key));
		}

		// System.out.println("before sorting");
		// for(int ctr = 0; ctr < 20;ctr++){
		// System.out.println(orderDetailList.get(ctr).getEan() + " : " +
		// orderDetailList.get(ctr).getOrderDate());
		// }
		// list to map
		Map<String, OrderDetail> orderDetailFinalMap = new LinkedHashMap<String, OrderDetail>();

		orderDetailList = Br1101Helper.prepareSearchOrdersSearchData(filterString, sortKey, sortSrcType,
				orderDetailList, orderDetailMap);
		// System.out.println("orderDetailList: " + orderDetailList.size() +
		// ", " + orderDetailList);
		System.out.println("after sorting");
		for (int ctr = 0; ctr < orderDetailList.size(); ctr++) {
			System.out.println(orderDetailList.get(ctr).getEan() + " : " + orderDetailList.get(ctr).getCustBudgNo());
		}

		// create the rearranged search term
		for (OrderDetail orderDetail : orderDetailList) {
			searchEans.add(orderDetail.getEan());
			orderDetailFinalMap.put(orderDetail.getSoNo(), orderDetail);
		}
		// System.out.println("searchEans: " + searchEans.size() + ", " +
		// searchEans);
	}

	private static void testListDisplay() {
		// sort type 1 test
		String url = "dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=Title&sortOrder=PRCE&simpleSearchType=Title&Ntk=Title&N=0&Nso=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=Useful,+Usable,+Desirable:+Applying+User+Experience+Design+to+Your+Library     &searchBy=Exact_Title&productLimit=EXTD";

		url = "dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=Title&sortOrder=PRCE&simpleSearchType=Title&Ntk=Title&N=0&Nso=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=100+Things+Nebraska+Fans+Should+Know+&+Do+Before+They+Die"
				+ "&searchBy=Keyword_Title&productLimit=EXTD";

		UrlGen urlg = new UrlGen(url, "UTF-8");

		// sort type 5 test
		url = "dobj=04/26/2014_10/31/2014&sortSrcType=5&Ns=PRICE_*_CL&sortOrder=PRCE&simpleSearchType=Title&Ntk=Title&N=0&Nso=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blue+eye&searchBy=Keyword_Title&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		// fund sort test
		url = "dobj=01/01/2014_12/31/2014&sortSrcType=1&sortOrder=Title&Ns=Title&N=0&Nso=0&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		// simple list display with basic sorting
		// url =
		// "Sf=Copyright Yearfilter_data_sepGreater
		// Thanfilter_data_sep1990filter_rec_sepFundfilter_data_sepExcludesfilter_data_sep23B-BOOK&dobj=09/27/2014_12/12/2014&sortSrcType=2&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=budget&Ns=budget&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		url = "Sf=Binding Formatfilter_data_sepContainsfilter_data_sepcloth&dobj=01/01/2014_12/31/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=Title&Ns=Title&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		url = "Sf=Binding Formatfilter_data_sepContainsfilter_data_seppaperback&dobj=10/18/2014_10/24/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=Title&Ns=Title&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		// Action filter test
		// Sf=Quantity_1filter_data_sepEqualsfilter_data_sep1
		url = "Sf=Quantityfilter_data_sepGreater Thanfilter_data_sep1&dobj=01/01/2014_12/31/2015&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=Title&Ns=Title&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		AcdmFilterHelper hlpr = ((AcdmFilterHelper) AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
		Map<String, List<String>> filterMap = hlpr.filterQueries("Quantityfilter_data_sepGreater Thanfilter_data_sep1");
		System.out.println("filterMap: " + filterMap);

		// TestListSortingFilter.testListPrepFlow("NATALIEB", "IN", "10537", "",
		// urlg.getParam("Ns"),
		// urlg.getParam("Sf"), urlg.getParam("sortSrcType"),
		// urlg.getParam("dobj"), url, "", "");
		// TestListSortingFilter.testListPrepFlow("CROSSIN", "IN", "11100", "",
		// urlg.getParam("Ns"),
		// urlg.getParam("Sf"), urlg.getParam("sortSrcType"),
		// urlg.getParam("dobj"), url, "", "");
		TestListSortingFilter.testListPrepFlow("ADMINI", "SC", "NOVA", "", urlg.getParam("Ns"), urlg.getParam("Sf"),
				urlg.getParam("sortSrcType"), urlg.getParam("dobj"), url, "", "");

	}

	private static void testSRSortFilter() {
		// sort type 1 test
		String url = "dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=Title&sortOrder=PRCE&simpleSearchType=Title&Ntk=Title&N=0&Nso=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=Useful,+Usable,+Desirable:+Applying+User+Experience+Design+to+Your+Library     &searchBy=Exact_Title&productLimit=EXTD";

		url = "dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=Title&sortOrder=PRCE&simpleSearchType=Title&Ntk=Title&N=0&Nso=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=100+Things+Nebraska+Fans+Should+Know+&+Do+Before+They+Die"
				+ "&searchBy=Keyword_Title&productLimit=EXTD";

		// for+love+of+country:+what+our+veterans
		// under+the+sun
		// Can+Democracy+Be+Saved?:+Participation,+Deliberation+and+Social+Movements
		// 9781101874455
		// 100+Things+Nebraska+Fans+Should+Know+&+Do+Before+They+Die

		UrlGen urlg = new UrlGen(url, "UTF-8");

		//

		// sort type 5 test
		url = "dobj=04/26/2014_10/31/2014&sortSrcType=5&Ns=PRICE_*_CL&sortOrder=PRCE&simpleSearchType=Title&Ntk=Title&N=0&Nso=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blue+eye&searchBy=Keyword_Title&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		// sort type 2 test
		// url =
		// "dobj=04/26/2014_10/31/2014&sortSrcType=2&Ns=budget&sortOrder=budget&simpleSearchType=Title&Ntk=Title_Exact&N=0&Nso=0&listTypeId=SR"
		// +
		// "&Ntt=blues&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues&searchBy=Title&dsplSearchTerm=blues&productLimit=EXTD";
		// urlg = new UrlGen(url, "UTF-8");

		// sort type 1 test
		// COUTTS_DIVISION_PUB_DATE_SEARCH_*
		// BISAC_CAT
		// url =
		// "Sf=&dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=BISAC_CAT&Nso=1&sortOrder=CG&simpleSearchType=Title&Ntk=Title&N=0&listTypeId=SR"
		// +
		// "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=lights&searchBy=Keyword_Title&productLimit=EXTD";
		// urlg = new UrlGen(url, "UTF-8");

		// filter test
		//
		url = "Sf=Titlefilter_data_sepContainsfilter_data_sepmarketing&dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=Title&Nso=1&sortOrder=TTL&simpleSearchType=Title&Ntk=Title_Exact&N=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=killing+farms&searchBy=Keyword_Title&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		// sort type 1 test
		// COUTTS_DIVISION_PUB_DATE_SEARCH_*
		// BISAC_CAT
		url = "sortSrcType=" + SortByFields.BISAC_CAT.getType() + "&Ns=" + SortByFields.BISAC_CAT.getDisplayName()
				+ "&sortOrder=" + SortByFields.BISAC_CAT.getFieldValue()
				+ "&simpleSearchType=Title&Ntk=Title&N=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=lights&searchBy=Keyword_Title&productLimit=EXTD";

		url = "Sf=Place of Publicationfilter_data_sepContainsfilter_data_sepWest Virginia&dobj=04/26/2014_10/31/2014&sortSrcType=1&Ns=Title&Nso=1&sortOrder=TTL&simpleSearchType=Title&Ntk=Title_Exact&N=0&listTypeId=SR"
				+ "&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blue+cow&searchBy=Keyword_Title&productLimit=EXTD";
		urlg = new UrlGen(url, "UTF-8");

		System.out.println("url: " + url);
		urlg = new UrlGen(url, "UTF-8");

		String searchBy = urlg.getParam("searchBy");
		String searchType = urlg.getParam("searchType");
		String searchTerm = urlg.getParam("searchTerm");
		String filterString = "";

		String searchPhrase = "";

		try {

			filterString = urlg.getParam("Sf").replaceAll("%25CE%25BE", "%CE%BE");
			filterString = URLDecoder.decode(filterString, "UTF-8").trim();

		} catch (Exception e) {
			e.printStackTrace();
			filterString = "";
		}

		if ("boolean".equalsIgnoreCase(searchType) || "power".equalsIgnoreCase(searchType)) {
			searchPhrase = SearchHelper.convertSearchTerm(urlg);
		} else {
			searchPhrase = SearchHelper.recorrectedSearchTerm(searchTerm, searchBy);
		}

		System.out.println("searchPhrase: " + searchPhrase);
		// TestListSortingFilter.testListPrepFlow("JFU2707",
		// urlg.getParam("listTypeId"), "48105000", "",
		// urlg.getParam("Ns"), urlg.getParam("Sf"),
		// urlg.getParam("sortSrcType"), urlg.getParam("dobj"),
		// url, searchPhrase, "10537A00");

		String sortKey = urlg.getParam("Ns") + "&Nso=" + urlg.getParam("Nso");
		TestListSortingFilter.testListPrepFlow("ALEXAP", urlg.getParam("listTypeId"), "57110", "", sortKey,
				filterString, urlg.getParam("sortSrcType"), urlg.getParam("dobj"), url, searchPhrase, "");

		// JFU2707, 48105000

	}

	public static String disallowHighResourceKeywords(String searchParamString) {
		if (searchParamString == null) {
			return "";
		}

		StringBuilder result = new StringBuilder();
		String[] searchTermsArr = searchParamString.split("\\s+");
		for (int i = 0; i < searchTermsArr.length; i++) {
			if (i > 0) {
				result.append(" ");
			}
			String token = searchTermsArr[i];
			if (token.startsWith("\"") && token.length() > 1) {
				token = token.substring(1);
			}
			if (token.endsWith("\"") && token.length() > 2) {
				token = token.substring(0, token.length() - 1);
			}
			Matcher nonSearchableMatcher = SearchConstants.nonSearchablePattern.matcher(token);
			if (nonSearchableMatcher.find()) {
				result.append(searchTermsArr[i].replaceAll("\\*", ""));
			} else {
				result.append(searchTermsArr[i]);
			}
		}
		return result.toString();
	}

	private static final String escapeMetaCharacters(String searchTermCopy) {
		Matcher searchTermMatcher = SearchConstants.escapeCharPattern.matcher(searchTermCopy);
		StringBuffer searchTermBuilder = new StringBuffer();

		while (searchTermMatcher.find()) {
			searchTermMatcher.appendReplacement(searchTermBuilder, "\\\\$0");
		}
		searchTermMatcher.appendTail(searchTermBuilder);
		return searchTermBuilder.toString();
	}

	private static String organizeAuthorTerms(String name) {
		StringBuffer returnBuffer = new StringBuffer();

		// remove trailing "."
		while (name.charAt(name.length() - 1) == '.')
			name = name.substring(0, name.length() - 1);
		// Identify ".", "," and replace them with space to make wild card
		// words.
		StringTokenizer tokenizer = new StringTokenizer(name, ". ,");
		String token = null;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.endsWith("*"))
				returnBuffer.append(token + "* ");
			else
				returnBuffer.append(token + " ");
		}
		return returnBuffer.toString();
	}

	private static void productPriceTest() {

		// String [] eans = new String
		// []{"9781878483805","9781595581037","9780814799482","9781425978792","9780472035526","9781936096701","9781595587183","9781558857469","9780415694582","9780521681438","9780814738269","9780199587575","9780674996816","9780813561066","9780231131858","9781107052925","9780199973729","9781905378326","9781612506890"};
		String[] eans = new String[] { "9781306815659" };
		Set<String> searchEansSet = new LinkedHashSet<String>();
		for (String ean : eans) {
			searchEansSet.add(ean);
		}
		CISUserLoginData cisUserLoginData = fetchLoginCallData("AMERADMIN");
		System.out.println("user first currency: " + cisUserLoginData.getFirstCurrencyValue());
		Map<String, AcdmTitlePriceObj> eanPriceMap = ProductPriceUtility.priceEanMap(searchEansSet, cisUserLoginData);

		System.out.println("eanPriceMap: " + eanPriceMap);
	}

	private static void productCisEbpGroupTest() {

		String searchTerm = "blues&qt=Title_SI";
		// searchTerm = "9780080504216&qt=ISBN_SI";
		searchTerm = "9781306007467 OR 9781322182254 OR 9781306006491  OR 9781322182278  OR 9781322063010  OR 9781600781308  OR 9781306515658 OR 9781299922914  OR 9780980236200  OR 9780935637328  OR 9780143191032 OR 9781306057097  OR 9781322078045  OR 9781306002493  OR 9781299740136&qt=ISBN_SI";

		CISUserLoginData cisUserLoginData = fetchLoginCallData("BOB2007");// CEV0108//
																			// GWIERSMA

		UserEbookDispOptions userEbookDispOptions = new UserEbookDispOptions();

		Map<String, Boolean> ebookOption = userEbookDispOptions.getUserEbookDispOptions("ACDM", "BOB2007");
		System.out.println("user ebook selections: " + ebookOption);

		// if (null != ebookOption && !ebookOption.isEmpty()) {
		// for (String key : ebookOption.keySet()) {
		// if (!cisUserLoginData.getEbpDataObjectMap().containsKey(key)) {
		// try {
		// cisUserLoginData.getEbpDataObjectMap().remove(key);
		// } catch (Exception e) {
		// // e.printStackTrace();
		// }
		// }
		// }
		// }

		System.out.println("user first currency: " + cisUserLoginData.getFirstCurrencyValue());
		System.out.println("user currency preference: " + cisUserLoginData.getCurrencyPreferences());
		System.out.println("legacy pricing: " + cisUserLoginData.getLegacyPricing());
		System.out.println("user currency exchange map: " + cisUserLoginData.getCurrencyExchangeMap());
		System.out.println("user mil download agreement: " + cisUserLoginData.getMilDownloadAgreement());
		System.out.println("user ebook preferences: " + cisUserLoginData.getPlatformPref());

		System.out.println("------------------------------------------------------------");
		System.out.println("EBSCOLevel:EBSCOCategory:-" + cisUserLoginData.getEbsoLevel() + ", "
				+ cisUserLoginData.getEbscoCategory());

		System.out.println("------------------------------------------------------------");

		for (String platform : cisUserLoginData.getEbpDataObjectMap().keySet()) {
			System.out.println(platform + " Licenses:");
			for (EBPDataObject obj : cisUserLoginData.getEbpDataObjectMap().get(platform)) {
				System.out.println(obj.getCisEbpLicenceDesc() + ": " + obj.getCisEbpLicence());
			}
			System.out.println("------------------------------------------------------------");
		}

		CisEbpGroupTest cisEbpGroupTest = new CisEbpGroupTest();
		// cisEbpGroupTest.priceSortedEanSRFromSolr(cisUserLoginData,
		// searchTerm, 20);
		cisEbpGroupTest.getCisEbpFromSolr(cisUserLoginData, searchTerm, 20);
	}

	private static void productEbookTest() {
		// String[] eans = new String[] { "9781306815659", "9780020306634",
		// "9781282319080", "9780080504216",
		// "9781137277619", "9780935161984" };
		String[] eans = new String[] { "9780080504216" };
		Set<String> searchEansSet = new LinkedHashSet<String>();
		for (String ean : eans) {
			searchEansSet.add(ean);
		}
		CISUserLoginData cisUserLoginData = fetchLoginCallData("BOB2007");// CEV0108//
																			// GWIERSMA
		System.out.println("cisUserLoginData: " + cisUserLoginData);
		System.out.println("user first currency: " + cisUserLoginData.getFirstCurrencyValue());
		System.out.println("user currency preference: " + cisUserLoginData.getCurrencyPreferences());
		System.out.println("user currency preference Str: " + cisUserLoginData.getUserCurrencyPreferenceStr());
		System.out.println("user mil download agreement: " + cisUserLoginData.getMilDownloadAgreement());
		System.out.println("user ebook preferences: " + cisUserLoginData.getPlatformPref());

		System.out.println("------------------------------------------------------------");
		System.out.println("EBSCOLevel:EBSCOCategory:-" + cisUserLoginData.getEbsoLevel() + ", "
				+ cisUserLoginData.getEbscoCategory());

		System.out.println("------------------------------------------------------------");

		for (String platform : cisUserLoginData.getEbpDataObjectMap().keySet()) {
			System.out.println(platform + " Licenses:");
			for (EBPDataObject obj : cisUserLoginData.getEbpDataObjectMap().get(platform)) {
				System.out.println(obj.getCisEbpLicenceDesc() + ": " + obj.getCisEbpLicence());
			}
			System.out.println("------------------------------------------------------------");
		}

		// Map<String, String> eanPriceMap =
		// ProductPriceUtilityTest.priceEanMapTest(searchEansSet,
		// cisUserLoginData);
		// Map<String, String> eanPriceMap =
		// ProductPriceUtilityTest.ebookPriceEanMapTest(searchEansSet,
		// cisUserLoginData);

		Map<String, AcdmTitlePriceObj> eanPriceMap = ProductPriceUtility.priceEanMap(searchEansSet, cisUserLoginData);

		for (String key : eanPriceMap.keySet()) {
			System.out.println("eanPriceMap: " + key + " : " + eanPriceMap.get(key));
		}

	}

	private static CISUserLoginData fetchLoginCallData(String userId) {

		CISUserLoginData cisUserLoginData = new CISUserLoginData();

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=&program=ipage/login.p&user_id="
		// + userId;
		String url = "http://gnm.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&program=ipage/login.p&user_id="
				+ userId;

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			cisUserLoginData.setApprovalCenter(object.get("approvalCenter"));
			cisUserLoginData.setAlibrisSupplier(object.get("alibrisSupplier"));
			cisUserLoginData.setAlibrisPartnerID(object.get("alibrisPartnerID"));
			cisUserLoginData.setMarcOutputMode(object.get("marcOutputMode"));
			cisUserLoginData.setBdsAgreement(object.get("bdsAgreement"));

			cisUserLoginData.setConsortiaLibraries(object.get("consortiaLibraries"));

			cisUserLoginData.setCatPartner(object.get("catPartner"));
			cisUserLoginData.setCatPartnerDescription(object.get("catPartnerDescription"));
			cisUserLoginData.setLicence(object.get("licence"));
			cisUserLoginData.setLicenceDescription(object.get("licenceDescription"));

			cisUserLoginData.setEbscoCategory(object.get("ebscoCategory"));
			cisUserLoginData.setEbsoLevel(object.get("ebsoLevel"));
			cisUserLoginData.setMilDownloadAgreement(object.get("milDownloadAgreement"));
			cisUserLoginData.setCurrencyPreferences(object.get("currencyPref"));
			cisUserLoginData.setApprovalPlans(object.get("approvalPlans"));

			cisUserLoginData.setLegacyPricing(object.get("legacyPricing"));
		}

		fetchLoginCallExchangeRateTest("00001A00", cisUserLoginData);

		return cisUserLoginData;

	}

	public static void fetchLoginCallExchangeRateTest(String custNo, CISUserLoginData cisUserLoginData) {

		// currency exchange map
		String url = "http://gnm.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&program=ipage/getexchrates.p&customer-no="
				+ custNo;
		// url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=&program=ipage/getexchrates.p&customer-no="
		// + custNo; //11100A00

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		Map<String, Double> curExchMap = new HashMap<String, Double>();

		for (Map<String, String> object : ganimadeResponseList) {
			try {
				curExchMap.put(object.get("buying-currency"), Double.parseDouble(object.get("exch-rate")));
			} catch (Exception e) {
				curExchMap.put(object.get("buying-currency"), 0d);
			}
		}

		cisUserLoginData.setCurrencyExchangeMap(curExchMap);
	}

	private static void patternSearches() {
		Pattern spacePattern = Pattern.compile("(?<=\\s|^)\\d+(?=\\s|$)");
		// Pattern numberPattern = Pattern.compile("^\\d+$");
		Pattern numberSpacePattern = Pattern.compile("^[0-9 ]+$");
		String terms = "9781241791797 9781241791636 9781241791490";
		// String terms =
		// "9781241791797 9781241791636 9781241791490 9781241791445
		// 9781483640921 9781568810638 9781404358324 9781483640914 9781249013471
		// 9781279835326 9781241794026 9781249011521 9781930142459 9781775412779
		// 9781930142442";
		if (numberSpacePattern.matcher(terms).find()) {
			System.out.println("numberPattern matches");
		}
	}

	private static void testListItemPopulation() {
		AcademicListService service = (AcademicListService) AcademicServiceLocator
				.getService(ServiceName.ACADEMIC_SERVICE);
		AcademicList academicList = service.findAcademicListByCustGroup("CROSSIN", "IN", "11100");
		long listId = 0L;

		if (null != academicList) {
			listId = academicList.getListId();
			Map<Long, AcademicItemData> academicItemDataMap = service.getAcademicItemDataByListId(listId);
			for (Long oprId : academicItemDataMap.keySet()) {
				System.out.println(oprId + ":" + academicItemDataMap.get(oprId));
			}

		}

	}

	private static void testUserConfig() {
		List<UserConfigField> userConfigList;

		UserConfigService service = (UserConfigService) AcademicServiceLocator
				.getService(ServiceName.USER_CONFIG_SERVICE);

		// Load the User Settings again
		userConfigList = service.getAllUserConfigByMarketSegment("ACDM");
		// Apply previously selected user settings
		userConfigList = service.applyUserSpecificSettings("CROSSIN", userConfigList);

		for (UserConfigField field : userConfigList) {
			if ("EBOOK_VEND".equalsIgnoreCase(field.getUserConfigTpCd())) {
				System.out.println("field: " + field.getUserConfigTpCd() + ", " + field.getFieldValue());
				for (UserConfigOption option : field.getValidOptions()) {
					System.out.println(option.getUserConfigFieldId() + ", " + option.getUserConfigOptDispVal() + ", "
							+ option.getUserConfigOptVal() + ", " + option.getUserConfigSeqNo() + ", "
							+ option.getUserConfigTpCd() + ", " + option.getUserConfigValdId() + ", "
							+ option.isFieldSelected());
				}

			}
		}
	}

	private static void testEneQry() {
		Pattern onePipePattern = Pattern.compile("\\|");
		Pattern twoPipesPattern = Pattern.compile("\\|{2}");

		// String url =
		// "Sf=Binding%20Formatfilter_data_sepContainsfilter_data_seppaperback&dobj=09/27/2014_12/12/2014&sortSrcType=1&simpleSearchType=Title&Ntk=Title_Exact&sortOrder=PUBDT&Ns=Title&N=0&Nso=0&listTypeId=SR&Ntt=blues*&productType=Book&Nty=1&pg=-1&Ntx=mode+matchall&searchTerm=blues*&searchBy=Title&dsplSearchTerm=blues*&productLimit=EXTD";

		// String url =
		// "simpleSearchType=ISBN&Ntk=ISBN|Keyword&Ns=CIS_FORMAT_DESC&Nr=AND(OR(15000647,15000763,15000662,15000651,15000793),NOT(RENTAL_ONLY:Y),NOT(ILS_ONLY_IND:Y),OR(15000665,15000664,15000663))&N=0&No=0&listTypeId=IN&Ntt=9781241791797
		// 9781241791636 9781241791490 9781241791445 9781483640921 9781568810638
		// 9781404358324 9781483640914 9781249013471 9781279835326 9781241794026
		// 9781249011521 9781930142459 9781775412779
		// 9781930142442|Counts&productType=All&Nty=1&pg=-1&Ntx=mode
		// matchany|mode matchall&searchBy=ISBN&dsplSearchTerm=Inbox";
		String url = "simpleSearchType=ISBN&Ntk=ISBN|Keyword&Ns=CIS_FORMAT_DESC&Nr=AND(OR(15000647,15000763,15000662,15000651,15000793),NOT(RENTAL_ONLY:Y),NOT(ILS_ONLY_IND:Y),OR(15000665,15000664,15000663))&N=0&No=0&listTypeId=IN&Ntt=9781241791797 OR 9781241791636 OR 9781241791490|Counts&productType=All&Nty=1&pg=-1&Ntx=mode matchany|mode matchall&searchBy=ISBN&dsplSearchTerm=Inbox";

		UrlGen urlg = new UrlGen(url, "UTF-8");
		UrlENEQuery enequery;
		try {
			enequery = new UrlENEQuery(URLDecoder.decode(urlg.toString(), "UTF-8"), "UTF-8");
			enequery.setAcdm(true);

			enequery.setApplyQt(false);

			for (ERecSearch recSrch : enequery.getNavERecSearches()) {
				System.out.println("terms: " + recSrch.getTerms());
			}

			if (enequery.getSort() == null || enequery.getSort().length() == 0) {
				return;
			}

			System.out.println("enequery.getSort(): " + enequery.getSort());
			String sortOrder = enequery.getSortOrder() == null ? null : enequery.getSortOrder();

			String[] rawSortKeys = null;
			try {
				String rawSortKey = URLDecoder.decode(enequery.getSort(), "UTF-8");
				rawSortKeys = twoPipesPattern.split(rawSortKey);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			for (String sortKey : rawSortKeys) {
				System.out.println("sortKey: " + sortKey);

				String[] tokens = onePipePattern.split(sortKey);
				if ("null".equalsIgnoreCase(tokens[0])) {
					continue;
				}

				SchemaEntry entry = SearchConfiguration.getSortableFieldByEndecaName(tokens[0]);
				if (entry == null) {
					System.out.println("error");
				}
				String key = (enequery.isAcdm()) ? entry.getEndecaName() : entry.getSolrName();
				System.out.println("key: " + key);

				String order = sortOrder;
				if (tokens.length == 2) {
					order = tokens[1];
				}
				if (order == null && !"0".equals(order) && !"1".equals(order)) {
					order = "0";
				}

			}
		} catch (UrlENEQueryParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void testBrowseRowDataHistory() {
		long listId = 33849l; // (IN) 33853l (sc)
		String listTypeId = "IN";
		String custGroup = "11100";
		String userId = "CROSSIN";
		String sandbox = "kboudre";

		EanCheckStatusGnmdUrls tabBuilder = new EanCheckStatusGnmdUrls.Builder("9781241791797", listId)
				.userLibGrp(custGroup).acdmUserId(userId).sandbox(sandbox).buildCheckStatusUrl();
		Set<ItemBrowseRowTabDataObj> tabCheckStatus = tabBuilder.getTabCheckStatus();

		System.out.println("tabCheckStatus: " + tabCheckStatus);
	}

	static boolean searchOver = false;

	private static void testBrowseRowDataFlow() {

		// http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest
		// &program=ipage/browserow.p&IDList=SC&user_id=CROSSIN&cust-group=11100&EANList=9781283993043,9781241791797
		// &oidOrderList=,&eBookDetailList=:,:

		// AB case
		// String reviewApprovalBooksUrl =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/browserow.p&IDList=AB"
		// +
		// "&user_id=MARTIN&cust-group=11100&EANList=9780813935447,9780199688258,9780814251881,9781438451176,9781299846661,9780199688296,9780500239193,9781442231474,9781442232594,"
		// +
		// "9780300186413,9780887485848,9780814212516,9780199371259,9780199382972,9781137406309,9780804789677,9781137311009,9780821420980,9780813037936,9781559394093,9781908836267,"
		// +
		// "9780719089282&oidOrderList=,,,,,,,,,,,,,,,,,,,,,&eBookDetailList=:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:";

		// List<String> searchEans = Arrays.asList(new String[] {
		// "9781283993043", "9781241791797", "9781241791445",
		// "9783822821671", "9781568847115", "9781859900246" });
		List<String> searchEans = Arrays.asList(new String[] { "9781559394093", "9781438451176" });

		// List<String> searchEans = Arrays.asList(new String[] {
		// "9780813935447","9780199688258","9780814251881","9781438451176",
		// "9781299846661","9780199688296","9780500239193","9781442231474","9781442232594","9780300186413","9780887485848",
		// "9780814212516","9780199371259","9780199382972","9781137406309","9780804789677","9781137311009","9780821420980",
		// "9780813037936","9781559394093","9781908836267","9780719089282"});

		final List<String> reqEans = new ArrayList<String>();
		List<String> searchEansOrderDetails = Arrays.asList(new String[] { "", "" });

		reqEans.add("9781559394093");
		reqEans.add("9781438451176");
		// reqEans.add("9781241791445");
		// reqEans.add("9783822821671");
		// reqEans.add("9781568847115");
		// reqEans.add("9781859900246");

		final long listId = 33558l; // (AB) 33849l; // (IN) 33853l (sc)
		String listTypeId = "AB"; // IN";
		String custGroup = "11100";
		String userId = "MARTIN";// CROSSIN";
		String sandbox = "kboudre";

		new BrowseRowGnmdUrls.Builder(searchEans, listId).salesOrderDetails(searchEansOrderDetails).idList(listTypeId)
				.userLibGrp(custGroup).acdmUserId(userId).sandbox(sandbox).buildBrowseRowUrl();

		// final boolean searchOver = false;
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		Runnable dataFetcher = new Runnable() {
			public void run() {
				System.out.println("outstanding eans: " + reqEans);
				if (!reqEans.isEmpty()) {
					requestCheckStatusData(reqEans, listId);
				} else {
					recheckResponse(reqEans, listId);
					System.out.println("shutting down the scheduler");
					scheduler.shutdown();
				}
			}
		};

		final ScheduledFuture<?> fetcherHandle = scheduler.scheduleAtFixedRate(dataFetcher, 10, 10, TimeUnit.SECONDS);
		scheduler.schedule(new Runnable() {
			public void run() {
				fetcherHandle.cancel(true);
			}
		}, 60 * 3, TimeUnit.SECONDS);

	}

	private static boolean requestCheckStatusData(List<String> reqEans, long listId) {
		if (!ResponseMapService.isEmpty()) {

			@SuppressWarnings("unchecked")
			List<ItemActionInfoObj> data = (List<ItemActionInfoObj>) ResponseMapService.getListData(listId);

			if (null != data && !data.isEmpty() && data.size() > 0) {
				List<JsonCheckStatusObj> uiJsonData = new ArrayList<JsonCheckStatusObj>();

				for (ItemActionInfoObj dataObj : data) {
					if (null != dataObj) {
						if (null != dataObj.getEAN()) {
							if (reqEans.contains(dataObj.getEAN())) {
								JsonCheckStatusObj jsonObj = new JsonCheckStatusObj();

								jsonObj.setPriority(Integer.parseInt(dataObj.getPriority()));
								jsonObj.setBrowseRowMessage(dataObj.getBrowseRowMessage());
								jsonObj.setCommunityGroup(dataObj.getCommunityGroup());
								jsonObj.setConsortiaShortName(dataObj.getConsortiaShortName());
								jsonObj.setEan(dataObj.getEAN());
								jsonObj.setListName(dataObj.getListName());
								jsonObj.setListOwner(dataObj.getListOwner());
								jsonObj.setListType(dataObj.getListType());
								jsonObj.setUserID(dataObj.getUserID());
								jsonObj.setOidSalesOrderDetail(dataObj.getOidSalesOrderDetail());

								uiJsonData.add(jsonObj);

								ResponsePurgeList.putObj(listId, Integer.valueOf(dataObj.hashCode()));

								ListIterator<String> reqEansItr = reqEans.listIterator();
								for (; reqEansItr.hasNext();) {
									if (reqEansItr.next().equalsIgnoreCase(dataObj.getEAN())) {
										reqEansItr.remove();
									}

								}
								System.out.println("uiJsonData: " + uiJsonData);
								new ReqCallback() {
									@SuppressWarnings({ "unchecked", "rawtypes" })
									public void executeNewTask() {
										GanimedeThreadExecutor.getReqFutures()
												.add((Future<Runnable>) GanimedeThreadExecutor.getPurgeExecutor()
														.submit(new ResponseMapPurger()));
									}
								}.executeNewTask();
							}
						}
					}

					if (reqEans.size() <= 0) {
						break;
					}

				} // for

			} // if (null != data)

			return true;

		} // if (!ResponseMapService.isEmpty())

		return false;

	}

	private static void recheckResponse(List<String> reqEans, long listId) {

		// to test the existence of the data in the response map
		System.out.println("to test the existence of the data in the response map - before purging the response map");
		reqEans.add("9781283993043");
		requestCheckStatusData(reqEans, listId);

		// ResponseMapService.removeListData(ResponsePurgeList.getData());

		System.out.println("to test the existence of the data in the response map - after purging the response map");
		reqEans.add("9781283993043");
		reqEans.add("9781241791797");
		requestCheckStatusData(reqEans, listId);

		GanimedeThreadExecutor.stopqExecutor();
		GanimedeThreadExecutor.stopDbExecutor();

	}

	private static void testRecordFilterExpression() {
		RecordFilterExpressionParser parser = new RecordFilterExpressionParser();

		String str = "AND(CIS_COUNTRIES_INCLUDED:(AU OR US OR BT)";

		try {
			str = parser.decode(parser.parse(URLDecoder.decode(str, "UTF-8")));
			System.out.println("str: " + str);
		} catch (UnsupportedEncodingException e) {
			System.out.println("UTF-8 ought to be supported.");
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void loadSchema() {
		Map<String, List<SchemaEntry>> schemaByEndecaName;
		Map<String, SchemaEntry> schemaBySolrName;
		Map<String, SchemaEntry> schemaByFacetableSolrName;
		try {
			SchemaLoader loader = new SchemaLoader(VariableData.getProperty("SOLR_SCHEMA_LOCATION"));
			schemaByEndecaName = loader.getSchemaByEndecaName();
			schemaBySolrName = loader.getSchemaBySolrName();

			System.out.println("schemaByEndecaName: " + schemaByEndecaName.keySet());
			System.out.println("schemaBySolrName: " + schemaBySolrName.keySet());

			System.out.println("schemaByEndecaName: " + schemaByEndecaName.get("CIS_FORMAT_CODE"));
			System.out.println("schemaByEndecaName: " + schemaByEndecaName.get("CIS_FORMAT_DESC"));

			schemaByFacetableSolrName = new HashMap<String, SchemaEntry>();
			for (List<SchemaEntry> entries : schemaByEndecaName.values()) {

				for (SchemaEntry entry : entries) {
					if (entry.isFacetable()) {
						schemaByFacetableSolrName.put(entry.getSolrName(), entry);
					}
				}
			}

		} catch (Exception e) {
			schemaByEndecaName = null;
			System.out.println("Error initializing search configuration" + e);
			throw new RuntimeException("Failed to initialize search configuration", e);
		}
	}

	public static void testNValLoader() {

		@SuppressWarnings("unchecked")
		Set<SelectableOptions> binding = (Set<SelectableOptions>) PowerSearchDataFactory.getData("binding");
		// Map<String, NValue> nvalMap = new LinkedHashMap<String, NValue>();
		// for (SelectableOptions option : binding) {
		// NValue nval = new NValue(0l, option.getValue(),
		// option.getDisplayValue());
		// nvalMap.put(option.getValue(), nval);
		// System.out.print(option.getValue() + "','");
		// }

		NValueRepositoryLoader nvalDao = new NValueDAO();
		try {
			nvalDao.loadAllNValues("CIS_FORMAT_CODE", binding);

			for (SelectableOptions option : binding) {
				System.out.print(option.getValue() + ":" + option.getDisplayValue() + ",");
			}

			System.out.println();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void testSolrResult() {
		SolrQueryReuseService solrService = new SolrQueryReuseService();
		List<FacetField> resp = solrService.getAllAvailableFieldValues("CIS_FORMAT_CODE");
		Map<String, String> facetsMap = new HashMap<String, String>();
		for (FacetField field : resp) {
			System.out.println("facet field: " + field.getName());
			for (Count count : field.getValues()) {
				System.out.print(count.getName() + ":" + count.getCount() + " | ");
				// StringUtils.split(count.)
				// facetsMap.put(key, value)
			}
			System.out.println();
		}
	}

	public static void sampleTest() {
		Object[] data = { "MIL", "MIL", "MIL", "MIL", "MIL", "MIL", "MIL", "MIL" };
		Object[] ldata = { 1, 1, 1, 1, 3, 3, 3, 3 };
		Collection<Object> cisEbpCatpartner = Arrays.asList(data);

		Set<EBPDataObject> cisEBPData = new ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(null)
				.cisEbpDownloadable(null).cisEbpDownloaddetails(null).cisEbpEbscocategory(null).cisEbpEbscolevel(null)
				.cisEbpLicence(Arrays.asList(ldata)).cisEbpMilaccess(null).cisEbpPrice(null).cisEbpTitleid(null)
				.getCisEBPData();

		System.out.println("cisEBPData: " + cisEBPData);
	}

	@SuppressWarnings("unchecked")
	public static void powerSearchData() {

		printGanimedeResponseData((Set<SelectableOptions>) PowerSearchDataFactory.getData("interdisciplinary"),
				"interdisciplinary");
		printGanimedeResponseData((Set<SelectableOptions>) PowerSearchDataFactory.getData("readership level"),
				"readership level");

		// times out
		// printGanimedeResponseData((Set<SelectableOptions>)
		// PowerSearchDataFactory.getData("dewey"), "dewey");
		// times out
		// printGanimedeResponseData((Set<SelectableOptions>)
		// PowerSearchDataFactory.getData("lc"), "lc");

		printGanimedeResponseData((Set<SelectableOptions>) PowerSearchDataFactory.getData("binding"), "binding");
		printGanimedeResponseData((Set<SelectableOptions>) PowerSearchDataFactory.getData("country"), "country");
		printGanimedeResponseData((Set<SelectableOptions>) PowerSearchDataFactory.getData("language"), "language");
	}

	private static void printGanimedeResponseData(Set<SelectableOptions> data, String title) {

		for (SelectableOptions object : data) {
			System.out.println(object.getValue() + "," + object.getDisplayValue());
		}
	}

	private static void powerSearchDataTest(String field) {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/cachetables.p&mode=" + field;

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		System.out.println("Ganimade Header :" + ganimedeResponse.getHeaderinfo());
		System.out.println("-----------");
		System.out.println("Key : Value");
		System.out.println("-----------");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			System.out.println(object.get("code") + "," + object.get("description"));
		}
		System.out.println("Ganimade Footer :" + ganimedeResponse.getStatus());
	}

	private static void couttsDemandTest() {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/couttsdemand.p&EAN=9780132830317";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void browseRowCheckStatusTest() {
		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
		// +
		// "&program=ipage/browserow.p&IDList=SR&user_id=crossin&cust-group=11100&EANList=9781242775239,9781159054298,9781233217106,9781455576371&oidOrderList=,,,&eBookDetailList=:,:,:,:";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
		// +
		// "&program=ipage/browserow.p&IDList=SR&user_id=chagan&cust-group=11100&EANList=9781118461037&oidOrderList=&eBookDetailList=:";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
		// +
		// "&program=ipage/browserow.p&IDList=SR&user_id=chagan&cust-group=11100&EANList=9781455576371,9781118461037&oidOrderList=,&eBookDetailList=:,:";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
		// +
		// "&program=ipage/browserow.p&IDList=IN&user_id=KLEACH&cust-group=45185&EANList=9782895444565,9782981274915,"
		// +
		// "9782764622742,9782764021422,9782764622780,9781771620062,9782760908901,9780195444926,9780992053628,9780774825719,"
		// +
		// "9782923830179,9780992048006,9781550505696,9782923896328,9781926824949,9782760633193,9781926956695,9782897333577,"
		// +
		// "9781771172585,9781770853164,9782923953076,9781771172974,9781552213438,9782896495542,9781895830781,9781551305493,"
		// +
		// "9780888875266,9782760308053,9781771173018,9782762135831,9781550814545,9782762137033,9782895973836,9782764623145,"
		// +
		// "9780433475187,9780433475316,9781443429474,9781926599328,9782896494804,9782760912687,9782760934542,9782760934573,"
		// +
		// "9782760934436,9782760934566,9782895961741,9781896238173,9782763794679,9782763719177,9782763720401,9782763718576"
		// + "&oidOrderList=,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,"
		// +
		// "&eBookDetailList=:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
		// +
		// "&program=ipage/browserow.p&IDList=SR&user_id=KLEACH&cust-group=45185&EANList=9781467721264,9781467744713,"
		// +
		// "9780307109088,9780394884325,9785517105158,9789872349813,9781475067798,9785517098078,9785517228147,9785517086754,"
		// +
		// "9785458903035,9785458937757,9785458878388,9785458829809,9785458792608,9785517343390,9781484835678,9785517087645,"
		// +
		// "9785517082299,9785517212573,9785517122971,9785517076298,9785517293510,9785458788038,9781482626131"
		// +
		// "&oidOrderList=,,,,,,,,,,,,,,,,,,,,,,,,&eBookDetailList=:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:";

		// String eans[] = { "9782895444565", "9782981274915", "9782764622742",
		// "9782764021422", "9782764622780",
		// "9781771620062", "9782760908901", "9780195444926", "9780992053628",
		// "9780774825719",
		// "9782923830179", "9780992048006", "9781550505696", "9782923896328",
		// "9781926824949", "9782760633193",
		// "9781926956695", "9782897333577", "9781771172585", "9781770853164",
		// "9782923953076", "9781771172974", "9781552213438", "9782896495542",
		// "9781895830781", "9781551305493",
		// "9780888875266", "9782760308053", "9781771173018", "9782762135831",
		// "9781550814545", "9782762137033", "9782895973836", "9782764623145",
		// "9780433475187", "9780433475316",
		// "9781443429474", "9781926599328", "9782896494804", "9782760912687",
		// "9782760934542", "9782760934573", "9782760934436", "9782760934566",
		// "9782895961741", "9781896238173",
		// "9782763794679", "9782763719177", "9782763720401", "9782763718576" };

		String eans[] = { "9782895444565", "9782981274915", "9782764622742", "9782764021422", "9782764622780" };
		String oid[] = { "1", "2" };
		List<String> searchEansOrderDetails = Arrays.asList(oid);

		List<String> searchEans = Arrays.asList(eans);
		Long listId = 1234l;

		// List<String> browseRowUrl = new BrowseRowGnmdUrls.Builder(searchEans,
		// listId).salesOrderDetails(searchEansOrderDetails).eBookDetails(null).idList("SR").userLibGrp("45185").acdmUserId("KLEACH").sandbox("kboudre").getBrowseRowUrl();

		// List<String> browseRowUrl = new
		// BrowseRowGnmdUrls.Builder(searchEans).listId(listId).salesOrderDetails(searchEansOrderDetails).eBookDetails(null).idList("SR").userLibGrp("45185").acdmUserId("KLEACH").sandbox("kboudre").getBrowseRowUrl();

		List<String> browseRowUrl = new BrowseRowGnmdUrls.Builder(searchEans).salesOrderDetails(null).eBookDetails(null)
				.userLibGrp("45185").acdmUserId("KLEACH").sandbox("kboudre").getBrowseRowUrl();

		for (String gnmdUrl : browseRowUrl) {
			System.out.println("gnmdUrl: " + gnmdUrl);
			gnmdUrl = gnmdUrl.replaceAll(" ", "%20");
			GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
			GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(gnmdUrl);

			printGanimedeResponseData(ganimedeResponse);
		}
	}

	private static void userSettingsEligibilityIstCall(String userid) {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/eligibility.p&mode=LISTING&user-id=" + userid;
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=eligibility.p/LISTING");

		if (null != ganimedeResponse.getDatalist() && !ganimedeResponse.getDatalist().isEmpty()) {

			System.out.println("keys: " + ganimedeResponse.getDatalist().get(0).keySet());
			printGanimedeResponseData(ganimedeResponse);

			for (Map<String, String> object : ganimedeResponse.getDatalist()) {
				System.out.println("Details for: " + object.get("oidIdentifier"));
				userSettingsEligibilityIIndCall(object.get("oidIdentifier"), userid);
			}
		}

	}

	private static void userSettingsEligibilityIIndCall(String oidIdentifier, String userid) {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/eligibility.p&mode=DETAILS&user-id=" + userid + "&oidIdentifier=" + oidIdentifier;
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=eligibility.p/DETAILS");

		System.out.println("keys: " + ganimedeResponse.getDatalist().get(0).keySet());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void searchOrderTest() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox="
				+ "&program=ipage/opod-search&mode=AUTO&cust-group=11100&iFoundSearch=false&start-date=20140501"
				+ "&end-date=20141130&patronOrders=false";

		url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox="
				+ "&program=ipage/opod-search&mode=AUTO&cust-group=57110&iFoundSearch=false&start-date=20140501&end-date=20140531&patronOrders=false";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("Search Orders");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			srParams.add(object.get("srParameter"));
		}
		System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void loginCallTestValues() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/ip-fields&cust-group=11100&user_id=chagan&mode=VALUES";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=VALUES");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			srParams.add(object.get("srParameter"));
		}
		System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void loginCallTestSummary() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/ip-fields&cust-group=11100&user_id=chagan&mode=SUMMARY";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=SUMMARY");

		// Set<String> srParams = new HashSet<String>();
		// List<Map<String, String>> ganimadeResponseList =
		// ganimedeResponse.getDatalist();

		// for (Map<String, String> object : ganimadeResponseList) {
		// srParams.add(object.get("srParameter"));
		// }
		// System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void loginCallTestDependencies() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/ip-fields&cust-group=11100&user_id=crossin&mode=DEPENDENCIES";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=DEPENDENCIES");

		// Set<String> srParams = new HashSet<String>();
		// List<Map<String, String>> ganimadeResponseList =
		// ganimedeResponse.getDatalist();

		// for (Map<String, String> object : ganimadeResponseList) {
		// srParams.add(object.get("srParameter"));
		// }
		// System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void loginCallTestLogin() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre"
				+ "&program=ipage/login.p&user_id=crossin";

		// http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest
		// &sandbox=kboudre&program=ipage/login.p&user_id=CROSSIN
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		if (ganimedeResponse.isRespError()) {
			System.out.println("Ganimede call failure");
			System.out.println("Header Info: " + ganimedeResponse.getHeaderinfo());
			System.out.println("status: " + ganimedeResponse.getStatus());
			System.out.println("Error: " + ganimedeResponse.isRespError());
			System.exit(1);
		}

		System.out.println("Header Info: " + ganimedeResponse.getHeaderinfo());
		System.out.println("status: " + ganimedeResponse.getStatus());
		System.out.println("Error: " + ganimedeResponse.isRespError());
		System.out.println("ipage/login.p");

		System.out.println("keys: " + ganimedeResponse.getDatalist().get(0).keySet());

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		Collection<Object> catPartner = null;
		Collection<Object> catPartnerDescription = null;
		Collection<Object> licence = null;
		Collection<Object> licenceDescription = null;

		String[] data = StringUtils.split(ganimadeResponseList.get(0).get("catPartner"), ",");
		catPartner = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		data = StringUtils.split(ganimadeResponseList.get(0).get("catPartnerDescription"), ",");
		catPartnerDescription = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		data = StringUtils.split(ganimadeResponseList.get(0).get("licence"), ",");
		licence = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		data = StringUtils.split(ganimadeResponseList.get(0).get("licenceDescription"), ",");
		licenceDescription = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		// System.out.println(catPartner);
		// System.out.println(catPartnerDescription);

		Set<EBPDataObject> cisEBPData = new ProductCisDetails.EBPBuilder(catPartner)
				.cisEbpCatpartnerDesc(catPartnerDescription).cisEbpLicence(licence)
				.cisEbpLicenceDesc(licenceDescription).getCisEBPData();

		System.out.println("cisEBPData: " + cisEBPData);

		System.out.println("creating CISUserLoginData");
		CISUserLoginData cisUserLoginData = new CISUserLoginData();
		cisUserLoginData.setCatPartner(ganimadeResponseList.get(0).get("catPartner"));
		cisUserLoginData.setCatPartnerDescription(ganimadeResponseList.get(0).get("catPartnerDescription"));
		cisUserLoginData.setLicence(ganimadeResponseList.get(0).get("licence"));
		cisUserLoginData.setLicenceDescription(ganimadeResponseList.get(0).get("licenceDescription"));

		System.out.println("cisUserLoginData.getEbpDataObjectList(): " + cisUserLoginData.getEbpDataObjectList());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void loginCallTest() {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre&program=ipage/getexchrates.p&customer-no=11100A00";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ipage/getexchrates.p");
		// printGanimedeResponseData(ganimedeResponse);
	}

	public static void testMrcDownload() {

		System.out.println("q: " + VariableData.getProperty("tibco.server.url"));

		StringBuilder txtMessage = new StringBuilder();
		UUID idOne = UUID.randomUUID();
		System.out.println(idOne);

		txtMessage.append("downloadID:" + idOne);
		txtMessage.append("user_id:crossin");
		txtMessage.append("EAN:9780538432047");

		MessagePublisher msgPblshr = new MessagePublisher("icg.uslv.ipage.coutts.marcbrowse", txtMessage.toString());
		ibg.common.VariableData.refresh();
		Thread tPblshr = new Thread(msgPblshr);
		tPblshr.start();

	}

	public static void dewey() {
		System.out.println("Start Time: " + new Date());
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/cachetables.p&mode=icdcsubject";

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		System.out.println("Start Time: " + new Date());

		System.out.println(ganimedeResponse.getDatalist().get(0).keySet());
		// printGanimedeResponseData(ganimedeResponse);
		writeData(ganimedeResponse, "dewey");

		System.out.println("Start Time: " + new Date());
	}

	public static void lc() {
		System.out.println("Start Time: " + new Date());
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/cachetables.p&mode=iclcsubject";

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		System.out.println("Start Time: " + new Date());
		System.out.println(ganimedeResponse.getDatalist().get(0).keySet());

		// printGanimedeResponseData(ganimedeResponse);
		writeData(ganimedeResponse, "lc");
		System.out.println("Start Time: " + new Date());
	}

	private static void writeData(GanimedeResponse ganimedeResponse, String type) {

		System.out.println("Writing to File Starts");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		FileWriter writer = null;
		BufferedWriter bufWriter = null;

		try {

			String logFileName = "C:/ingram/Tasks/power-search/" + type + ".txt";
			writer = new FileWriter(new File(logFileName));
			bufWriter = new BufferedWriter(writer);

			for (Map<String, String> object : ganimadeResponseList) {
				for (String key : object.keySet()) {
					bufWriter.write(key + ":" + object.get(key) + "\n");
				}
				bufWriter.write("\n");
			}

			bufWriter.close();
			writer.close();

		} catch (IOException e) {
		} finally {
			try {
				bufWriter.close();
				writer.close();
			} catch (IOException e) {

			}

		}

		System.out.println("Writing Over");
	}

	private static void profileTab() {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/approvaltab.p&part-no=3034802900&cust-group=26000";

		// web url
		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=web/programs/apcenters.p&mode=select&part-no=3034802900&cust-group=26000";

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/approvaltab.p&part-no=9089441174&cust-group=11100";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		System.out.println(ganimedeResponse.getDatalist().get(0).keySet());
		filterProfileData(ganimedeResponse);

		// printGanimedeResponseData(ganimedeResponse);
	}

	private static void filterProfileData(GanimedeResponse ganimedeResponse) {

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		List<List<Map<Integer, profilematch>>> filteredData = new LinkedList<List<Map<Integer, profilematch>>>();
		List<Map<Integer, profilematch>> tableRows = null;
		Map<Integer, profilematch> rowData = null;
		int ctr = 0;
		int ctr1 = 0;

		for (Map<String, String> probj : ganimadeResponseList) {

			Map<Integer, String> colStyle = new HashMap<Integer, String>();

			if (null != probj.get("web.line;System.Int32;;")) {
				if ("0".equalsIgnoreCase(probj.get("web.line;System.Int32;;"))) {

					String[] colStyles = SearchConstants.tildePattern.split(probj.get("web.data;System.String;;:U"));
					ctr = 0;
					for (String vals : colStyles) {
						vals = StringUtils.strip(vals, ":U");
						colStyle.put(ctr++, vals);
					}
					ctr = 0;
					tableRows = new LinkedList<Map<Integer, profilematch>>();

					for (Map<String, String> chobj : ganimadeResponseList) {
						if (chobj.get("web.name;System.String;;")
								.equalsIgnoreCase(probj.get("web.name;System.String;;"))
								&& !"0".equalsIgnoreCase(chobj.get("web.line;System.Int32;;"))) { // Variables

							String[] colVals = null;
							try {

								byte[] utf8Bytes = chobj.get("web.data;System.String;;:U").getBytes("UTF8");
								chobj.put("web.data;System.String;;:U", new String(utf8Bytes, "UTF8"));
								colVals = SearchConstants.tildePattern
										.split(StringUtils.strip(chobj.get("web.data;System.String;;:U"), ":U"));

							} catch (UnsupportedEncodingException e) {
								System.out.println("exception: " + e);
							}

							// colVals =
							// SearchConstants.tildePattern.split(chobj.get("web.data;System.String;;:U"));

							rowData = new HashMap<Integer, profilematch>();

							ctr1 = 0;
							for (String vals : colVals) {
								profilematch data = new profilematch();
								/*
								 * try { byte[] utf8Bytes =
								 * vals.getBytes("UTF8"); vals = new
								 * String(utf8Bytes, "UTF8"); vals =
								 * StringUtils.strip(vals, ":U");
								 * 
								 * } catch (UnsupportedEncodingException e) {
								 * System.out.println("exception: " + e); }
								 */
								// vals = StringUtils.strip(vals, ":U");
								// dirtyString.replaceAll("[^a-zA-Z0-9]","");

								data.setData(vals);
								data.setProps("style", colStyle.get(ctr1));
								rowData.put(ctr1++, data);
							}
							ctr1 = 0;
							tableRows.add(rowData);
						} // if

					} // for
					if (null != tableRows) {
						filteredData.add(tableRows);
					}
				} // if

			} // if

		} // for

		for (List<Map<Integer, profilematch>> object : filteredData) {
			for (Map<Integer, profilematch> chobj : object) {
				for (Integer key : chobj.keySet()) {
					System.out.println(key + "=>" + chobj.get(key).getData() + "=>" + chobj.get(key).getProp("style"));
				}
			}
			System.out.println("-------");
		}

	}

	static class profilematch {
		private String data;
		private Map<String, String> props;

		profilematch() {
			props = new HashMap<String, String>();
		}

		/**
		 * @return the data
		 */
		public String getData() {
			return data;
		}

		/**
		 * @param data
		 *            the data to set
		 */
		public void setData(String data) {
			this.data = data;
		}

		/**
		 * @return the props
		 */
		public Map<String, String> getProps() {
			return props;
		}

		/**
		 * @param props
		 *            the props to set
		 */
		public void setProps(String propKey, String propVal) {
			this.props.put(propKey, propVal);
		}

		public String getProp(String propKey) {
			return this.props.get(propKey);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "profilematch [data=" + data + ", props=" + props + "]";
		}

	}

	private static void powerSearch() {

		// mode = icareastudies / icreadership / icdcsubject / iclcsubject /
		// sfbt / country / language /
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/cachetables.p"
				+ "&mode=icreadership&cust-group=11100";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void checkStatusTest() {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/browserow.p&IDList=IN&user_id=rma8de&cust-group=WAG320&EANList=9780729541039,9780729541374,9781118452387,9780071016261,9781285065359,9781118554142,9780729541107,9781280468391,9781408075746,9780195576351&oidOrderList=MIL:4,MIL:4,MIL:4,MIL:4,MIL:4,MIL:3,MIL:4,MIL:4,MIL:4,MIL:4&eBookDetailList=:,:,:,:,:,:,:,:,:,:";

		String reviewApprovalBooksUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/browserow.p&IDList=AB"
				+ "&user_id=MARTIN&cust-group=11100&EANList=9780813935447,9780199688258,9780814251881,9781438451176,9781299846661,9780199688296,9780500239193,9781442231474,9781442232594,"
				+ "9780300186413,9780887485848,9780814212516,9780199371259,9780199382972,9781137406309,9780804789677,9781137311009,9780821420980,9780813037936,9781559394093,9781908836267,"
				+ "9780719089282&oidOrderList=,,,,,,,,,,,,,,,,,,,,,&eBookDetailList=:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:,:";

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(reviewApprovalBooksUrl);

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void customTest() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();

		// String srchOrderOrderInfoUrl =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/opod-search&mode=AUTO&orderstatus=ALL&end-date=20131015&limit=5000&start-date=20131015&cust-group=26000";
		// String srchOrderOrderInfoUrl =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/opod-search&mode=loadorderinfo&oidSalesOrderDetail=01L5CC5A".replaceAll("
		// ",
		// "%20");

		String prePubUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/icpmala-select.p&mode=SELECT&cust-group=11100&start-date=10012013&end-date=10312013&profile=&filter";

		String alibrisUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/hardtofind.p&EAN=9780201634594&skulist=sku1,sku2,sku3&pubList=33.54,8.34,8.34&cust-group=26000&user_id=caseacq";

		// mgmt.rep.single.subj.dewey
		// mgmt.rep.single.subj.lc
		// mgmt.rep.sub.class.dewey
		// mgmt.rep.sub.class.lc

		HttpCallUrl gnmdUrl = new HttpCallUrl("mgmt.rep.sub.class.dewey");
		gnmdUrl.setString(2, "kboudre");
		gnmdUrl.setString(3, "CO");
		String url = gnmdUrl.getGnmdUrl();

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/ip-fields&cust-group=81200&user_id=SEL8ja&mode=SUMMARY";
		url = url.replaceAll(" ", "%20");
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		printGanimedeResponseData(ganimedeResponse);
	}

	private static void printPrePub() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/icpmala-select.p&mode=SELECT&cust-group=11100&start-date=04012014&end-date=04042014&profile=&filter=";
		// http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/icpmala-select.p&mode=SELECT&cust-group=11100&start-date=04012014&end-date=05012014&profile=&filter=
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		System.out.println("keys: " + ganimedeResponse.getDatalist().get(0).keySet());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void orderInfoTest() {
		HttpCallUrl gnmdUrl = new HttpCallUrl("EDIT_ORDER_INFO");
		gnmdUrl.setString(2, "kboudre");
		gnmdUrl.setString(3, "ACU");// 11100;81200
		gnmdUrl.setString(4, "fbruce");// crossin;SEL8ja
		gnmdUrl.setString(5, "SUMMARY");
		String url = gnmdUrl.getGnmdUrl();

		// String url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/ip-fields&cust-group=81200&user_id=SEL8ja&mode=SUMMARY";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void profileTest() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		HttpCallUrl gnmdUrl = new HttpCallUrl("mgmt.rep.profile.from.to");
		gnmdUrl.setString(2, "kboudre");
		gnmdUrl.setString(3, "CO");
		gnmdUrl.setString(4, "26000");
		String url = gnmdUrl.getGnmdUrl();

		url = url.replaceAll(" ", "%20");
		System.out.println("url: " + url);
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		printGanimedeResponseData(ganimedeResponse);
		createDropDown(ganimedeResponse);

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void invoiceHistoryTest() {
		String editOrderInfoUrl = AcademicServiceLocator.getApplEnv().getProperty("INVOICE_HISTORY");
		HttpCallUrl gnmdUrl = new HttpCallUrl(true, editOrderInfoUrl);
		gnmdUrl.setString(2, "kboudre");
		gnmdUrl.setString(3, "57110");
		gnmdUrl.setString(4, "01012014");
		gnmdUrl.setString(5, "12312014");
		String url = gnmdUrl.getGnmdUrl();
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		for (Map<String, String> object : ganimadeResponseList) {

			if (!"I".equalsIgnoreCase(object.get("opih.invoiceType"))
					&& !"C".equalsIgnoreCase(object.get("opih.invoiceType"))) {
				for (String key : object.keySet()) {
					System.out.println(key + ":" + object.get(key));
				}
			}

		}

		// printGanimedeResponseData(ganimedeResponse);
	}

	private static void testGnmdUrl() {
		String editOrderInfoUrl = AcademicServiceLocator.getApplEnv().getProperty("EDIT_ORDER_INFO");
		HttpCallUrl gnmdUrl = new HttpCallUrl(true, editOrderInfoUrl);
		gnmdUrl.setString(2, "kboudre");
		gnmdUrl.setString(3, "26000");
		gnmdUrl.setString(4, "Crossin");
		gnmdUrl.setString(5, "SUMMARY");
		String summaryUrl = gnmdUrl.getGnmdUrl();
		System.out.println("summaryUrl: " + summaryUrl);
	}

	private static void patterMatch() {
		Map<Integer, String> params = new LinkedHashMap<Integer, String>();
		params.put(2, "kboudre");
		params.put(3, "ACU");
		params.put(4, "Crossin");
		params.put(5, "xyz");

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=?&sessionID=CouttsGanimedeRequest&program=ipage/ip-fields&cust-group=?&user_id=?&mode=?";
		// url =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=?&sessionID=CouttsGanimedeRequest&program=ipage/programs/otunnel&statement=FIND
		// arlg WHERE arlg.company='?' AND arlg.cust-group='?' NO-LOCK. FOR EACH
		// arcust OF arlg NO-LOCK, EACH opappm WHERE
		// opappm.oidCustomer=arcust.oidCustomer NO-LOCK, EACH opappmh WHERE
		// opappmh.oidApprovalPlan=opappm.oidApprovalPlan AND
		// opappmh.profileVersion = 0 AND LOOKUP(opappmh.selection,'B') > 0
		// NO-LOCK BY opappm.approvalPlan: RUN outputRow.
		// END.&rowText=STRING(opappmh.description)\'\\t\'STRING(opappm.approvalPlan)&columnText='opappmh.description\\topappm.approvalPlan'";
		Matcher gm = SearchConstants.qp.matcher(url);
		StringBuffer gsb = new StringBuffer();
		int count = 1;
		while (gm.find()) {
			if (params.containsKey(count)) {
				if (null != params.get(count)) {
					gm.appendReplacement(gsb, params.get(count));
				}
			}
			count++;

		}
		gm.appendTail(gsb);
		System.out.println(gsb.toString());

		// Pattern p = Pattern.compile("cat");
		// Matcher m = p.matcher("one cat two cats in the yard");
		// StringBuffer sb = new StringBuffer();
		// while (m.find()) {
		// m.appendReplacement(sb, "dog");
		// }
		// m.appendTail(sb);
		// System.out.println(sb.toString());
	}

	private static void createDropDown(GanimedeResponse ganimedeResponse) {
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		Set<SelectableOptions> profile = new TreeSet<SelectableOptions>();
		profile.add(new SelectableOptions("all", "- All -"));

		for (Map<String, String> object : ganimadeResponseList) {

			StringBuilder profileDispVal = new StringBuilder("(");
			profileDispVal.append(object.get("opappm.approvalPlan")).append(") - ")
					.append(object.get("opappmh.description"));

			profile.add(new SelectableOptions(object.get("opappm.approvalPlan"), profileDispVal.toString()));
		}

		System.out.println("Printing drop dwon values");
		for (SelectableOptions option : profile) {
			System.out.println(option.getValue() + " => " + option.getDisplayValue());
		}
	}

	private static void printGanimedeResponseData(GanimedeResponse ganimedeResponse) {
		System.out.println("Ganimade Header :" + ganimedeResponse.getHeaderinfo());
		System.out.println("-----------");
		System.out.println("Key : Value");
		System.out.println("-----------");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			for (String key : object.keySet()) {
				System.out.println(key + ":" + object.get(key));
			}
			System.out.println();
		}
		System.out.println("Ganimade Footer :" + ganimedeResponse.getStatus());
	}

	private static void printGanimedeResponseData(List<Map<String, String>> ganimadeResponseList) {
		System.out.println("Key : Value");
		System.out.println("-----------");

		for (Map<String, String> object : ganimadeResponseList) {
			for (String key : object.keySet()) {
				System.out.println(key + ":" + object.get(key));
			}
			System.out.println();
		}
	}

	private static void callMarc(GanimedeResponse ganimedeResponse) {
		int recordCount = 1;
		System.out.println("Response :" + ganimedeResponse);
		System.out.println("Ganimade Header :" + ganimedeResponse.getHeaderinfo());
		System.out.println("-----------");
		System.out.println("Key : Value");
		System.out.println("-----------");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			System.out.println("------- Record : " + recordCount + "-------");
			for (String key : object.keySet()) {

				System.out.println(key + ":" + object.get(key));
			}
			recordCount++;
		}
		System.out.println("Ganimade Footer :" + ganimedeResponse.getStatus());
	}

	private static void testActionsAvailableToUser(String userId, String listTypeId, String userRole) {

		PopulateUserRoleActions populateUserRoleActions = (PopulateUserRoleActions) AcademicServiceLocator
				.getBean(BeanName.POPULATE_USER_ROLE_ACTION);
		List<AcademicArrowActions> actions = populateUserRoleActions.getUserRoleArrowActions(listTypeId, userRole);

		System.out.println("actions available: ");
		for (AcademicArrowActions action : actions) {
			System.out.println(action);
		}
		List<AcademicArrowActions> actionsCopy = new ArrayList<AcademicArrowActions>();
		UserAdminService userAdminService = (UserAdminService) AcademicServiceLocator
				.getService(ServiceName.USER_ADMIN_SERVICE);
		CisUser cisUser = userAdminService.getCisUserInfo(userId);

		System.out.println("cisUser: " + cisUser);
		boolean addThisAction = true;
		for (AcademicArrowActions action : actions) {
			// private boolean firmPlaceOrder;
			// private boolean firmPlaceClaim ;
			// private boolean firmPlaceCancel;

			addThisAction = true;
			if ("Block on Approval".equalsIgnoreCase(action.getName().trim()) && !cisUser.getFirmPlaceCancel()) {
				addThisAction = false;
			} else if (SearchConstants.orderPattern.matcher(action.getName()).find() && !cisUser.getFirmPlaceOrder()) {
				addThisAction = false;
			} else if (SearchConstants.claimPattern.matcher(action.getName()).find() && !cisUser.getFirmPlaceClaim()) {
				addThisAction = false;
			}
			if (addThisAction) {
				actionsCopy.add(action);
			}
		}
		actions.clear();
		actions.addAll(actionsCopy);
		actionsCopy.clear();
		actionsCopy = null;

		System.out.println("actions optimized: ");
		for (AcademicArrowActions action : actions) {
			System.out.println(action);
		}

	}

	/*
	 * private static void makeComboBox(GanimedeResponse ganimedeResponse){
	 * List<Map<String, String > lists = ganimedeResponse.getDatalist(); List
	 * <String> selectHeader = new ArrayList<String>(); List<String> valueHeader
	 * = new ArrayList<String>(); int count = 0; for(Map<String,List<String>>
	 * map : lists){ count = 0; for(String key : map.keySet()){ count++;
	 * List<String> value = map.get(key); if(value.size() ==1){ if(count ==1){
	 * valueHeader.add(StringUtils.trim(value.get(0))); }else if(count >1){
	 * selectHeader.add(StringUtils.trim(value.get(0))); } } } }
	 * selectBoxCreation(selectHeader, valueHeader); } private static void
	 * selectBoxCreation(List<String> selectHeader, List<String> valueHeader) {
	 * int count; Map<String, String> selectBox = new LinkedHashMap<String,
	 * String>(); if(selectHeader.size()==valueHeader.size()){ count = 0;
	 * for(count=0;count<selectHeader.size();count++){ String selectKey =
	 * selectHeader.get(count); String selectValue =
	 * "("+selectKey+")"+"-"+valueHeader.get(count); selectBox.put(selectKey,
	 * selectValue); } } printSelectBox(selectBox); } private static void
	 * printSelectBox(Map<String, String> selectBox){ for(String
	 * selectKey:selectBox.keySet()){ String selectValue =
	 * selectBox.get(selectKey);
	 * System.out.println(selectKey+"<-:->"+selectValue); } } private static
	 * void print(GanimedeResponse ganimedeResponse){ int recordCount = 1;
	 * System.out.println("Response :"+ganimedeResponse); System.out.println(
	 * "Ganimade Header :"+ganimedeResponse.getHeaderinfo());
	 * System.out.println("-----------"); System.out.println("Key : Value");
	 * System.out.println("-----------"); List<Map<String, List<String> >>
	 * ganimadeResponseList = ganimedeResponse.getDatalist();
	 * 
	 * for(Map<String, List<String>> object:ganimadeResponseList){
	 * System.out.println("------- Record : "+ recordCount + "-------");
	 * for(String key : object.keySet()){ List<String> values = object.get(key);
	 * StringBuffer sbr = new StringBuffer(""); int count = 0; for(String val :
	 * values){ if(count > 0) sbr.append(","); sbr.append(val); }
	 * System.out.println(key +" : "+sbr.toString()); } recordCount++; }
	 * System.out.println("Ganimade Footer :"+ganimedeResponse.getStatus()); }
	 */
	
	private static class CustomThreadFactoryBuilder {

		private String namePrefix = null;
		private boolean daemon = false;
		private int priority = Thread.NORM_PRIORITY;

		public CustomThreadFactoryBuilder setNamePrefix(String namePrefix) {
			if (namePrefix == null) {
				throw new NullPointerException();
			}
			this.namePrefix = namePrefix;
			return this;
		}

		public CustomThreadFactoryBuilder setDaemon(boolean daemon) {
			this.daemon = daemon;
			return this;
		}

		public CustomThreadFactoryBuilder setPriority(int priority) {

			if (priority < Thread.MIN_PRIORITY) {
				throw new IllegalArgumentException(
						String.format("Thread priority (%s) must be >= %s", priority, Thread.MIN_PRIORITY));
			}

			if (priority > Thread.MAX_PRIORITY) {
				throw new IllegalArgumentException(
						String.format("Thread priority (%s) must be <= %s", priority, Thread.MAX_PRIORITY));
			}

			this.priority = priority;
			return this;
		}

		public ThreadFactory build() {
			return build(this);
		}

		private ThreadFactory build(CustomThreadFactoryBuilder builder) {
			final String namePrefix = builder.namePrefix;
			final Boolean daemon = builder.daemon;
			final Integer priority = builder.priority;

			final AtomicLong count = new AtomicLong(0);

			return new ThreadFactory() {
				@Override
				public Thread newThread(Runnable runnable) {
					Thread thread = new Thread(runnable);
					if (namePrefix != null) {
						thread.setName(namePrefix + "-" + count.getAndIncrement());
					}
					if (daemon != null) {
						thread.setDaemon(daemon);
					}
					if (priority != null) {
						thread.setPriority(priority);
					}
					return thread;
				}
			};
		}

	}

}
