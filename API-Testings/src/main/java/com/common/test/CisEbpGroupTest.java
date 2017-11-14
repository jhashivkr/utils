package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.administration.CISUserLoginData;
import ibg.browserow.dto.AcademicItemData;
import ibg.lib.activity.browserowp.db.response.ObjectMapperWraper;
import ibg.product.ebook.TitleEbookInfoAnalyzer;
import ibg.product.ebook.TitleUserEbookInfoCmprtr;
import ibg.product.information.EBPDataObject;
import ibg.product.information.ProductCisDetails;
import ibg.product.search.acdm.SearchConstants;
import ibg.product.search.acdm.SolrQueryAcdm;
import ibg.product.search.acdm.SolrQueryReuseService;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class CisEbpGroupTest {
	
	private String availableEbpDataObjectJson;

	public Map<String, List<Long>> priceSortedEanSRFromSolr(CISUserLoginData cisUserLoginData, String searchTerm, int rsltSrchMaxPageCnt) {

		try {
			Map<Long, AcademicItemData> academicItemDataMap = new LinkedHashMap<Long, AcademicItemData>();
			Map<String, List<Long>> eanOprMap = null;

			// get all the eans from the list
			Set<String> searchEansSet = new LinkedHashSet<String>();

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator.getBean(BeanName.SOLR_QRY));
			qry.setFields();
			qry.setPriceFields();

			// check for defType and qt in the searchTerm
			// if(searchTerm.contains("&defType=")){
			if (searchTerm.contains("&")) {
				String[] search = searchTerm.split("\\&");
				qry.set("q", search[0]);

				search = search[1].split("\\=");
				if (search[0].contains("defType")) {
					qry.set("defType", search[1]);
				} else if (search[0].contains("qt")) {
					qry.set("qt", search[1]);
				}

			} else {
				qry.set("q", searchTerm);
			}

			qry.setRows(rsltSrchMaxPageCnt);

			SortedSet<Map.Entry<String, Double>> sortedset = new TreeSet<Map.Entry<String, Double>>(new Comparator<Map.Entry<String, Double>>() {
				public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {

					if (e1.getValue().doubleValue() <= 0) {
						return 1;
					}

					else if (e2.getValue().doubleValue() <= 0) {
						return -1;
					}

					else if (e1.getValue().doubleValue() > e2.getValue().doubleValue()) {
						return 1;
					}

					else if (e1.getValue().doubleValue() < e2.getValue().doubleValue()) {
						return -1;
					} else if (e1.getValue().doubleValue() == e2.getValue().doubleValue()) {
						return 1;
					}

					return 0;

				}
			});

			SortedMap<String, Double> eanMap = new TreeMap<String, Double>();

			searchEansSet.clear();

			QueryResponse response = SolrQueryReuseService.getSearchEansSet(qry);
			SolrDocumentList result = response.getResults();
			for (SolrDocument doc : result) {
				try {
					Double price = Double.valueOf(0);

					Collection<Object> cisEbpCatpartner = doc.getFieldValues("CIS_EBP_CATPARTNER");
					if (null != cisEbpCatpartner && !cisEbpCatpartner.isEmpty()) {

						ProductCisDetails.EBPBuilder bldr = new ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(doc.getFieldValues("CIS_EBP_CURRENCY"))
								.cisEbpDownloadable(doc.getFieldValues("CIS_EBP_DOWNLOADABLE")).cisEbpDownloaddetails(doc.getFieldValues("CIS_EBP_DOWNLOADDETAILS"))
								.cisEbpEbscocategory(doc.getFieldValues("CIS_EBP_EBSCOCATEGORY")).cisEbpEbscolevel(doc.getFieldValues("CIS_EBP_EBSCOLEVEL"))
								.cisEbpLicence(doc.getFieldValues("CIS_EBP_LICENSE")).cisEbpMilaccess(doc.getFieldValues("CIS_EBP_MILACCESS")).cisEbpPrice(doc.getFieldValues("CIS_EBP_PRICE"))
								.cisEbpTitleid(doc.getFieldValues("CIS_EBP_TITLEID"));

						TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>> ebpComp = new TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>>();
						ebpComp.setUserLoginData(cisUserLoginData);
						ebpComp.setCisEBPMap(bldr.getCisEBPDataMap());

						ebpComp.analyzeEbpData();

						if (null != ebpComp.getBestMatchEbook().getCisEbpPrice() && !ebpComp.getBestMatchEbook().getCisEbpPrice().isEmpty()) {
							if (!ebpComp.getBestMatchEbook().getCisEbpCurrency().equalsIgnoreCase(cisUserLoginData.getFirstCurrencyValue())) {
								price = Math.rint(Double.parseDouble(ebpComp.getBestMatchEbook().getCisEbpPrice())
										* cisUserLoginData.getCurrencyExchangeMap().get(ebpComp.getBestMatchEbook().getCisEbpCurrency()));
							} else {
								price = Double.parseDouble(ebpComp.getBestMatchEbook().getCisEbpPrice());
							}
						}
					}

					if (price.doubleValue() <= 0) {
						// PRICE_*'s
						for (String cur : cisUserLoginData.getCurrencyPreferences()) {

							try {
								String priceFldName = SearchConstants.solr_cur_name.replace("?", cur.trim());
								Object obj = doc.get(priceFldName);
								if (obj instanceof Collection<?>) {
									obj = doc.getFirstValue(priceFldName);
								}
								if (null != obj) {
									// the price
									price = Double.valueOf(obj.toString());

									if (!cur.equalsIgnoreCase(cisUserLoginData.getFirstCurrencyValue())
											&& (null != cisUserLoginData.getCurrencyExchangeMap() && cisUserLoginData.getCurrencyExchangeMap().isEmpty())) {
										price = Math.rint(price * cisUserLoginData.getCurrencyExchangeMap().get(cur));
									}

									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}

						}// for
					}// if

					eanMap.put(doc.get("EAN").toString(), price);

				} catch (Exception e) {
					eanMap.put(doc.get("EAN").toString(), Double.valueOf(0));
					e.printStackTrace();
				}

			}// for
			sortedset.addAll(eanMap.entrySet());

			if (sortedset.size() > 0) {
				for (Map.Entry<String, Double> acdm : sortedset) {
					searchEansSet.add(acdm.getKey());
					// System.out.println(acdm.getKey() + ":" + acdm.getValue());
				}
			}

			if (null != academicItemDataMap) {
				eanOprMap = new LinkedHashMap<String, List<Long>>(searchEansSet.size());
				// put all the sortedSearchEans in the map
				for (String ean : searchEansSet) {
					eanOprMap.put(ean, new LinkedList<Long>());
				}

				for (Map.Entry<Long, AcademicItemData> academicItemDataEntry : academicItemDataMap.entrySet()) {

					if (null != academicItemDataEntry.getValue().getTitleId()) {
						if (eanOprMap.containsKey(academicItemDataEntry.getValue().getTitleId())) {
							eanOprMap.get(academicItemDataEntry.getValue().getTitleId()).add(academicItemDataEntry.getValue().getOprId());
						}

					}// if
				}

			}

			return eanOprMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public void getCisEbpFromSolr(CISUserLoginData cisUserLoginData, String searchTerm, int rsltSrchMaxPageCnt) {

		try {
			Map<Long, AcademicItemData> academicItemDataMap = new LinkedHashMap<Long, AcademicItemData>();
			Map<String, List<Long>> eanOprMap = null;

			// get all the eans from the list
			Set<String> searchEansSet = new LinkedHashSet<String>();

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator.getBean(BeanName.SOLR_QRY));
			qry.setFields();
			qry.setPriceFields();

			// check for defType and qt in the searchTerm
			if (searchTerm.contains("&")) {
				String[] search = searchTerm.split("\\&");
				qry.set("q", search[0]);

				search = search[1].split("\\=");
				if (search[0].contains("defType")) {
					qry.set("defType", search[1]);
				} else if (search[0].contains("qt")) {
					qry.set("qt", search[1]);
				}

			} else {
				qry.set("q", searchTerm);
			}

			qry.setRows(rsltSrchMaxPageCnt);

			Map<String, String> eanMap = new HashMap<String, String>();

			searchEansSet.clear();

			QueryResponse response = SolrQueryReuseService.getSearchEansSet(qry);
			SolrDocumentList result = response.getResults();
			for (SolrDocument doc : result) {
				try {
					Double price = Double.valueOf(0);
					String whichPrice = "";

					Collection<Object> cisEbpCatpartner = doc.getFieldValues("CIS_EBP_CATPARTNER");
					if (null != cisEbpCatpartner && !cisEbpCatpartner.isEmpty()) {

						ProductCisDetails.EBPBuilder bldr = new ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(doc.getFieldValues("CIS_EBP_CURRENCY"))
								.cisEbpDownloadable(doc.getFieldValues("CIS_EBP_DOWNLOADABLE")).cisEbpDownloaddetails(doc.getFieldValues("CIS_EBP_DOWNLOADDETAILS"))
								.cisEbpEbscocategory(doc.getFieldValues("CIS_EBP_EBSCOCATEGORY")).cisEbpEbscolevel(doc.getFieldValues("CIS_EBP_EBSCOLEVEL"))
								.cisEbpLicence(doc.getFieldValues("CIS_EBP_LICENSE")).cisEbpMilaccess(doc.getFieldValues("CIS_EBP_MILACCESS")).cisEbpPrice(doc.getFieldValues("CIS_EBP_PRICE"))
								.cisEbpTitleid(doc.getFieldValues("CIS_EBP_TITLEID"));

						// TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>> ebpComp = new
						// TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>>();
						// ebpComp.setUserLoginData(cisUserLoginData);
						// ebpComp.setCisEBPMap(bldr.getCisEBPDataMap());

						TitleUserEbookInfoCmprtr<CISUserLoginData, List<EBPDataObject>> ebpComp = new TitleUserEbookInfoCmprtr<CISUserLoginData, List<EBPDataObject>>();

						try {
							// get the ebook details
							if (null != cisUserLoginData.getEbpDataObjectMap() && null != cisUserLoginData.getEbpDataObjectList()) {
								if (null != bldr.getCisEBPDataMap() && !bldr.getCisEBPDataMap().isEmpty()) {
									ebpComp.setUserLoginData(cisUserLoginData);
									ebpComp.setCisEBPMap(bldr.getCisEBPDataMap());
									ebpComp.analyzeEbpData();
								}

							}
						} catch (Exception e) {
							// e.printStackTrace();
						}

						if (null != ebpComp.getBestMatchEbook()) {
							if (null != ebpComp.getBestMatchEbook().getCisEbpCatpartner() && !ebpComp.getBestMatchEbook().getCisEbpCatpartner().isEmpty()) {
								
								System.out.println("EAN: " + doc.get("EAN").toString());
								
								System.out.println("display text: " + ebpComp.getBestMatchEbook().getCisEbpDisplayText());
								System.out.println("cat partner: " + ebpComp.getBestMatchEbook().getCisEbpCatpartner());

								System.out.println("display currency: " + ebpComp.getBestMatchEbook().getCisEbpCurrency());
								System.out.println("display price: " + ebpComp.getBestMatchEbook().getCisEbpPrice());
								System.out.println("display license: " + ebpComp.getBestMatchEbook().getCisEbpLicenceDesc());
								
								if(null != ebpComp.getOthersAvailable() && !ebpComp.getOthersAvailable().isEmpty()){
									int ctr = 1;
									for(EBPDataObject othrObj: ebpComp.getOthersAvailable()){
										System.out.println("others available - " + ctr++ + ": " + othrObj);
									}
									
									//add the best match to the other available list
									Set<EBPDataObject> allMatchSet = ebpComp.getOthersAvailable();
									allMatchSet.add(ebpComp.getBestMatchEbook());
									
									System.out.println("json rep: " + convertEBPDataObjectToJson(allMatchSet));
								}
								
								System.out.println("------------------------------");
							}
						}

						if (null != ebpComp.getBestMatchEbook().getCisEbpPrice() && !ebpComp.getBestMatchEbook().getCisEbpPrice().isEmpty()) {
							if ((!ebpComp.getBestMatchEbook().getCisEbpCurrency().equalsIgnoreCase(cisUserLoginData.getFirstCurrencyValue()))
									&& (null != cisUserLoginData.getCurrencyExchangeMap() && cisUserLoginData.getCurrencyExchangeMap().isEmpty())) {

								price = Math.rint(Double.parseDouble(ebpComp.getBestMatchEbook().getCisEbpPrice())
										* cisUserLoginData.getCurrencyExchangeMap().get(ebpComp.getBestMatchEbook().getCisEbpCurrency()));
								whichPrice = "Ebook-price (" + ebpComp.getBestMatchEbook().getCisEbpCurrency() + "): ";

							} else {
								price = Double.parseDouble(ebpComp.getBestMatchEbook().getCisEbpPrice());
								whichPrice = "Ebook-price (" + ebpComp.getBestMatchEbook().getCisEbpCurrency() + "): ";
							}
						}
					}// if (null != cisEbpCatpartner && !cisEbpCatpartner.isEmpty())

					if (price.doubleValue() <= 0) {
						// PRICE_*'s
						for (String cur : cisUserLoginData.getCurrencyPreferences()) {

							try {
								String priceFldName = SearchConstants.solr_cur_name.replace("?", cur.trim());
								Object obj = doc.get(priceFldName);
								if (obj instanceof Collection<?>) {
									obj = doc.getFirstValue(priceFldName);
								}
								if (null != obj) {
									// the price
									price = Double.valueOf(obj.toString());
									whichPrice = "Non Ebook-price (" + cur + "): ";

									if (!cur.equalsIgnoreCase(cisUserLoginData.getFirstCurrencyValue())
											&& (null != cisUserLoginData.getCurrencyExchangeMap() && cisUserLoginData.getCurrencyExchangeMap().isEmpty())) {
										price = Math.rint(price * cisUserLoginData.getCurrencyExchangeMap().get(cur));
									}

									break;
								}
							} catch (Exception e) {
								e.printStackTrace();
								continue;
							}

						}// for
					}// if

					whichPrice += price;
					eanMap.put(doc.get("EAN").toString(), whichPrice);

				} catch (Exception e) {
					eanMap.put(doc.get("EAN").toString(), "No Price");
					e.printStackTrace();
				}

			}// for

			for (String ean : eanMap.keySet()) {
				System.out.println(ean + ", " + eanMap.get(ean));
			}
			
			//UserEbookDispOptions userEbookDispOptions = new UserEbookDispOptions();

			//Map<String, Boolean> ebookOption = userEbookDispOptions.getUserEbookDispOptions("ACDM", "KATHYB");
			//Map<String, String> cisEbpCatpartnerDescMap = new HashMap<String, String>(0);

			//if (null != cisUserLoginData.getEbpDataObjectMap()) {
			//	cisEbpCatpartnerDescMap.putAll(userEbookDispOptions.fetchEbookCatPartnerDes(cisUserLoginData.getEbpDataObjectMap()));
			//}

			//System.out.println("ebookOption: " + ebookOption);
			//System.out.println("cisEbpCatpartnerDescMap: " + cisEbpCatpartnerDescMap);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the availableEblJson
	 */
	private String convertEBPDataObjectToJson(Set<EBPDataObject> objSet ) {
		try {
			availableEbpDataObjectJson = ((ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER)).write(objSet);
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return availableEbpDataObjectJson;
	}
	
	/**
	 * @return the availableEblJson
	 */
	private String convertEBPDataObjectToJson(EBPDataObject obj) {
		String json = null;
		try {
			json = ((ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER)).write(obj);
		} catch (JsonGenerationException e) {
		} catch (JsonMappingException e) {
		} catch (IOException e) {
		}
		return json;
	}

	
}
