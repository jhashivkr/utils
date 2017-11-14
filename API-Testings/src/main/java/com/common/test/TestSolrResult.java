package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.product.search.acdm.SolrQueryReuseService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import solr.api.SearchException;
import solr.api.SolrServerFactory;
import solr.api.acdm.SolrJServerFactory;

public class TestSolrResult {

	public static void testSolrFacetsResult() {

		//String[] geoAreaCodes = new String[] { "n-us", "e-uk-en", "e-uk", "n-us-ny", "n-us-ca", "n-cn-on", "n-cn", "e", "n-us-tx", "e-uk-st", "n-cn-bc", "n-us-ma", "a-cc", "n-us-il", "n-us-pa",
		//		"e-fr", "n-us-fl", "a-ii", "n-cn-ab", "e-gx", "n-us-va", "e-it", "n-cn-qu", "n-us-la", "e-ie", "n-us-nc", "a-ja", "n-us-ga", "u-at", "n-us-co", "f", "n-us-oh", "n-us-wa", "e-ru",
		//		"n-us-dc", "n-us-mi", "n-us-az", "e-uk-wl", "n-us-nm", "n-us-ak", "n-us-mn", "aw", "n-us-sc", "d", "e-gr", "n-us-nj", "n-us-md", "a", "n-cn-ns", "n-us-tn", "n-us-wi", "e-uk-ni",
		//		"n-us-me", "n-us-hi", "cl", "e-sp", "n-us-or", "n-us-mt", "n-mx", "n-us-mo", "n-us-al", "a-is", "n-us-ky", "n-us-ms", "n-cn-mb", "n-us-in", "n-cn-nf", "n-us-nv", "n-cn-sn", "n-us-ct",
		//		"f-sa", "n", "u-at-ne", "u-at-vi", "n-us-ok", "f-ua", "a-tu", "a-cc-ti", "n-usu", "n-us-ut", "n-us-wy", "a-ir", "n-cn-nk", "n-us-ks", "u-at-we", "e-ur", "a-iq", "n-us-ia", "n-cn-nu",
		//		"s-bl", "n-us-wv", "n-us-nh", "n-us-vt", "n-us-ar", "n-us-id", "n-cn-yk", "n-us-nb", "nwcu", "n-cn-nt", "e-pl" };

		try {
			SolrQueryReuseService solrService = new SolrQueryReuseService();
			List<FacetField> resp = solrService.getAllAvailableFieldValues("CIS_GEOGRAPHIC_AREA_CODE");
			Map<String, String> facetsMap = new HashMap<String, String>();
			List<String> fields = new LinkedList<String>();
			for (FacetField field : resp) {
				System.out.println("facet field: " + field.getName());
				for (Count count : field.getValues()) {
					System.out.print(count.getName() + ":" + count.getCount() + " | ");
					fields.add(count.getName());
				}
				System.out.println();
			}

			for (String key : fields) {
				SolrQuery solrQry = new SolrQuery();
				solrQry.set("q", "*:*");
				solrQry.set("fq", "CIS_GEOGRAPHIC_AREA_CODE:" + key);
				solrQry.setFields("CIS_GEOGRAPHIC_AREA_NAME");
				solrQry.setRows(1);
				

				QueryResponse response;

				response = getSolrData(solrQry);

				SolrDocumentList solrList = response.getResults();
				Iterator<SolrDocument> docItr = solrList.iterator();

				try {
					facetsMap.put(key, docItr.next().get("CIS_GEOGRAPHIC_AREA_NAME").toString());
				} catch (Exception e) {
					facetsMap.put(key, "Not Exist");
				}

			}

		} catch (SearchException e) {
			System.out.println("error from testSolrFacetsResult");
			e.printStackTrace();
		}

	}

	public static void testSolrResponse() {
		// "solr-ipage-dev.ingramtest.com:8080/solr/ipage/select?wt=json&indent=on&q=ITEM_OHND:Y&fl=EAN,PRICE_*&rows=100";
		// String qry = "&fl=EAN,PRICE_*&rows=100";

		SolrQuery solrQry = new SolrQuery();
		solrQry.set("q", "red");
		solrQry.set("qt", "Title_SI");

		// solrQry.set("q", "0604388499625 OR 9780413758606");
		// solrQry.set("fq", "ITEM_OHND:Y");
		// solrQry.setFields("EAN","PRICE_*");
		// solrQry.setFields("EAN", "CIS_OUT_OF_PRINT", "PRICE_US_MS");

		solrQry.setFields("EAN", "ITEM_OHND_QTY_*");
		// solrQry.addFilterQuery("(ITEM_OHND:Y AND (ITEM_OHND_QTY_*:[0 TO 10]))");
		// solrQry.addFilterQuery("(ITEM_OHND:Y)");
		solrQry.addFilterQuery("(ITEM_OHND:Y AND (ITEM_OHND_QTY_CI:[0 TO 10] OR ITEM_OHND_QTY_DD:[0 TO 10] OR ITEM_OHND_QTY_EE:[0 TO 10] OR ITEM_OHND_QTY_NV:[0 TO 10]))");

		solrQry.setRows(100);

		System.out.println(solrQry.toString());

		QueryResponse response = null;
		SolrServer server = null;
		try {

			server = ((SolrJServerFactory) AcademicServiceLocator.getBean(BeanName.SOLR_RESP_WORKER)).getDefaultInstance();

			solrQry.setTimeAllowed(SolrServerFactory.getMaximumQueryMs());
			response = server.query(solrQry, METHOD.POST);

			SolrDocumentList solrList = response.getResults();
			Iterator<SolrDocument> docItr = solrList.iterator();

			for (; docItr.hasNext();) {
				SolrDocument doc = docItr.next();

				// System.out.println("available solr field Names: " + doc.getFieldNames());
				System.out.println("doc: " + doc);
				// System.out.println("EAN: " + doc.get("EAN"));
				// System.out.println("CIS_OUT_OF_PRINT: " + doc.get("CIS_OUT_OF_PRINT"));
				// System.out.println("PRICE_US_MS: " + doc.get("PRICE_US_MS"));
				// System.out.println("PRICE_*: " + doc.get("PRICE_*"));
				// System.out.println("PRICE_CA_MS: " + doc.get("PRICE_CA_MS"));

				// Object o = doc.get("PRICE_US_MS");
				// if (o instanceof Collection<?>) {
				// o = doc.getFirstValue("PRICE_US_MS");
				// }
				// System.out.println("o: " + o);
			}

		} catch (Exception e) {
			System.out.println("error: " + e);
		} finally {
			server.shutdown();
		}
	}

	private static final QueryResponse getSolrData(SolrQuery solrQuery) throws SearchException {

		QueryResponse response = null;
		SolrServer server = null;
		try {

			server = ((SolrJServerFactory) AcademicServiceLocator.getBean(BeanName.SOLR_RESP_WORKER)).getDefaultInstance();
			solrQuery.setTimeAllowed(SolrServerFactory.getMaximumQueryMs());
			response = server.query(solrQuery, METHOD.POST);

		} catch (Exception e) {
			System.out.println("Error performing solr query");
			e.printStackTrace();
		} finally {
			server.shutdown();
		}

		return response;
	}

}
