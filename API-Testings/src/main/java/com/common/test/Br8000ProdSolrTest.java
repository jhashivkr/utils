package com.common.test;

import ibg.academics.dto.AcademicAlibrisItem;
import ibg.academics.dto.AcademicList;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.academics.service.SolrUtilityService;
import ibg.administration.CISUserLoginData;
import ibg.browserow.dto.AcademicItemData;
import ibg.common.MarketSegment;
import ibg.common.utility.MultiCurrencyUtility;
import ibg.product.information.EBPDataObject;
import ibg.product.information.Product;
import ibg.product.information.service.ProductInformationException;
import ibg.product.information.service.ProductInformationServiceImpl;
import ibg.product.search.EbookInfoCmprtrHelper;
import ibg.product.search.ListSortFilterHelper;
import ibg.product.search.SearchResultDataBean;
import ibg.product.search.UserEbookDispOptions;
import ibg.product.search.acdm.SearchConstants;
import ibg.product.search.acdm.SolrSearchFieldsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public final class Br8000ProdSolrTest {

	private long listId; // list id of the list
	private String listTypeName;

	private List<SearchResultDataBean<Product>> searchResult;

	private ProductInformationServiceImpl solrSrv = new ProductInformationServiceImpl();
	private StringBuilder languageListBuilder = new StringBuilder();
	private StringBuilder statusBuilder = new StringBuilder();

	private Map<String, SolrDocument> solrDataMap;

	private long eRecsOffset;
	private long recSize;
	private long totalNumERecs;
	private long totRecOnPage;
	private long userSetMaxRecSize;
	private long startRec;
	private long endRec;

	private Map<String, List<Long>> eanOprMap;
	private Map<Long, AcademicItemData> academicItemDataMap;
	private Map<Long, AcademicAlibrisItem> alibrisItemDataMap;
	private CISUserLoginData cisUserLoginData;

	private Set<String> solrFetchSearchEans;

	private Map<String, Boolean> ebookOption;
	private String thisUser;

	private String listType;

	private Map<String, String> cisEbpCatpartnerDescMap = new HashMap<String, String>(0);

	private boolean powerSearch;
	private String targetSwitchEan;
	private String itemId;

	public Br8000ProdSolrTest(Map<String, List<Long>> eanOprMap, CISUserLoginData cisUserLoginData, Map<String, SolrDocument> solrDataMap, String userId,
			Map<Long, AcademicItemData> academicItemDataMap, AcademicList academicList) {

		this.eanOprMap = eanOprMap;
		this.cisUserLoginData = cisUserLoginData;
		thisUser = userId;
		this.listType = academicList.getListType();
		this.listId = academicList.getListId();

		this.academicItemDataMap = academicItemDataMap;
		this.solrDataMap = solrDataMap;

		solrSrv.setMarketSegment(MarketSegment.ACDM);
		// powerSearch = "power".equalsIgnoreCase(request.getParameter("searchType"));

		try {
			// System.out.println("recs: " + recs.size());
			searchResult = new LinkedList<SearchResultDataBean<Product>>(); // the size should be the users page size
			solrFetchSearchEans = new LinkedHashSet<String>();

			buildSolrProduct();

			// eRecsOffset = response.getERecsOffset() + 1;
			// recSize = response.getERecsOffset() + recs.size();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private final void buildSolrProduct() {

		Product solrProduct = null;
		CISUserLoginData cisUserLoginDataCopy = CISUserLoginData.newInstance(cisUserLoginData);

		// user ebook disp config to be got
		UserEbookDispOptions userEbookDispOptions = new UserEbookDispOptions();
		ebookOption = userEbookDispOptions.getUserEbookDispOptions("ACDM", thisUser);
		try {
			// IPG-697
			cisEbpCatpartnerDescMap.putAll(userEbookDispOptions.fetchEbookCatPartnerDes(cisUserLoginData.getEbpDataObjectMap()));

		} catch (Exception e) {
			// e.printStackTrace();
		}
		// leave this code commented till a decision is taken about the impact
		// of user selection of Ebook platforms from the change setting page

		// remove those ebpdataobject from cisuserlogindata which is not in user selection
		// if (null != ebookOption && !ebookOption.isEmpty()) {
		// Boolean viewThisEbook = false;

		// for (String key : ebookOption.keySet()) {
		// try {
		// viewThisEbook = Boolean.valueOf(ebookOption.get(key));
		// if (!viewThisEbook && cisUserLoginData.getEbpDataObjectMap().containsKey(key)) {
		// cisUserLoginData.getEbpDataObjectMap().remove(key);
		// } else if (!cisUserLoginData.getEbpDataObjectMap().containsKey(key)) {
		// cisUserLoginData.getEbpDataObjectMap().remove(key);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// continue;
		// }
		// }
		// }

		if (powerSearch) {

			try {
				if (null != cisUserLoginData.getPowerSearchData("eBookPlatformSelect")) {
					// keep only those ebook platforms in the userLoginData which is
					// selected by the user

					String[] userSelEbooks = cisUserLoginData.getPowerSearchData("eBookPlatformSelect").toString().split("\\,");
					if (null != userSelEbooks) {

						Set<String> userEbpKeys = new HashSet<String>(cisUserLoginDataCopy.getEbpDataObjectMap().keySet());
						userEbpKeys.removeAll(Arrays.asList(userSelEbooks));

						cisUserLoginDataCopy.getEbpDataObjectMap().keySet().removeAll(userEbpKeys);

					}
				}

			} catch (Exception e) {
				// e.printStackTrace();
			}

		}

		try {

			int recsCtr = 0;
			boolean switchEan = false;

			// targetSwitchEan = (String) request.getSession().getAttribute("recent_switch_ean");
			// itemId = (String) request.getSession().getAttribute("recent_switch_item");

			Map<String, Boolean> ebookOption;
			ebookOption = new UserEbookDispOptions().getUserEbookDispOptions("ACDM", thisUser);

			for (String isbn : eanOprMap.keySet()) {

				//System.out.println("isbn: " + isbn);
				languageListBuilder.delete(0, languageListBuilder.length());
				statusBuilder.delete(0, statusBuilder.length());

				@SuppressWarnings("unchecked")
				// Map<String, String> switchEanMap = (Map<String, String>)
				// request.getSession().getAttribute("switch_ean_map");
				Map<String, String> switchEanMap = new HashMap<String, String>();

				// String ean = (String) rec.get("EAN");
				solrProduct = new Product();
				switchEan = false;

				// switch case evaluation must not be done here
				// the isbn must be switched in the session isbn map
				// if (null != switchEanMap && switchEanMap.containsKey(ean) &&
				// !AcademicListType.SEARCH_ORDERS.getListTypeId().equals(listType)) {
				// SolrUtilityService solrService = (SolrUtilityService)
				// AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);

				// String tragetEan = getMapValue(switchEanMap, ean);

				// switchEanMap.put(ean, tragetEan);
				// request.getSession().setAttribute("switch_ean_map", switchEanMap);
				// solrService.setSearchEans(tragetEan, 1);
				// solrProduct = solrService.fetchProductDetatilsFromSolr().get(0);
				// switchEan = true;
				// rec = solrService.fetchNewErec();

				// } else {
				solrSrv.loadProductData(solrDataMap.get(isbn), solrProduct);
				// }

				SearchResultDataBean<Product> productDetail = new SearchResultDataBean<Product>(solrProduct);

				// load alternate details
				loadAlternateDetails(solrProduct, productDetail, cisUserLoginDataCopy);

				// get the products price
				// Ebook analyzing is done in the same method
				productPriceFinder(productDetail, cisUserLoginDataCopy, solrDataMap.get(isbn));
				
				if (null != productDetail.getOthersAvailable() && !productDetail.getOthersAvailable().isEmpty() && !solrProduct.isShowFormatTab()) {
					for (EBPDataObject ebpDataObject : productDetail.getOthersAvailable()) {
						if (null != ebookOption) {
							if (null != ebpDataObject.getCisEbpCatpartner() && ebookOption.get(ebpDataObject.getCisEbpCatpartner())) {
								solrProduct.setShowFormatTab(true);
								break;
							}
						}// if

					}
				}

				productDateFinder(productDetail, solrDataMap.get(isbn));

				productDetail.setProdRowNoOnPage(recsCtr++);
				// solrFetchSearchEans.add(solrProduct.getEan());

				Long oprId = null;
				try {
					oprId = eanOprMap.get(solrProduct.getEan()).get(0);
				} catch (Exception e) {
					oprId = Long.parseLong(solrProduct.getEan());
					if (academicItemDataMap.containsKey(oprId)) {
						oprId = Long.parseLong(productDetail.getSolrProduct().getEan());
					} else {
						oprId = 0l;
					}
				}
				productDetail.setOprId(oprId);
				//buildSolrProductProperties(productDetail);

				if (academicItemDataMap.containsKey(oprId)) {
					AcademicItemData academicItemData = academicItemDataMap.get(oprId);
					productDetail.setAcademicData(academicItemData);

					// replace the licence and platform with the information
					// available (if) in the order info details of this product
					try {
						if (null != productDetail.getAcademicData().getLicense() && !productDetail.getAcademicData().getLicense().isEmpty()) {
							productDetail.setDispEbookLicense(productDetail.getAcademicData().getLicense());
						}
					} catch (Exception e) {
						continue;
					}
				}

				// For Alibris List
				if (null != alibrisItemDataMap && alibrisItemDataMap.containsKey(oprId)) {
					setAlibrisInfo(productDetail, oprId);
				}

				searchResult.add(productDetail);
				// }// else
				// }// if (null != eanOprMap && eanOprMap.containsKey(solrProduct.getEan()))

				solrProduct = null;

			}// for (String isbn : eanOprMap.keySet())

		} catch (ProductInformationException e) {
			e.printStackTrace();
		}
	}

	private void loadAlternateDetails(Product solrProduct, SearchResultDataBean<Product> productDetail, CISUserLoginData cisUserLoginDataCopy) {

		// get the alternate ean details
		if (null != solrProduct.getAltEan() && !solrProduct.getAltEan().isEmpty()) {
			List<String> invalidFormatCode = Arrays.asList(new String[] { "DA", "DM", "DZ", "EH", "EL", "IA", "OA", "OL", "PW", "UB", "U" });
			// get details of the alternate eans
			List<Object> altEans = new LinkedList<Object>(solrProduct.getAltEan());

			// SolrUtilityService solrService = (SolrUtilityService)
			// AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);
			// solrService.setSearchEans(StringUtils.join(altEans, ','), altEans.size());
			// solrService.setCisUserLoginData(cisUserLoginDataCopy);

			List<Product> altProducts = buildAltSolrProduct(altEans);

			// if (switchEan) {
			// solrService.setSearchFields(null);
			// } else {
			// solrService.setSearchFields(SolrSearchFieldsService.getAltEansBrowseRowReqFields());
			// }

			// get the alternates
			List<Product> altEansList = new ArrayList<Product>();
			for (Product altEanProducts : altProducts) {

				if (!invalidFormatCode.contains(altEanProducts.getCisFormatCode())) {
					// analyze the ebook eligibility
					if (null != altEanProducts.getCisEBPData() && null != cisUserLoginDataCopy.getEbpDataObjectMap()) {

						EbookInfoCmprtrHelper ebookInfoCmprtr = new EbookInfoCmprtrHelper(cisUserLoginDataCopy, academicItemDataMap, solrProduct);
						ebookInfoCmprtr.analyzeForAlternate(altEanProducts);

					}
				}

				try {
					if (null != altEanProducts.getCisEBPData() && !altEanProducts.getCisEBPData().isEmpty()) {
						// if (null != ebookOption) {
						for (EBPDataObject ebp : altEanProducts.getCisEBPData()) {
							// if (null != ebp.getCisEbpCatpartner() && null !=
							// ebookOption.get(ebp.getCisEbpCatpartner()) &&
							// ebookOption.get(ebp.getCisEbpCatpartner()).booleanValue()) {
							if (null != ebp.getCisEbpCatpartner() && !ebp.getCisEbpCatpartner().isEmpty()) {
								altEansList.add(altEanProducts);

								StringBuilder reqParam = new StringBuilder();
								reqParam.append("platform=").append(ebp.getCisEbpCatpartner()).append("&titleId=").append(ebp.getCisEbpTitleid());
								productDetail.setAltEbookSeeInsideDetails(reqParam.toString());
								break;
							}
						}// for
							// }// if

					}// if
					else {
						altEansList.add(altEanProducts);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			}// for
			if (null != altEansList && !altEansList.isEmpty()) {
				solrProduct.setShowFormatTab(true);
			}

			solrProduct.setRelatedProducts(altEansList);
			solrProduct.setAltProducts(altEansList);
		}// alternate eans
	}

	private void setAlibrisInfo(SearchResultDataBean<Product> productDetail, Long oprId) {
		// For Alibris List
		if (null != alibrisItemDataMap && alibrisItemDataMap.containsKey(oprId)) {
			AcademicAlibrisItem alibrisItem = alibrisItemDataMap.get(oprId);
			if (alibrisItem.getPrice() != null) {
				productDetail.getAcademicData().setAlibrisCopyPrice(alibrisItem.getPrice().floatValue());
			} else {
				productDetail.getAcademicData().setAlibrisCopyPrice(0F);
			}
		}
	}
	
	private List<Product> buildAltSolrProduct(List<Object> altEans) {

		ListSortFilterHelper sortHlpr = ((ListSortFilterHelper) AcademicServiceLocator.getBean(BeanName.SORT_HLPR));
		QueryResponse solrResponseAlt = sortHlpr.getIsbnDetailsFromSolr(altEans.size(), altEans);

		Product solrProduct = null;
		List<Product> productFormatlist = new ArrayList<Product>(0);

		if (null != solrResponseAlt && null != solrResponseAlt.getResults()) {
			SolrDocumentList docList = solrResponseAlt.getResults();
			if (null != docList && !docList.isEmpty()) {
				try {
					for (SolrDocument rec : docList) {
						solrProduct = new Product();
						solrSrv.loadProductData(rec, solrProduct);

						// fetch multy currency data for non-ebook item only
						if (cisUserLoginData != null && (solrProduct.getCisEBPData() == null || solrProduct.getCisEBPData().isEmpty())) {
							MultiCurrencyUtility.fetchProdMultiCurrency(cisUserLoginData.getCurrencyPreferences(), rec, solrProduct);
						}

						productFormatlist.add(solrProduct);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return productFormatlist;
	}

	private String getMapValue(Map<String, String> ma, String key) {

		String targetVal = "";
		List<String> keyList = new ArrayList<String>();
		while (null != ma.get(key)) {
			if (keyList.contains(key))
				break;
			keyList.add(key);
			targetVal = ma.get(key);
			ma.remove(key);
			key = targetVal;

		}
		return targetVal;

	}

	/**
	 * @return the ebookOption
	 */
	public Map<String, Boolean> getEbookOption() {
		return ebookOption;
	}

	public List<SearchResultDataBean<Product>> getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(List<SearchResultDataBean<Product>> searchResult) {
		this.searchResult = searchResult;
	}

	public long getListId() {

		return listId;
	}

	public void setListId(long listId) {
		this.listId = listId;
	}

	public String getListTypeName() {
		return listTypeName;
	}

	public void setListTypeName(String listTypeName) {
		this.listTypeName = listTypeName;
	}

	public long getERecsOffset() {
		return eRecsOffset;
	}

	public long getRecSize() {
		return recSize;
	}

	public long getTotalNumERecs() {
		return totalNumERecs;
	}

	public long getTotRecOnPage() {
		return totRecOnPage;
	}

	public long getUserSetMaxRecSize() {
		return userSetMaxRecSize;
	}

	public Map<String, List<Long>> getEanOprMap() {
		return eanOprMap;
	}

	public Map<Long, AcademicItemData> getAcademicItemDataMap() {
		return academicItemDataMap;
	}

	public long geteRecsOffset() {
		return eRecsOffset;
	}

	public void seteRecsOffset(long eRecsOffset) {
		this.eRecsOffset = eRecsOffset;
	}

	public void setRecSize(long recSize) {
		this.recSize = recSize;
	}

	public long getStartRec() {
		return startRec;
	}

	public long getEndRec() {
		return endRec;
	}

	public Map<String, String> getCisEbpCatpartnerDescMap() {
		return cisEbpCatpartnerDescMap;
	}

	public void setCisEbpCatpartnerDescMap(Map<String, String> cisEbpCatpartnerDescMap) {
		this.cisEbpCatpartnerDescMap = cisEbpCatpartnerDescMap;
	}

	public String getTargetSwitchEan() {
		return targetSwitchEan;
	}

	public void setTargetSwitchEan(String targetSwitchEan) {
		this.targetSwitchEan = targetSwitchEan;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the powerSearch
	 */
	public boolean isPowerSearch() {
		return powerSearch;
	}

	/**
	 * @param powerSearch
	 *            the powerSearch to set
	 */
	public void setPowerSearch(boolean powerSearch) {
		this.powerSearch = powerSearch;
	}

	private final void productPriceFinder(SearchResultDataBean<Product> productDetail, CISUserLoginData cisUserLoginDataCopy, SolrDocument doc) {
		productDetail.setDispEbookPrice(null);

		try {
			// get the ebook details
			if (null != cisUserLoginDataCopy.getEbpDataObjectMap() && null != cisUserLoginDataCopy.getEbpDataObjectList()) {
				// check if there is a platform / license setting in the AcademicItemData
				// if exists then sort the EBPDataObject in cisUserLoginDataCopy on CisEbpCatpartner
				// that EBPDataObject object in the list of EBPDataObject in cisUserLoginDataCopy
				// should come on top - the one which is selected for this product
				if (null != productDetail.getSolrProduct().getCisEBPData() && !productDetail.getSolrProduct().getCisEBPData().isEmpty()) {
					EbookInfoCmprtrHelper ebookInfoCmprtr = new EbookInfoCmprtrHelper(cisUserLoginDataCopy, academicItemDataMap, productDetail.getSolrProduct());
					ebookInfoCmprtr.analyze(productDetail);
				}

			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		if (null == productDetail.getDispEbookPrice() || productDetail.getDispEbookPrice().isEmpty()) {
			// PRICE_*'s
			if (powerSearch && null != cisUserLoginData.getPowerSearchData("priceLimitCur")) {
				String priceFldName = SearchConstants.solr_cur_name.replace("?", cisUserLoginData.getPowerSearchData("priceLimitCur").toString());
				Object obj = doc.getFirstValue(priceFldName);
				productDetail.setPowerSearchCurrency(cisUserLoginData.getPowerSearchData("priceLimitCur").toString());
				productDetail.setProdMltiCurPrices(cisUserLoginData.getPowerSearchData("priceLimitCur").toString(), SearchConstants.numberFormatter.format(Double.valueOf(obj.toString())));
			} else {
				try {

					for (String cur : cisUserLoginData.getCurrencyPreferences()) {

						try {
							String priceFldName = SearchConstants.solr_cur_name.replace("?", cur.trim());
							Object obj = doc.get(priceFldName);
							if (obj instanceof Collection<?>) {
								obj = doc.getFirstValue(priceFldName);
							}
							if (null != obj) {
								if (null == productDetail.getAvailPriceKey() || productDetail.getAvailPriceKey().isEmpty()) {
									productDetail.setAvailPriceKey(cur.trim());
								}
								productDetail.setProdMltiCurPrices(cur.trim(), SearchConstants.numberFormatter.format(Double.valueOf(obj.toString())));

								break;
							}
						} catch (Exception e) {
							continue;
						}

					}// fors
				} catch (Exception e) {
				}
			}
		}

	}

	private final void productDateFinder(SearchResultDataBean<Product> productDetail, SolrDocument doc) {

		// get the library default date from session
		// CisCustGroup cisCustGroup = (CisCustGroup) request.getSession().getAttribute("sessionCisCustGroup");
		SimpleDateFormat dateFormat = null;
		SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyyMMdd");

		// if (null != cisCustGroup && (null != cisCustGroup.getDateFormat() &&
		// !cisCustGroup.getDateFormat().isEmpty())) {
		// dateFormat = new SimpleDateFormat(cisCustGroup.getDateFormat());
		// }

		// pub date
		try {
			Object[] divCodeArr = doc.getFieldValues("COUTTS_DIVISION_CODE").toArray();
			Map<Object, Object> pubDateMap = new HashMap<Object, Object>();

			if (null != divCodeArr) {
				Object[] pubDateArr = doc.getFieldValues("COUTTS_DIVISION_PUB_DATE").toArray();
				if (null != pubDateArr) {
					int ctr = 0;
					for (Object code : divCodeArr) {
						pubDateMap.put(code, pubDateArr[ctr++]);
					}

				}
			}

			try {
				if (null != pubDateMap.get(cisUserLoginData.getApprovalCenter())) {
					productDetail.setAcdmPubDate(dateFormat.format(defaultDateFormat.parse(pubDateMap.get(cisUserLoginData.getApprovalCenter()).toString())));
				}

			} catch (ParseException pe) {
				productDetail.setAcdmPubDate(null);
			}

		} catch (Exception e) {
			productDetail.setAcdmPubDate(null);
		}
	}

	// only uses this constructor when the result found is 0
	// and no result page is to be shown
	public Br8000ProdSolrTest(String userId) {

		thisUser = userId;

		totalNumERecs = 0;
		totRecOnPage = 0;
		startRec = 0;
		endRec = 0;

	}

}