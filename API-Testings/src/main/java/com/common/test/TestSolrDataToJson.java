package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.lib.activity.browserowp.db.response.ObjectMapperWraper;
import ibg.product.search.AcdmFilterHelper;
import ibg.product.search.acdm.SearchQueryResult;
import ibg.product.search.acdm.SolrQueryAcdm;
import ibg.product.search.acdm.SolrQueryReuseService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestSolrDataToJson {

	public static void testListPrepFlow(String... params) {

		String sortKey = null;
		String filterString = null;
		String sortSrcType = null;
		String searchTerm = null;

		if (null != params) {

			if (null != params[0]) {
				sortKey = params[0];
			}

			if (null != params[1]) {
				filterString = params[1];
			}
			if (null != params[2]) {
				sortSrcType = params[2];
			}

			if (null != params[3]) {
				searchTerm = params[3];
			}

		}
		int maxResultsSelect = 10;

		System.out.println("maxResultsSelect:sortSrcType => " + maxResultsSelect + ":" + sortSrcType);
		Map<String, List<Long>> eanOprMap = prepareSRListData(maxResultsSelect, filterString, sortKey, sortSrcType, searchTerm, "Book");

		System.out.println("eanOprMap: " + eanOprMap.keySet().size() + "\n" + eanOprMap.keySet() + "\n" + eanOprMap);
		System.out.println("eanOprMap: " + eanOprMap.keySet().size() + "\n" + eanOprMap.keySet());

	}

	// handle academic list view data population
	private final static Map<String, List<Long>> prepareSRListData(int rsltSrchMaxPageCnt, String... params) {

		String filterString = null;
		String sortKey = null;
		String sortSrcType = null;
		String searchTerm = null;
		String productType = null;

		if (null != params) {
			if (null != params[0]) {
				filterString = params[0];
			}
			if (null != params[1]) {
				sortKey = params[1];
			}
			if (null != params[2]) {
				sortSrcType = params[2];
			}
			if (null != params[3]) {
				searchTerm = params[3];
			}
			if ((null != params[4] && !params[4].isEmpty()) && !"All".equalsIgnoreCase(params[4])) {
				productType = "Product:(\"" + params[4].replaceAll("\\,", "\" OR \"") + "\")";
			}

		}

		Map<String, List<Long>> eanOprMap = null;

		int sortType = 0;

		// get all the sort and filter criteria applied for this search
		Map<String, List<String>> filterMap = null;

		AcdmFilterHelper filterHlpr = ((AcdmFilterHelper) AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));

		try {
			sortType = Integer.parseInt(sortSrcType);
		} catch (Exception e) {
			sortType = 1;
		}

		if (null != filterString) {
			filterMap = filterHlpr.filterQueries(filterString);
		}

		// get the result from solr
		// the limit is that set by the user in the power search form
		// or 3000

		// get the list of eans to apply sorting filtering from solr
		// when the filter conditions has a non db solr based filter criteria
		// - get the data from the tables taking care of the above two
		// conditions
		// - form the appropriate solr query and get the filtered data
		String solrFilterQry = null;

		if (null != filterMap && null != filterMap.get("solr") && !filterMap.get("solr").isEmpty()) {
			if (filterMap.get("solr").size() > 1) {
				solrFilterQry = StringUtils.join(filterMap.get("solr"), " AND ");
			} else {
				solrFilterQry = filterMap.get("solr").get(0);
			}
		}

		if (sortType != 5) {
			if (sortType == 2 || sortType == 3) {
				sortKey = "Title_Exact";
			} else if (null == sortKey || sortKey.isEmpty()) {
				sortKey = "Title_Exact";
			}

			// ListSortFilterHelper sortHlpr = ((ListSortFilterHelper)
			// AcademicServiceLocator.getBean(BeanName.SORT_HLPR));
			eanOprMap = getSREansFromSolr(rsltSrchMaxPageCnt, solrFilterQry, sortKey, searchTerm, productType);
		}

		return eanOprMap;

	}

	// re-order academic item details data
	private final static Map<String, List<Long>> getSREansFromSolr(int rsltSrchMaxPageCnt, String... params) {

		String filterString = null;
		String sortKey = null;
		String searchTerm = null;
		String productType = null;

		if (null != params) {
			if (null != params[0]) {
				filterString = params[0];
			}
			if (null != params[1]) {
				sortKey = params[1];
			}
			if (null != params[2]) {
				searchTerm = params[2];
			}
			if (null != params[3]) {
				productType = params[3];
			}

		}
		try {
			Map<String, List<Long>> eanOprMap = null;
			SearchQueryResult qryResult = ((SearchQueryResult) AcademicServiceLocator.getBean(BeanName.QRYRESULT));

			// get all the eans from the list
			Set<String> searchEansSet = new LinkedHashSet<String>();

			SolrQueryAcdm qry = ((SolrQueryAcdm) AcademicServiceLocator.getBean(BeanName.SOLR_QRY));
			// qry.setFields();
			String searchString = null;

			// check for defType and qt in the searchTerm
			if (null != searchTerm && !searchTerm.isEmpty()) {

				if (searchTerm.contains("&")) {
					String[] searchArr = searchTerm.split("\\&");
					String[] search = null;
					searchString = searchArr[0];

					for (String srch : searchArr) {
						search = srch.split("\\=");
						if (search[0].contains("defType")) {
							qry.set("defType", search[1]);
						} else if (search[0].contains("qt")) {
							qry.set("qt", search[1]);
							if (search[1].equalsIgnoreCase("ISBN_SI")) {
								searchString = "EAN:(" + searchString + ")";
							}
						} else if (search[0].contains("fq")) {
							qry.addFilterQuery(search[1]);
						}
					}// for

					qry.set("q", searchString);

				} else {
					qry.set("q", searchTerm);
				}
			}

			if (null != filterString) {
				qry.addFilterQuery(filterString);
			}
			if (null != productType && !productType.isEmpty()) {
				qry.addFilterQuery(productType);
			}
			qry.setRows(rsltSrchMaxPageCnt);

			sortKey = sortKey.trim();
			// check if the sort key contains sort order
			if (sortKey.indexOf("&Nso=") != -1) {

				int sortOrder = Integer.parseInt(sortKey.substring(sortKey.lastIndexOf('=') + 1, sortKey.length()));
				sortKey = sortKey.substring(0, sortKey.indexOf("&Nso="));

				qry.setSort(sortKey, (sortOrder > 0 ? SolrQuery.ORDER.desc : SolrQuery.ORDER.asc));
			} else {
				qry.setSort(sortKey, SolrQuery.ORDER.asc);
			}

			// get the solr actioned eans set
			QueryResponse response = SolrQueryReuseService.getSearchEansSet(qry);
			SolrDocumentList result = response.getResults();
			// qryResult.getFacetList().addAll(response.getFacetFields());

			searchEansSet.clear();
			System.out.println("json result: " + transform(result));
			for (SolrDocument doc : result) {
				searchEansSet.add(doc.get("EAN").toString());
			}

			if (null != searchEansSet && !searchEansSet.isEmpty()) {
				eanOprMap = new LinkedHashMap<String, List<Long>>(searchEansSet.size());
				// put all the sortedSearchEans in the map
				for (String ean : searchEansSet) {
					eanOprMap.put(ean, new LinkedList<Long>());
				}
			}

			qryResult.setEanOprMap(eanOprMap);
			return eanOprMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String transform(SolrDocumentList solrResultList) {

		String jsonResp = "[{'priority':'','browseRowMessage':'','ean':'','communityGroup':'','consortiaShortName':'','userID':'','listName':'','listOwner':'','listType':'','oidSalesOrderDetail':'','actionRejectBoth':'','actionAllowed':'','actionAllowBlock':'','actionAllowClaim':'', 'lastReceived':'', 'netPrice':''}]";

		if (null != solrResultList && !solrResultList.isEmpty() && solrResultList.size() > 0) {

			try {

				ObjectMapperWraper mapper = (ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER);
				// jsonResp = mapper.write(solrResultList);

				return mapper.write(solrResultList);

			} catch (JsonGenerationException e) {
				return jsonResp;
			} catch (JsonMappingException e) {
				return jsonResp;
			} catch (IOException e) {
				return jsonResp;
			}

			// try {
			// for (SolrDocument solrDoc : solrResultList) {
			// if (null != solrDoc) {

			// }// if (null != solrDoc)

			// }// for
			// } catch (Exception e) {

			// }

		} // if (null != data)
		else {
			return jsonResp;
		}

	}

}
