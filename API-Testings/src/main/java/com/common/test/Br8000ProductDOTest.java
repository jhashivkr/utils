package com.common.test;

import ibg.academics.dto.AcademicAlibrisItem;
import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListType;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.academics.service.SolrUtilityService;
import ibg.administration.CISUserLoginData;
import ibg.browserow.dto.AcademicItemData;
import ibg.common.MarketSegment;
import ibg.product.information.EBPDataObject;
import ibg.product.information.Product;
import ibg.product.information.service.ProductInformationException;
import ibg.product.information.service.ProductInformationServiceImpl;
import ibg.product.search.EbookInfoCmprtrHelper;
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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;

import com.endeca.navigation.ERec;
import com.endeca.navigation.Navigation;

public final class Br8000ProductDOTest {

	private HttpServletRequest request;

	private long listId; // list id of the list
	private String listTypeName;

	private List<SearchResultDataBean<Product>> searchResult;

	private List<ERec> recs;

	private ProductInformationServiceImpl solrSrv = new ProductInformationServiceImpl();
	private StringBuilder languageListBuilder = new StringBuilder();
	private StringBuilder statusBuilder = new StringBuilder();

	private Navigation nav;

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

	public Br8000ProductDOTest(Map<String, List<Long>> eanOprMap, CISUserLoginData cisUserLoginData, Navigation nav, String userId, Map<Long, AcademicItemData> academicItemDataMap,
			AcademicList academicList) {

		this.eanOprMap = eanOprMap;
		this.cisUserLoginData = cisUserLoginData;
		thisUser = userId;
		this.listType = academicList.getListType();
		this.listId = academicList.getListId();

		this.academicItemDataMap = academicItemDataMap;
		this.nav = nav;

		solrSrv.setMarketSegment(MarketSegment.ACDM);
		// powerSearch = "power".equalsIgnoreCase(request.getParameter("searchType"));

		try {

			recs = (List<ERec>) nav.getERecs();
			// System.out.println("recs: " + recs.size());
			searchResult = new LinkedList<SearchResultDataBean<Product>>(); // the size should be the users page size
			solrFetchSearchEans = new LinkedHashSet<String>();

			buildSolrProduct();

			eRecsOffset = nav.getERecsOffset() + 1;
			recSize = nav.getERecsOffset() + recs.size();

		} catch (Exception e) {
			e.printStackTrace();
			recs = null;
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
		// remove those ebpdataobject from cisuserlogindata which is not in user selection
		if (null != ebookOption && !ebookOption.isEmpty()) {
			Boolean viewThisEbook = false;

			for (String key : ebookOption.keySet()) {
				try {
					viewThisEbook = Boolean.valueOf(ebookOption.get(key));
					if (!viewThisEbook && cisUserLoginData.getEbpDataObjectMap().containsKey(key)) {
						cisUserLoginData.getEbpDataObjectMap().remove(key);
					} else if (!cisUserLoginData.getEbpDataObjectMap().containsKey(key)) {
						cisUserLoginData.getEbpDataObjectMap().remove(key);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					continue;
				}
			}
		}

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
			for (ERec rec : recs) {

				languageListBuilder.delete(0, languageListBuilder.length());
				statusBuilder.delete(0, statusBuilder.length());

				@SuppressWarnings("unchecked")
				// Map<String, String> switchEanMap = (Map<String, String>)
				// request.getSession().getAttribute("switch_ean_map");
				Map<String, String> switchEanMap = new HashMap<String, String>();

				String ean = (String) rec.getSolrDocument().get("EAN");
				solrProduct = new Product();
				switchEan = false;

				if (null != switchEanMap && switchEanMap.containsKey(ean) && !AcademicListType.SEARCH_ORDERS.getListTypeId().equals(listType)) {
					SolrUtilityService solrService = (SolrUtilityService) AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);

					String tragetEan = getMapValue(switchEanMap, ean);

					switchEanMap.put(ean, tragetEan);
					// request.getSession().setAttribute("switch_ean_map", switchEanMap);
					solrService.setSearchEans(tragetEan, 1);
					solrProduct = solrService.fetchProductDetatilsFromSolr().get(0);
					switchEan = true;
					rec = solrService.fetchNewErec();

				} else {
					solrSrv.loadProductData(rec.getSolrDocument(), solrProduct);
				}
				
				SolrDocument doc = rec.getSolrDocument();
				SearchResultDataBean<Product> productDetail = new SearchResultDataBean<Product>(solrProduct);

				System.out.println("All the alternates: " + solrProduct.getAltEan());

				// get the alternate ean details
				if (null != solrProduct.getAltEan() && !solrProduct.getAltEan().isEmpty()) {
					List<String> invalidFormatCode = Arrays.asList(new String[] { "DA", "DM", "DZ", "EH", "EL", "IA", "OA", "OL", "PW", "UB", "U" });
					// get details of the alternate eans
					List<Object> altEans = new LinkedList<Object>(solrProduct.getAltEan());
					SolrUtilityService solrService = (SolrUtilityService) AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);
					solrService.setSearchEans(StringUtils.join(altEans, ','), altEans.size());
					solrService.setCisUserLoginData(cisUserLoginDataCopy);

					if (switchEan) {
						solrService.setSearchFields(null);
					} else {
						solrService.setSearchFields(SolrSearchFieldsService.getAltEansBrowseRowReqFields());
					}

					// get the alternates
					List<Product> altEansList = new ArrayList<Product>();
					for (Product altEanProducts : solrService.fetchProductDetatilsFromSolr()) {

						if (!invalidFormatCode.contains(altEanProducts.getCisFormatCode())) {
							// analyze the ebook eligibility
							if (null != altEanProducts.getCisEBPData() && null != cisUserLoginDataCopy.getEbpDataObjectMap()) {

								//System.out.println("altEanProducts.getCisEBPData(): " + altEanProducts.getEan() + " | " + altEanProducts.getCisEBPDataMap());

								EbookInfoCmprtrHelper ebookInfoCmprtr = new EbookInfoCmprtrHelper(cisUserLoginDataCopy, academicItemDataMap, solrProduct);
								ebookInfoCmprtr.analyzeForAlternate(altEanProducts);

								// EbookInfoCmprtrHelper ebookInfoCmprtr = new
								// EbookInfoCmprtrHelper(cisUserLoginDataCopy, academicItemDataMap, solrProduct);
								// ebookInfoCmprtr.analyzeAlternate(altEanProducts);

							}
						}

						try {
							if (null != altEanProducts.getCisEBPData() && !altEanProducts.getCisEBPData().isEmpty()) {
								//if (null != ebookOption) {
									for (EBPDataObject ebp : altEanProducts.getCisEBPData()) {
										// if (null != ebp.getCisEbpCatpartner() && null !=
										// ebookOption.get(ebp.getCisEbpCatpartner()) &&
										// ebookOption.get(ebp.getCisEbpCatpartner()).booleanValue()) {
										if (null != ebp.getCisEbpCatpartner() && !ebp.getCisEbpCatpartner().isEmpty()) {
											System.out.println("ebook alternate: " + altEanProducts.getEan() + ", " + ebp.getCisEbpCatpartner());
											altEansList.add(altEanProducts);
											
											StringBuilder reqParam = new StringBuilder();
											reqParam.append("platform=").append(ebp.getCisEbpCatpartner()).append("&titleId=").append(ebp.getCisEbpTitleid());
											productDetail.setAltEbookSeeInsideDetails(reqParam.toString());
											break;
										}
									}// for
								//}// if

							}// if
							else {
								System.out.println("non ebook alternate: " + altEanProducts.getEan() + ", " + altEanProducts.getCisFormatDesc());
								altEansList.add(altEanProducts);
							}
						} catch (Exception e) {
							e.printStackTrace();

						}

					}// for
					if (null != altEansList && !altEansList.isEmpty()) {
						System.out.println("formats tab show");
						solrProduct.setShowFormatTab(true);
					}

					solrProduct.setRelatedProducts(altEansList);
					solrProduct.setAltProducts(altEansList);
				}// alternate eans

				
				// get the products price
				// Ebook analyzing is done in the same method
				productPriceFinder(productDetail, cisUserLoginDataCopy, doc);

				productDateFinder(productDetail, doc);

				productDetail.setProdRowNoOnPage(recsCtr++);
				// solrFetchSearchEans.add(solrProduct.getEan());

				if (null != eanOprMap && eanOprMap.containsKey(solrProduct.getEan())) {

					// if size > 1 it means this ean exists multiple times in the
					// parent list
					if (eanOprMap.get(solrProduct.getEan()).size() > 1) {
						int ctr = 0;
						// create copies
						for (Long oprId : eanOprMap.get(solrProduct.getEan())) {
							SearchResultDataBean<Product> productDetailCopy = SearchResultDataBean.newInstance(productDetail);
							productDetailCopy.setProdRowNoOnPage(productDetailCopy.getProdRowNoOnPage() + ctr);
							recsCtr = productDetailCopy.getProdRowNoOnPage() + ctr;
							ctr++;
							productDetailCopy.setOprId(oprId);
							if (academicItemDataMap.containsKey(oprId) || academicItemDataMap.containsKey(productDetailCopy.getSolrProduct().getEan())) {
								AcademicItemData academicItemData = academicItemDataMap.get(oprId);
								productDetailCopy.setAcademicData(academicItemData);

								// replace the licence and platform with the information
								// available (if) in the order info details of this product
								try {
									if (null != productDetailCopy.getAcademicData().getLicense() && !productDetailCopy.getAcademicData().getLicense().isEmpty()) {
										productDetailCopy.setDispEbookLicense(productDetailCopy.getAcademicData().getLicense());
									}
								} catch (Exception e) {
									continue;
								}
							}
							// For Alibris List
							if (null != alibrisItemDataMap && alibrisItemDataMap.containsKey(oprId)) {
								AcademicAlibrisItem alibrisItem = alibrisItemDataMap.get(oprId);
								if (alibrisItem.getPrice() != null) {
									productDetailCopy.getAcademicData().setAlibrisCopyPrice(alibrisItem.getPrice().floatValue());
								} else {
									productDetailCopy.getAcademicData().setAlibrisCopyPrice(0F);
								}
							}

							searchResult.add(productDetailCopy);
						}// for
					}// if (eanOprMap.get(solrProduct.getEan()).size() > 1)
					else {
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
						if (academicItemDataMap.containsKey(oprId)) {
							AcademicItemData academicItemData = academicItemDataMap.get(oprId);
							productDetail.setAcademicData(academicItemData);

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
							AcademicAlibrisItem alibrisItem = alibrisItemDataMap.get(oprId);
							if (alibrisItem.getPrice() != null) {
								productDetail.getSolrProduct().setLibraryPrice(alibrisItem.getPrice().floatValue());
							} else {
								productDetail.getSolrProduct().setLibraryPrice(0F);
							}
						}

						searchResult.add(productDetail);
					}// else
				}// if (null != eanOprMap && eanOprMap.containsKey(solrProduct.getEan()))
				else {

					searchResult.add(productDetail);
					// }
				}// else

				solrProduct = null;

			}// for (ERec rec : recs)

		} catch (ProductInformationException e) {
			// e.printStackTrace();
		}
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
	public Br8000ProductDOTest(String userId) {

		thisUser = userId;

		totalNumERecs = 0;
		totRecOnPage = 0;
		startRec = 0;
		endRec = 0;

	}

}