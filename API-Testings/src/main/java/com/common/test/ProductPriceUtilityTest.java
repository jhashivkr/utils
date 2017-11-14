package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.administration.CISUserLoginData;
import ibg.common.AcdmTitlePriceObj;
import ibg.product.ebook.TitleEbookInfoAnalyzer;
import ibg.product.ebook.TitleUserEbookInfoCmprtr;
import ibg.product.information.EBPDataObject;
import ibg.product.information.ProductCisDetails;
import ibg.product.search.acdm.SearchConstants;
import ibg.product.search.acdm.SolrQueryAcdm;
import ibg.product.search.acdm.SolrQueryReuseService;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class ProductPriceUtilityTest {

	public static Map<String, AcdmTitlePriceObj> priceEanMap(Set<String> searchEansSet, CISUserLoginData cisUserLoginData) {

		try {
			Map<String, AcdmTitlePriceObj> eanPriceMap = new LinkedHashMap<String, AcdmTitlePriceObj>(searchEansSet.size());

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator.getBean(BeanName.SOLR_QRY));
			qry.setFields();
			qry.setPriceFields();

			List<String> searchEansList = new LinkedList<String>(searchEansSet);
			Set<String> searchEansTmp = new LinkedHashSet<String>();

			int start = 0;
			int end = 1000;
			int limit = 0;

			if (searchEansList.size() < end) {
				end = searchEansList.size();
				limit = 1;
			} else {
				limit = Math.abs(searchEansList.size() / end);
				limit = (searchEansList.size() > (limit * end)) ? limit + 1 : limit;
			}

			for (; limit > 0;) {
				searchEansTmp.addAll(searchEansList.subList(start, end));

				QueryResponse response = SolrQueryReuseService.getRearrangedEansList(searchEansTmp, qry);
				SolrDocumentList result = response.getResults();
				for (SolrDocument doc : result) {
					try {
						// price = 0;
						AcdmTitlePriceObj price = null;

						Collection<Object> cisEbpCatpartner = doc.getFieldValues("CIS_EBP_CATPARTNER");
						if (null != cisEbpCatpartner && cisEbpCatpartner.size() > 0) {

							ProductCisDetails.EBPBuilder bldr = new ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(doc.getFieldValues("CIS_EBP_CURRENCY"))
									.cisEbpDownloaddetails(doc.getFieldValues("CIS_EBP_DOWNLOADDETAILS")).cisEbpEbscocategory(doc.getFieldValues("CIS_EBP_EBSCOCATEGORY"))
									.cisEbpEbscolevel(doc.getFieldValues("CIS_EBP_EBSCOLEVEL")).cisEbpLicence(doc.getFieldValues("CIS_EBP_LICENSE")).cisEbpPrice(doc.getFieldValues("CIS_EBP_PRICE"));

							TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>> ebpComp = new TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>>();
							ebpComp.setUserLoginData(cisUserLoginData);
							ebpComp.setCisEBPMap(bldr.getCisEBPDataMap());

							ebpComp.analyzeEbpData();
							if (null != ebpComp.getBestMatchEbook()) {
								
								price = new AcdmTitlePriceObj();
								price.setPrice(Double.parseDouble(ebpComp.getBestMatchEbook().getCisEbpPrice()));
								price.setCur(ebpComp.getBestMatchEbook().getCisEbpCurrency());

							}
						}

						if (null == price) {
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
										price = new AcdmTitlePriceObj();
										price.setPrice(Double.valueOf(obj.toString()));
										price.setCur(cur.trim());
										break;
									}
								} catch (Exception e) {
									continue;
								}

							}// for
						}// if

						eanPriceMap.put(doc.get("EAN").toString(), price);
					} catch (Exception e) {
						eanPriceMap.put(doc.get("EAN").toString(), null);
						e.printStackTrace();
					}
				}// for (SolrDocument doc : result)

				limit--;
				start = end + 1;
				end += 1000;
				end = (end > searchEansList.size()) ? searchEansList.size() : end;
				searchEansTmp.clear();
			}// for

			return eanPriceMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Map<String, String> priceEanMapTest(Set<String> searchEansSet, CISUserLoginData cisUserLoginData) {

		try {
			Map<String, String> eanPriceMap = new LinkedHashMap<String, String>(searchEansSet.size());

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator.getBean(BeanName.SOLR_QRY));
			// qry.setFields();
			// qry.setPriceFields();

			qry.setFields("EAN", "CIS_EBP_CATPARTNER", "CIS_EBP_CURRENCY", "CIS_EBP_DOWNLOADABLE", "CIS_EBP_DOWNLOADDETAILS", "CIS_EBP_EBSCOCATEGORY", "CIS_EBP_EBSCOLEVEL", "CIS_EBP_LICENSE",
					"CIS_EBP_MILACCESS", "CIS_EBP_PRICE", "CIS_EBP_TITLEID", "PRICE_*_CL");

			List<String> searchEansList = new LinkedList<String>(searchEansSet);
			Set<String> searchEansTmp = new LinkedHashSet<String>();

			int start = 0;
			int end = 1000;
			int limit = 0;

			if (searchEansList.size() < end) {
				end = searchEansList.size();
				limit = 1;
			} else {
				limit = Math.abs(searchEansList.size() / end);
				limit = (searchEansList.size() > (limit * end)) ? limit + 1 : limit;
			}

			for (; limit > 0;) {
				searchEansTmp.addAll(searchEansList.subList(start, end));

				QueryResponse response = SolrQueryReuseService.getRearrangedEansList(searchEansTmp, qry);
				SolrDocumentList result = response.getResults();
				for (SolrDocument doc : result) {
					try {
						// price = 0;
						String data = null;

						Collection<Object> cisEbpCatpartner = doc.getFieldValues("CIS_EBP_CATPARTNER");
						if (null != cisEbpCatpartner && cisEbpCatpartner.size() > 0) {

							// ProductCisDetails.EBPBuilder bldr = new
							// ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(doc.getFieldValues("CIS_EBP_CURRENCY"))
							// .cisEbpDownloadable(doc.getFieldValues("CIS_EBP_DOWNLOADABLE")).cisEbpDownloaddetails(doc.getFieldValues("CIS_EBP_DOWNLOADDETAILS"))
							// .cisEbpEbscocategory(doc.getFieldValues("CIS_EBP_EBSCOCATEGORY")).cisEbpEbscolevel(doc.getFieldValues("CIS_EBP_EBSCOLEVEL"))
							// .cisEbpLicence(doc.getFieldValues("CIS_EBP_LICENSE")).cisEbpPrice(doc.getFieldValues("CIS_EBP_PRICE"));

							ProductCisDetails.EBPBuilder bldr = new ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(doc.getFieldValues("CIS_EBP_CURRENCY"))
									.cisEbpDownloadable(doc.getFieldValues("CIS_EBP_DOWNLOADABLE")).cisEbpDownloaddetails(doc.getFieldValues("CIS_EBP_DOWNLOADDETAILS"))
									.cisEbpEbscocategory(doc.getFieldValues("CIS_EBP_EBSCOCATEGORY")).cisEbpEbscolevel(doc.getFieldValues("CIS_EBP_EBSCOLEVEL"))
									.cisEbpLicence(doc.getFieldValues("CIS_EBP_LICENSE")).cisEbpPrice(doc.getFieldValues("CIS_EBP_PRICE")).cisEbpMilaccess(doc.getFieldValues("CIS_EBP_MILACCESS"))
									.cisEbpTitleid(doc.getFieldValues("CIS_EBP_TITLEID"));

							TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>> ebpComp = new TitleEbookInfoAnalyzer<CISUserLoginData, List<EBPDataObject>>();
							ebpComp.setUserLoginData(cisUserLoginData);
							ebpComp.setCisEBPMap(bldr.getCisEBPDataMap());

							ebpComp.analyzeEbpData();
							if (null != ebpComp.getBestMatchEbook()) {
								//System.out.println("best match: " + ebpComp.getBestMatchEbook());
								data = ebpComp.getBestMatchEbook().getCisEbpPrice() + ", " + ebpComp.getBestMatchEbook().getCisEbpCurrency() + ", " + ebpComp.getBestMatchEbook().getCisEbpCatpartner()
										+ ", " + ebpComp.getBestMatchEbook().getCisEbpCatpartnerDesc() + ", " + ebpComp.getBestMatchEbook().getCisEbpLicence() + ", "
										+ ebpComp.getBestMatchEbook().getCisEbpLicenceDesc() + ", " + ebpComp.getBestMatchEbook().getCisEbpDisplayText();

							}
							
						}

						if (null == data) {
							// PRICE_*'s
							for (String cur : cisUserLoginData.getCurrencyPreferences()) {

								try {
									String priceFldName = SearchConstants.solr_cur_name.replace("?", cur.trim());
									Object obj = doc.get(priceFldName);
									if (obj instanceof Collection<?>) {
										obj = doc.getFirstValue(priceFldName);
									}
									if (null != obj) {
										// the data
										data = obj.toString() + ", " + cur.trim();
										break;
									}
								} catch (Exception e) {
									continue;
								}

							}// for
						}// if

						eanPriceMap.put(doc.get("EAN").toString(), data);
					} catch (Exception e) {
						eanPriceMap.put(doc.get("EAN").toString(), null);
						e.printStackTrace();
					}
				}// for (SolrDocument doc : result)

				limit--;
				start = end + 1;
				end += 1000;
				end = (end > searchEansList.size()) ? searchEansList.size() : end;
				searchEansTmp.clear();
			}// for

			return eanPriceMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static Map<String, String> ebookPriceEanMapTest(Set<String> searchEansSet, CISUserLoginData cisUserLoginData) {

		try {
			Map<String, String> eanPriceMap = new LinkedHashMap<String, String>(searchEansSet.size());

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator.getBean(BeanName.SOLR_QRY));
			qry.setFields("EAN", "CIS_EBP_CATPARTNER", "CIS_EBP_CURRENCY", "CIS_EBP_DOWNLOADABLE", "CIS_EBP_DOWNLOADDETAILS", "CIS_EBP_EBSCOCATEGORY", "CIS_EBP_EBSCOLEVEL", "CIS_EBP_LICENSE",
					"CIS_EBP_MILACCESS", "CIS_EBP_PRICE", "CIS_EBP_TITLEID", "PRICE_*_CL");

			List<String> searchEansList = new LinkedList<String>(searchEansSet);
			Set<String> searchEansTmp = new LinkedHashSet<String>();

			int start = 0;
			int end = 1000;
			int limit = 0;

			if (searchEansList.size() < end) {
				end = searchEansList.size();
				limit = 1;
			} else {
				limit = Math.abs(searchEansList.size() / end);
				limit = (searchEansList.size() > (limit * end)) ? limit + 1 : limit;
			}

			for (; limit > 0;) {
				searchEansTmp.addAll(searchEansList.subList(start, end));

				QueryResponse response = SolrQueryReuseService.getRearrangedEansList(searchEansTmp, qry);
				SolrDocumentList result = response.getResults();
				
				for (SolrDocument doc : result) {
					try {
						// price = 0;
						String data = null;

						Collection<Object> cisEbpCatpartner = doc.getFieldValues("CIS_EBP_CATPARTNER");
						if (null != cisEbpCatpartner && cisEbpCatpartner.size() > 0) {

							ProductCisDetails.EBPBuilder bldr = new ProductCisDetails.EBPBuilder(cisEbpCatpartner).cisEbpCurrency(doc.getFieldValues("CIS_EBP_CURRENCY"))
									.cisEbpDownloadable(doc.getFieldValues("CIS_EBP_DOWNLOADABLE")).cisEbpDownloaddetails(doc.getFieldValues("CIS_EBP_DOWNLOADDETAILS"))
									.cisEbpEbscocategory(doc.getFieldValues("CIS_EBP_EBSCOCATEGORY")).cisEbpEbscolevel(doc.getFieldValues("CIS_EBP_EBSCOLEVEL"))
									.cisEbpLicence(doc.getFieldValues("CIS_EBP_LICENSE")).cisEbpPrice(doc.getFieldValues("CIS_EBP_PRICE")).cisEbpMilaccess(doc.getFieldValues("CIS_EBP_MILACCESS"))
									.cisEbpTitleid(doc.getFieldValues("CIS_EBP_TITLEID"));

							TitleUserEbookInfoCmprtr<CISUserLoginData, List<EBPDataObject>> ebpComp = new TitleUserEbookInfoCmprtr<CISUserLoginData, List<EBPDataObject>>();

							ebpComp.setUserLoginData(cisUserLoginData);
							ebpComp.setCisEBPMap(bldr.getCisEBPDataMap());
							ebpComp.analyzeEbpData();

							// for (String platform : ebpComp.getOthersAvailableMap().keySet()) {
							// System.out.println(platform + ":" + ebpComp.getOthersAvailableMap().get(platform));
							// }

							if (null != ebpComp.getBestMatchEbook()) {
								System.out.println("best match: " + ebpComp.getBestMatchEbook());
								data = ebpComp.getBestMatchEbook().getCisEbpPrice() + ", " + ebpComp.getBestMatchEbook().getCisEbpCurrency() + ", " + ebpComp.getBestMatchEbook().getCisEbpCatpartner()
										+ ", " + ebpComp.getBestMatchEbook().getCisEbpCatpartnerDesc() + ", " + ebpComp.getBestMatchEbook().getCisEbpLicence() + ", "
										+ ebpComp.getBestMatchEbook().getCisEbpLicenceDesc() + ", " + ebpComp.getBestMatchEbook().getCisEbpDisplayText();

							}
							if (null != ebpComp.getOthersAvailable() && !ebpComp.getOthersAvailable().isEmpty()) {
								System.out.println("others Available for: " + doc.get("EAN").toString());

								for (EBPDataObject obj : ebpComp.getOthersAvailable()) {
									System.out.println(obj);
								}
							}

							
						}

						if (null == data) {
							// PRICE_*'s
							for (String cur : cisUserLoginData.getCurrencyPreferences()) {

								try {
									String priceFldName = SearchConstants.solr_cur_name.replace("?", cur.trim());
									Object obj = doc.get(priceFldName);
									if (obj instanceof Collection<?>) {
										obj = doc.getFirstValue(priceFldName);
									}
									if (null != obj) {
										// the data
										data = obj.toString() + ", " + cur.trim();
										break;
									}
								} catch (Exception e) {
									continue;
								}

							}// for
						}// if

						eanPriceMap.put(doc.get("EAN").toString(), data);
					} catch (Exception e) {
						eanPriceMap.put(doc.get("EAN").toString(), null);
						e.printStackTrace();
					}
				}// for (SolrDocument doc : result)

				limit--;
				start = end + 1;
				end += 1000;
				end = (end > searchEansList.size()) ? searchEansList.size() : end;
				searchEansTmp.clear();
			}// for

			return eanPriceMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
