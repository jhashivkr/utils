package com.common.test;


import ibg.common.VariableData;
import ibg.product.search.SearchFields;
import ibg.product.search.acdm.SearchConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import solr.api.SchemaEntry;
import solr.api.SchemaEntry.Types;
import solr.api.SchemaLoader;
import solr.api.SearchConfiguration;
import solr.api.SearchException;
import solr.api.endeca.EndecaDimension;



public class TestSolrQuery implements SearchConstants {

	private static Map<String, List<SchemaEntry>> schemaByEndecaName;
	private static Map<String, SchemaEntry> schemaBySolrName;
	private static Map<String, SchemaEntry> schemaByFacetableSolrName;
	private static ClassPathXmlApplicationContext context = null;
	private SearchBuildObject sbo;

	/*
	 * //String N - navDescriptors //String Ntk - searchFields //String Ntt - searchTerms //String Nty - didYouMean
	 * //String Ntx - searchModes
	 * 
	 * Ntk = "Title"; Ntk = "Contrib"; Ntk = "Director"; Ntk = "ISBN"; Ntk = startOfCriteria[2]; //Title_Exact Ntk =
	 * startOfCriteria[2];//Title_Phrase Ntk = exactTitle[2]; //Title_Exact Ntk = "Keyword"; Ntk = "Series"; Ntk =
	 * "OCLC_NUM_ID"; Ntk = "SongList"; Ntk = "SongList"; Ntk = "SJCG_DEWY_DEC_NBR"; Ntk = "LC_CL_SEARCH"; Ntk =
	 * "Rating"; Ntk = "Author/Title(4,4)"; //Author4_Title4 Ntk = "TOC"; Ntk = "BISAC_Categories";
	 * 
	 * productType = "All"; searchTerm = startOfCriteria[0]; //title with * appended searchTerm =
	 * startOfCriteria[0];//search terms as it is searchTerm = exactTitle[0]; //search string
	 * 
	 * Ntx = startOfCriteria[1]; //mode matchall Ntx = startOfCriteria[1];//mode matchall Ntx = exactTitle[1]; //mode
	 * matchall Ntx = "mode matchany"; Ntx = "mode matchany"; Ntx = "mode matchany"; Ntx = "mode matchall";
	 */

	static {

		try {

			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void loadSchema() {

		try {
			SchemaLoader loader = new SchemaLoader(VariableData.getProperty("SOLR_SCHEMA_LOCATION"));
			schemaByEndecaName = loader.getSchemaByEndecaName();
			schemaBySolrName = loader.getSchemaBySolrName();

			schemaByFacetableSolrName = new HashMap<String, SchemaEntry>();
			for (List<SchemaEntry> entries : schemaByEndecaName.values()) {

				for (SchemaEntry entry : entries) {
					if (entry.isFacetable()) {
						schemaByFacetableSolrName.put(entry.getSolrName(), entry);
						// System.out.println(entry.getSolrName());
					}
				}
			}

			sbo = new SearchBuildObject("Title_SI", true, "red", "All", "mode matchall");

			// printEndecaSchema(schemaByEndecaName);
			// printSolrSchema(schemaBySolrName);
			// printFacetableSolrSchema(schemaByFacetableSolrName);

		} catch (Exception e) {
			schemaByEndecaName = null;
		}
	}

	private void printEndecaSchema(Map<String, List<SchemaEntry>> schemaByEndecaName) {
		// print
		System.out.println("schemaByEndecaName: ");
		for (String key : schemaByEndecaName.keySet()) {
			for (SchemaEntry entry : schemaByEndecaName.get(key)) {
				if (null != entry.getaFineSubstitution())
					System.out.printf("key | value : %s | %s\n", key, entry.toString());
			}

		}
	}

	private void printSolrSchema(Map<String, SchemaEntry> schemaBySolrName) {
		System.out.println("schemaBySolrName: ");
		for (String key : schemaBySolrName.keySet()) {
			// if (null != schemaBySolrName.get(key).getaFineSubstitution())
			System.out.printf("key | value : %s | %s\n", key, schemaBySolrName.get(key).toString());
		}
	}

	private void printFacetableSolrSchema(Map<String, SchemaEntry> schemaByFacetableSolrName) {
		System.out.println("schemaByFacetableSolrName: ");
		for (String key : schemaByFacetableSolrName.keySet()) {
			System.out.printf("key | value : %s | %s\n", key, schemaByFacetableSolrName.get(key).toString());
		}
	}

	private void getQt(String searchBy, SolrQuery solrquery, SearchBuildObject sbo) {

		String qt = null;
		if (null != searchBy) {
			// SOLR can only use 1 Search Interface per query, so whenever the query specifies more than 1 we have to
			// pick.
			// search the schemaBySolrName map (loaded by the loader) to find if searchinterface value is true
			// get the last one for what the that value is true

			for (String key : schemaBySolrName.keySet()) {
				if (true == schemaBySolrName.get(key).isSearchInterface()) {
					qt = schemaBySolrName.get(key).getSolrName();
				}
			}

			if (null == qt || qt.isEmpty()) {
				solrquery.set("defType", "edismax");
			} else {
				solrquery.set("qt", qt);
			}

			/*
			 * Map<String, SchemaEntry> searchInterfacesNeededBySolrName = new HashMap<String, SchemaEntry>();
			 * 
			 * for (ERecSearch search : query.getNavERecSearches()) { SchemaEntry entry =
			 * SearchConfiguration.getSearchableFieldByEndecaName(search.getKey(), true);
			 * 
			 * if (entry != null && entry.isSearchInterface()) {
			 * System.out.println("buildExtendedDismaxQuery: 2:search: " + search.getKey() + ", " + entry.toString());
			 * searchInterfacesNeededBySolrName.put(entry.getSolrName(), entry); } }// for if
			 * (searchInterfacesNeededBySolrName.size() == 1) { sbo.searchInterfaceToUse =
			 * searchInterfacesNeededBySolrName.values().iterator().next();
			 * System.out.println("buildExtendedDismaxQuery: 3: " + sbo.searchInterfaceToUse.toString());
			 * 
			 * } else if (searchInterfacesNeededBySolrName.size() > 1) { for (SchemaEntry entry :
			 * searchInterfacesNeededBySolrName.values()) { if (entry.getaFineSubstitution() != null) { SchemaEntry
			 * theOneToUse = searchInterfacesNeededBySolrName.get(entry.getaFineSubstitution()); if (theOneToUse !=
			 * null) { sbo.searchInterfaceToUse = theOneToUse; } System.out.println("buildExtendedDismaxQuery: 4: " +
			 * sbo.searchInterfaceToUse.toString()); } } }// else if (searchInterfacesNeededBySolrName.size() > 0 &&
			 * sbo.searchInterfaceToUse == null) { for (SchemaEntry entry : searchInterfacesNeededBySolrName.values()) {
			 * SchemaEntry nonSiSubstitution = SearchConfiguration.getSearchableFieldByEndecaName(entry.getEndecaName(),
			 * false); if (nonSiSubstitution == null) { sbo.searchInterfaceToUse = entry; break; } }
			 * System.out.println("buildExtendedDismaxQuery: 5: " + sbo.searchInterfaceToUse.toString()); }
			 * 
			 * parseKeywordSearches(sbo); parseRangeSearches(sbo); }
			 * 
			 * if (sbo.qb.length() == 0) { sbo.qb.append("*:*"); }
			 * 
			 * SolrQuery solrquery = new SolrQuery(sbo.qb.toString()); if (sbo.searchInterfaceToUse != null) {
			 * solrquery.set("qt", sbo.searchInterfaceToUse.getSolrName());
			 * 
			 * // If the query string is very long, disable spell checking // as a defensive measure against very poor
			 * performance. if (sbo.qb.length() > 200 || sbo.disableSpellCorrection) { solrquery.set("spellcheck",
			 * "false"); } } else { solrquery.set("defType", "edismax"); }
			 */
		}
	}

	private void getQ(SolrQuery solrquery, SearchBuildObject sbo) {

		// String searchBy, boolean searchOption, String searchTerm, String searchProductType, String searchModes

		parseKeywordSearches(sbo);
		// parseRangeSearches(sbo)

		if (sbo.qb.length() == 0) {
			sbo.qb.append("*:*");
		}

		// If the query string is very long, disable spell checking
		// as a defensive measure against very poor performance.
		if (sbo.qb.length() > 200 || sbo.disableSpellCorrection) {
			solrquery.set("spellcheck", "false");
		}

		// String N - navDescriptors //String Ntk - searchFields //String Ntt - searchTerms //String Nty - didYouMean
		// String Ntx - searchModes

	}

	private void parseKeywordSearches(SearchBuildObject sbo) {
		if (sbo.isSearchPossible()) {
			return;
		}

		// for (ERecSearch search : sbo.query.getNavERecSearches()) {
		// String erecsearchTerms = search.getTerms().trim();
		// String erecsearchKey = search.getKey();

		String erecsearchTerms = sbo.getSearchTerm();
		String erecsearchKey = sbo.getSearchBy();

		System.out.println("parseKeywordSearches: 1: " + erecsearchTerms + ", " + erecsearchKey);

		if ("Citation".equals(erecsearchKey)) {
			Matcher citMatch = citationIdPattern.matcher(erecsearchTerms);
			if (citMatch.find()) {
				erecsearchTerms = citMatch.group(1);
				erecsearchKey = "SRC_CITA_ID";
			} else {
				erecsearchKey = "SRC_CITA_DN";
			}
		}

		boolean isABooleanSearch = sbo.getSearchModes() != null && sbo.getSearchModes().toLowerCase().contains("boolean");
		boolean isAMatchAllSearch = sbo.getSearchModes() == null || sbo.getSearchModes().length() == 0 || sbo.getSearchModes().toLowerCase().contains("all");

		System.out.println("parseKeywordSearches: 2: " + isABooleanSearch + ", " + isAMatchAllSearch);

		SchemaEntry schemaEntry = getSchemaEntry(erecsearchKey, sbo);
		System.out.println("parseKeywordSearches: 3: " + schemaEntry.toString());

		if (!isABooleanSearch && !schemaEntry.isFacetable()) {
			erecsearchTerms = floatingPunctuationPattern.matcher(erecsearchTerms).replaceAll(" ");
			System.out.println("parseKeywordSearches: 4: " + erecsearchTerms);
			erecsearchTerms = escapeMetacharacters(erecsearchTerms);
			System.out.println("parseKeywordSearches: 5: " + erecsearchTerms);
		}
		String[] terms = prepareTerms(schemaEntry, erecsearchTerms, isABooleanSearch);

		String field = schemaEntry.getSolrName();
		String mode = " OR ";
		if (isAMatchAllSearch) {
			mode = " AND ";
		}
		if (!sbo.searchFirstTime) {
			sbo.qb.append(" AND ");
		}

		if (!schemaEntry.isSearchInterface()) {
			sbo.qb.append("( ");
			sbo.qb.append(field).append(":( ");
		}

		System.out.println("parseKeywordSearches: 6: " + sbo.toString());
		parseTerms(terms, schemaEntry, field, mode, sbo, isABooleanSearch);
		System.out.println("parseKeywordSearches: 7: " + sbo.toString());

		if (!schemaEntry.isSearchInterface()) {
			sbo.qb.append(" )");
			sbo.qb.append(" )");
		}
		sbo.searchFirstTime = false;
		// }// for
	}

	private void parseRangeSearches(SearchBuildObject sbo) {
		// if (sbo.query.getRangeFilters() == null) {
		// return;
		// }

		// for (RangeFilter rf : sbo.query.getNavRangeFilters()) {
		String filterQuery = sbo.getFilterQuery();
		if (filterQuery.indexOf("%") > -1) {
			try {
				filterQuery = URLDecoder.decode(filterQuery, "UTF-8");
			} catch (UnsupportedEncodingException usee) {
				throw new AssertionError("UTF-8 ought to be supported");
			}
		}

		Matcher m = rangeFilterPattern.matcher(filterQuery);
		boolean valid = true;
		SchemaEntry schemaEntry = null;
		String from = "*";
		String to = "*";
		String operator = null;
		System.out.println(m.find() + " - " + m.groupCount());
		if (m.find() && m.groupCount() >= 3) {
			String field = m.group(1);
			schemaEntry = getSchemaEntry(field, sbo);
			System.out.println("1: " + schemaEntry.toString());
			operator = m.group(2).toLowerCase();
			String operand = m.group(3);
			if ("null".equals(operand)) {
				operand = "*";
			}

			if ("lt".equals(operator)) {
				to = bumpValue(operand, schemaEntry, true);
			} else if ("lteq".equals(operator)) {
				to = operand;
			} else if ("gt".equals(operator)) {
				from = bumpValue(operand, schemaEntry, false);
			} else if ("gteq".equals(operator)) {
				from = operand;
			} else if ("btwn".equals(operator) && m.groupCount() == 4) {
				from = operand;
				to = "null".equals(m.group(4)) ? "*" : m.group(4);
			} else {
				valid = false;
			}
		} else {
			valid = false;
		}

		if (!valid) {
			try {
				throw new SearchException("Invalid Range Filter: " + filterQuery);
			} catch (SearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (!sbo.searchFirstTime) {
				sbo.qb.append(" AND ");
			}
			sbo.qb.append("(");
			if (!schemaEntry.isSearchInterface()) {
				sbo.qb.append(schemaEntry.getSolrName()).append(":");
			}
			sbo.qb.append("[").append(from).append(" TO ").append(to).append("]");
			sbo.qb.append(")");
			sbo.searchFirstTime = false;
		}
		// }
	}

	private String bumpValue(String originalValue, SchemaEntry schemaEntry, boolean bumpDownNotUp) {
		if (schemaEntry.getType() == Types.INTEGER) {
			try {
				int i = Integer.parseInt(originalValue);
				i = bumpDownNotUp ? i - 1 : i + 1;
				return "" + i;
			} catch (NumberFormatException nfe) {
				try {
					throw new SearchException(originalValue + " is not a valid integer for range searching.");
				} catch (SearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (schemaEntry.getType() == Types.DECIMAL) {
			try {
				float f = Float.parseFloat(originalValue);
				f = bumpDownNotUp ? f - .000001f : f + .000001f;
				return "" + f;
			} catch (NumberFormatException nfe) {
				try {
					throw new SearchException(originalValue + " is not a valid decimal for range searching.");
				} catch (SearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return originalValue;
	}

	private void parseTerms(String[] terms, SchemaEntry schemaEntry, String field, String mode, SearchBuildObject sbo, boolean isABooleanSearch) {
		boolean inAPhrase = false;
		boolean termFirstTime = true;
		for (String term : terms) {
			if (!termFirstTime) {
				if (inAPhrase) {
					sbo.qb.append(" ");
				} else {
					sbo.qb.append(mode);
				}
			}
			termFirstTime = false;

			if (!isABooleanSearch) {
				term = setupWildcardSearchTerms(schemaEntry, term, inAPhrase);

				// In Endeca, we stored Title_Exact replacing Greek Xi's for spaces.
				// (sometimes ipage code corrupts this to code point 206 followed by 190)
				// This caused each title to be indexed as 1 big term.
				// In SOLR, we retain the spaces but index as "string" (not "text") to get the same result (cleaner).
				if ("Title_Exact".equals(field)) {
					term = term.replaceAll("" + ((char) 958), "\\\\ ").replaceAll("" + ((char) 206), "\\\\ ").replaceAll("" + ((char) 190), "").toUpperCase();
				}
				// SOLR can index a *, so we are not using "Y" as a workaround as in Endeca.
				else if ("SRC_CITA_RCMD_CD".equals(field) && "Y".equals(term)) {
					term = "\\*";
					// These are indexed uppercase and SOLR is case-sensitive for String types
				} else if ("SJCG_DEWY_DEC_NBR".equals(field) || "LC_CL_SEARCH".equals(field) || "Suppliway_Publisher".equals(field)) {
					term = term.toUpperCase();
				} else if ("PROD_ABBV_TAG_NBR".equals(field)) {

					term = term.toUpperCase();
					if (term.indexOf(",") == -1) {
						if (term.length() == 8) {
							term = term.substring(0, 4) + "," + term.substring(4, 8);
						} else if (term.length() == 9) {
							term = term.substring(0, 4) + "," + term.substring(5, 9);
						}
					}
				}
				Matcher quoteMatch = quotePattern.matcher(term);
				if (schemaEntry.isPhraseSearchable()) {
					while (quoteMatch.find()) {
						inAPhrase = !inAPhrase;
					}
				} else {
					term = quoteMatch.replaceAll("");
				}
			}
			sbo.qb.append(term);

			if (term.length() > 25) {
				sbo.disableSpellCorrection = true;
			}
		}
	}

	// sud go in abstract class
	private String[] prepareTerms(SchemaEntry entry, String rawTerms, boolean isABooleanSearch) {
		if (entry.isFacetable() && !entry.getEndecaName().equals("Guided Reading Level")) {
			rawTerms = escapeMetacharacters(rawTerms);
			rawTerms = singleSpacePattern.matcher(rawTerms).replaceAll("\\\\ ");

			return new String[] { rawTerms };
		}

		if (!isABooleanSearch && !(rawTerms.charAt(0) == '"' && rawTerms.charAt(rawTerms.length() - 1) == '"')) {
			// If not a Boolean Search any AND/OR/NOT's should be
			// wrapped in quotes to prevent boolean logic.
			Matcher andOrNotMatch = andOrNotPattern.matcher(rawTerms);
			StringBuffer replacementTerms = new StringBuffer();
			while (andOrNotMatch.find()) {
				String replaceWith = andOrNotMatch.group(1);
				if ("and".equalsIgnoreCase(replaceWith)) {
					replaceWith = "\"AND\"";
				} else if ("or".equalsIgnoreCase(replaceWith)) {
					replaceWith = "\"OR\"";
				}
				if ("not".equalsIgnoreCase(replaceWith)) {
					replaceWith = "\"NOT\"";
				}
				andOrNotMatch.appendReplacement(replacementTerms, replaceWith);
			}
			andOrNotMatch.appendTail(replacementTerms);
			rawTerms = replacementTerms.toString();
		}

		String[] splitTerms = null;
		if (isABooleanSearch) {
			splitTerms = new String[1];
			splitTerms[0] = convertBooleanFieldnames(rawTerms);
		} else if ("PROD_ABBV_TAG_NBR".equals(entry.getSolrName())) {
			splitTerms = new String[1];
			splitTerms[0] = rawTerms;
		} else {
			splitTerms = spacePattern.split(rawTerms);
		}

		// Multivalued, multiple-word queries need to be converted to phrases with slop to prevent cross-field matches.
		// (This also prevents wildcard searches from working)
		if (!isABooleanSearch && splitTerms.length > 1 && entry.isMultiValued() && entry.isPhraseSearchable()) {
			quotePattern.matcher(rawTerms).replaceAll("");
			splitTerms = spacePattern.split("\"" + rawTerms + "\"~50");
		}
		return splitTerms;
	}

	// sud go in abstract class
	private String convertBooleanFieldnames(String query) {
		// This is tailored to produce a valid query "most of the time" given the output
		// from BooleanServlet.java ... (small changes were made there as well to ease complexity here...)

		// remove extra spaces to simplify subsequent changes
		query = query.replaceAll("(\\W)\\s+(\\W)", "$1$2");

		// For each restrict operator, convert the field name from Endeca to Solr and make other changes to the subquery
		// as needed.
		StringBuffer qb = new StringBuffer();
		Matcher m = Pattern.compile("([a-zA-Z0-9_\\-]+)[:][\\s]*[(](.*?)[)](\\s+|AND|OR|NOT|$)").matcher(query);
		while (m.find()) {
			String fieldToSearchOn = m.group(1);
			SchemaEntry entry = SearchConfiguration.getSearchableFieldByEndecaName(fieldToSearchOn, false);
			if (entry == null) {
				try {
					throw new SearchException(fieldToSearchOn + " was specified for searching but not configured as Searchable in SOLR ... from boolean query: " + query);
				} catch (SearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			SearchBuildObject sbo = new SearchBuildObject();
			String field = entry.getSolrName();

			// Hack to compensate for an imperfect regex that sometimes captures parenthesis with keywords...
			String addToFront = null;
			String addToRear = null;
			String rawTerm = m.group(2);
			if (rawTerm.startsWith("(")) {
				rawTerm = rawTerm.substring(1);
				addToFront = "(";
			}
			if (rawTerm.endsWith(")")) {
				rawTerm = rawTerm.substring(0, rawTerm.length() - 1);
				addToRear = ")";
			}

			String[] terms = prepareTerms(entry, rawTerm, false);

			parseTerms(terms, entry, field, " ", sbo, false);
			if (addToFront != null) {
				sbo.qb.insert(0, addToFront);
			}
			if (addToRear != null) {
				sbo.qb.append(addToRear);
			}

			String replacement = field + ":(" + sbo.qb + ") ";

			// This compensates for "m.appendReplacement()" interpreting backslashes as metacharacters (and disappear).
			// We want literal backslashes, provided the backslash is not immediately preceded with "AND|OR|NOT" or
			// followed by: EITHER a space then "AND|OR|NOT" OR no space then a closing parenthesis.
			replacement = replacement.replaceAll("(?<!OR|AND|NOT)\\\\(?!\\sOR|\\sAND|\\sNOT|\\S\\))", "\\\\\\\\");
			m.appendReplacement(qb, replacement);
		}
		m.appendTail(qb);

		// Within parentheses, insert AND between each keyword, if not already there and if not a phrase query.
		Matcher m1 = Pattern.compile("[(][^\\)\\(\"~\\\\]+?[)]").matcher(qb.toString());
		qb.delete(0, qb.length());
		while (m1.find()) {
			StringBuilder replacement = new StringBuilder();
			String[] tokens = spacePattern.split(m1.group(0));
			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				if (replacement.length() > 0) {
					replacement.append(" AND ");
				}
				replacement.append(token);
			}

			m1.appendReplacement(qb, replacement.toString());
		}
		m1.appendTail(qb);

		// Hack to fix mistakenly putting "and ... and" around other operators
		String finalAnswer = qb.toString().replaceAll("AND (AND|OR|NOT) AND", "$1");

		return finalAnswer;
	}

	// sud go in abstract class
	private String setupWildcardSearchTerms(SchemaEntry schemaEntry, String term, boolean inAPhrase) {
		if (inAPhrase || !schemaEntry.isWildcardSearchable()) {
			// If within a phrase, remove all wildcards as these are not supported.
			term = wildcardPattern.matcher(term).replaceAll("");
		} else {
			// Remove all but trailing wildcards.
			// (edismax supports internal/leading wildcards but we
			// should not allow them for performance reasons.)
			StringBuilder termBuilder = new StringBuilder(term);
			for (int j = 0; j < termBuilder.length() - 1; j++) {
				if (termBuilder.charAt(j) == '*') {
					termBuilder.deleteCharAt(j);
				}
			}
			term = termBuilder.toString();

			// Do not allow just a wildcard.
			if ("*".equals(term)) {
				term = "";
			}

			if (schemaEntry.isWildcardSearchRequiresPreLowercasing() && term.endsWith("*")) {
				term = term.toLowerCase();
			}
		}
		return term;
	}

	private void parseRangeFilters(String top, SolrQuery solrquery, boolean isSA) {
		String desirabilityField;

		/*********************************/
		// do something for top demand here.
		if ("Y".equals(top) || "SA".equals(top) || "IBC".equals(top)) {

			desirabilityField = "TOT_DSRB_CNT";
			solrquery.addSort("TOT_DSRB_CNT", ORDER.desc);

			if (!"IBC".equals(top) && (isSA || "SA".equals(top))) {
				desirabilityField = "SA_TOT_DSRB_CNT";
				solrquery.addSort("SA_TOT_DSRB_CNT", ORDER.desc);
			}

			// execute the query
			long numfound = 0;
			// try {
			// numfound = server.query(solrquery).getResults().getNumFound();
			// } catch (SolrServerException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			// if (numfound > 0) {
			// urlg.addParam("Nf", desirabilityField + "|GTEQ " + numfound);
			// }

		}
	}

	private class SearchBuildObject {
		private SchemaEntry searchInterfaceToUse = null;
		private boolean searchFirstTime = true;
		private StringBuilder qb = new StringBuilder();
		private boolean disableSpellCorrection = false;

		private String searchBy;
		private boolean searchOption;
		private String searchTerm;
		private String searchProductType;
		private String searchModes;
		private String filterQuery;
		private SolrQuery solrquery;

		private boolean searchPossible = false;

		private SearchBuildObject() {

		}

		private SearchBuildObject(String searchBy, boolean searchOption, String searchTerm, String searchProductType, String searchModes) {
			this.searchBy = searchBy;
			this.searchOption = searchOption;
			this.searchTerm = searchTerm;
			this.searchProductType = searchProductType;
			this.searchModes = searchModes;
			solrquery = new SolrQuery();
		}

		public boolean isSearchPossible() {
			searchPossible = false;
			if (null != searchBy && !searchBy.isEmpty()) {
				if (searchBy.trim().length() > 0) {
					searchPossible = true;
				}
			}

			return searchPossible;
		}

		public String getSearchBy() {
			return searchBy;
		}

		public boolean isSearchOption() {
			return searchOption;
		}

		public String getSearchTerm() {
			return searchTerm;
		}

		public String getSearchProductType() {
			return searchProductType;
		}

		public String getSearchModes() {
			return searchModes;
		}

		public String getFilterQuery() {
			return filterQuery;
		}

		public void setFilterQuery(String filterQuery) {
			this.filterQuery = filterQuery;
		}

		@Override
		public String toString() {
			return "SearchBuildObject [searchInterfaceToUse=" + searchInterfaceToUse + ", searchFirstTime=" + searchFirstTime + ", qb=" + qb + ", disableSpellCorrection=" + disableSpellCorrection
					+ ", searchBy=" + searchBy + ", searchOption=" + searchOption + ", searchTerm=" + searchTerm + ", searchProductType=" + searchProductType + ", searchModes=" + searchModes
					+ ", filterQuery=" + filterQuery + ", solrquery=" + solrquery + ", searchPossible=" + searchPossible + "]";
		}

	}

	private void analyzeSchemaData() {
		// playDims(schemaByFacetableSolrName);

		// SchemaEntry entry = schemaBySolrName.get("Title");
		// System.out.println(entry.getSolrName() + ", " + entry.isFacetable());

		// entry = schemaBySolrName.get("Title_Exact");
		// System.out.println(entry.getSolrName() + ", " + entry.isFacetable());

		// System.out.printf("Title_Phrase (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("Title"),
		// schemaByEndecaName.get("Title"));
		// System.out.printf("Keyword (solr | endeca):\n %s \n %s\n%s\n\n", schemaBySolrName.get("Keyword_SI"),
		// schemaByEndecaName.get("Keyword"), schemaByFacetableSolrName.get("Keyword_SI"));

		/*
		 * Ntk = "Title"; Ntk = "Contrib"; Ntk = "Director"; Ntk = "ISBN"; Ntk = startOfCriteria[2]; //Title_Exact Ntk =
		 * startOfCriteria[2];//Title_Phrase Ntk = exactTitle[2]; //Title_Exact Ntk = "Keyword"; Ntk = "Series"; Ntk =
		 * "OCLC_NUM_ID"; Ntk = "SongList"; Ntk = "SongList"; Ntk = "SJCG_DEWY_DEC_NBR"; Ntk = "LC_CL_SEARCH"; Ntk =
		 * "Rating"; Ntk = "Author/Title(4,4)"; //Author4_Title4 Ntk = "TOC"; Ntk = "BISAC_Categories";
		 * 
		 * productType = "All"; searchTerm = startOfCriteria[0]; //title with * appended searchTerm =
		 * startOfCriteria[0];//search terms as it is searchTerm = exactTitle[0]; //search string
		 * 
		 * Ntx = startOfCriteria[1]; //mode matchall Ntx = startOfCriteria[1];//mode matchall Ntx = exactTitle[1];
		 * //mode matchall Ntx = "mode matchany"; Ntx = "mode matchany"; Ntx = "mode matchany"; Ntx = "mode matchall";
		 */

		/*
		 * 
		 * System.out.printf("Dewey (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("Dewey"),
		 * schemaByEndecaName.get("Dewey")); System.out.printf("Title (solr | endeca):\n %s \n %s\n\n",
		 * schemaBySolrName.get("Title"), schemaByEndecaName.get("Title"));
		 * System.out.printf("Contrib (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("CTBR_SORT"),
		 * schemaByEndecaName.get("Contrib")); System.out.printf("Director (solr | endeca):\n %s \n %s\n\n",
		 * schemaBySolrName.get("Director"), schemaByEndecaName.get("Director"));
		 * System.out.printf("ISBN (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("ISBN"),
		 * schemaByEndecaName.get("ISBN"));
		 * 
		 * System.out.printf("Title_Exact (solr | endeca):\n %s \n %s\n%s\n\n", schemaBySolrName.get("Title_Exact"),
		 * schemaByEndecaName.get("Title_Exact"), schemaByFacetableSolrName.get("Title_Exact"));
		 * 
		 * System.out.printf("Title_Phrase (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("Title"),
		 * schemaByEndecaName.get("Title"));
		 * 
		 * System.out.printf("Keyword (solr | endeca):\n %s \n %s\n%s\n\n", schemaBySolrName.get("Keyword_SI"),
		 * schemaByEndecaName.get("Keyword"), schemaByFacetableSolrName.get("Keyword_SI"));
		 * 
		 * System.out.printf("Series (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("Series"),
		 * schemaByEndecaName.get("Series")); System.out.printf("OCLC_NUM_ID (solr | endeca):\n %s \n %s\n\n",
		 * schemaBySolrName.get("OCLC_NUM_ID"), schemaByEndecaName.get("OCLC_NUM_ID"));
		 * System.out.printf("SongList (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("SONGS"),
		 * schemaByEndecaName.get("SongList")); System.out.printf("SJCG_DEWY_DEC_NBR (solr | endeca):\n %s \n %s\n\n",
		 * schemaBySolrName.get("SJCG_DEWY_DEC_NBR"), schemaByEndecaName.get("SJCG_DEWY_DEC_NBR"));
		 * System.out.printf("LC_CL_SEARCH (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("LC_CL_SEARCH"),
		 * schemaByEndecaName.get("LC_CL_SEARCH")); System.out.printf("Rating (solr | endeca):\n %s \n %s\n\n",
		 * schemaBySolrName.get("Rating"), schemaByEndecaName.get("Rating"));
		 * System.out.printf("Author/Title(4,4) (solr | endeca):\n %s \n %s\n\n",
		 * schemaBySolrName.get("Author4_Title4"), schemaByEndecaName.get("Author4_Title4"));
		 * System.out.printf("TOC (solr | endeca):\n %s \n %s\n\n", schemaBySolrName.get("TOC_DN"),
		 * schemaByEndecaName.get("TOC")); System.out.printf("BISAC_Categories (solr | endeca):\n %s \n %s\n %s \n\n",
		 * schemaBySolrName.get("BSAC_DN1"), schemaBySolrName.get("BSAC_DN2"),
		 * schemaByEndecaName.get("BISAC_Categories")); System.out.printf("BISAC_Categories (solr | endeca):\n %s \n\n",
		 * schemaBySolrName.get("BSAC_SEARCH"));
		 */
	}

	private void playDims() {

		Set<String> fieldListFields = new HashSet<String>(SearchFields.EXPANDED_VIEW_ACDM.getFields());
		Collection<EndecaDimension> dims = SearchConfiguration.getDimensions();

		for (EndecaDimension dim : dims) {
			SchemaEntry entry = SearchConfiguration.getFacetableFieldByEndecaName(dim.getDimensionName());

			if (null != entry) {
				// System.out.printf("%s = ,%s,%s,%s\n", dim.getDimensionName(), entry.toString());
				System.out.printf("%s = ,%s\n", dim.getDimensionName(), entry.toString());
				
				//entry.isMultiValued()
			}

			// String endecaName = entry.getEndecaName();
			// String solrName = entry.getSolrName();
		}

		System.out.println("****************************************");

		for (String dim : fieldListFields) {
			SchemaEntry entry = schemaByFacetableSolrName.get(dim);
			SchemaEntry entry1 = SearchConfiguration.getFacetableFieldBySolrName(dim);

			if (null != entry && null != entry1) {
				// System.out.printf("%s = %s,%s,%s,%s\n", dim, entry.isMultiValued(), entry.isViewable(),
				// entry.isSortable(),
				// SearchConfiguration.getMultiLevelEndecaDimensionFromSolrName(entry.getSolrName()));
				System.out.printf("%s = %s,%s\n", dim, entry.toString(), SearchConfiguration.getMultiLevelEndecaDimensionFromSolrName(entry.getSolrName()));
			}
		}
		/*
		 * for (EndecaDimension dim : dims) { System.out.println("SearchConfiguration.getDimensions(): " +
		 * dim.toString());
		 * 
		 * //if ((fieldListFields.size() == 0 || fieldListFields.contains(dim.getDimensionName())) &&
		 * !dimensionNamesNotShown.contains(dim.getDimensionName()) && EndecaSolrUtilities.isDimensionShown(enequery,
		 * dim)) { if ((fieldListFields.size() == 0 || fieldListFields.contains(dim.getDimensionName()))) { SchemaEntry
		 * entry = SearchConfiguration.getFacetableFieldByEndecaName(dim.getDimensionName()); if (entry == null) { throw
		 * new SearchException(dim.getDimensionName() +
		 * " is set up as an Endeca Dimension but not configured for Faceting with Solr."); }
		 * 
		 * String endecaName = entry.getEndecaName(); String solrName = entry.getSolrName(); boolean facetLimitNegOne =
		 * false; if (dim.isMultiLevel()) { Integer facetingLevel = facetLevel.get(endecaName); if (facetingLevel ==
		 * null) { facetingLevel = 0; } else { facetingLevel++; } String[] solrNamesByLevel = dim.getSolrNameByLevel();
		 * 
		 * // Skip if the user has already arrived at a leaf node. if (facetingLevel > solrNamesByLevel.length - 1) {
		 * continue; } solrName = solrNamesByLevel[facetingLevel]; if (!enequery.isBr003Facets() && facetingLevel > 0) {
		 * setParam(solrquery, ("f." + solrName + ".facet.sort"), "lex", false); setParam(solrquery, ("f." + solrName +
		 * ".facet.limit"), "-1", false); facetLimitNegOne = true; } }
		 * 
		 * if (SearchConfiguration.isRangeDimension(dim.getDimensionName())) { for (String query :
		 * SearchConfiguration.getRangeQueryStrings(dim.getDimensionName())) { queries.add(solrName + ":" + query); } }
		 * else { fields.add(solrName); }
		 * 
		 * if (!globalLimitSet && !facetLimitNegOne) { int limit = EndecaSolrUtilities.isDimensionExpanded(enequery,
		 * dim.getDimensionName()) ? maxFacetsToReturn : 1; setParam(solrquery, ("f." + solrName + ".facet.limit"), "" +
		 * limit, true); } if (!dim.isLexSort() && !enequery.isSortUnexpandedFacetsByCount()) { setParam(solrquery,
		 * ("f." + solrName + ".facet.sort"), "count", false); } else if (!enequery.isBr003Facets() && !facetLimitNegOne
		 * && enequery.isSortUnexpandedFacetsByCount() && EndecaSolrUtilities.isDimensionExpanded(enequery,
		 * dim.getDimensionName(), false)) { setParam(solrquery, ("f." + solrName + ".facet.sort"), "lex", false);
		 * setParam(solrquery, ("f." + solrName + ".facet.limit"), "-1", false); } } }
		 */
	}

	private String escapeMetacharacters(String in) {
		Matcher searchTermMatcher = escapeCharPattern.matcher(in);
		StringBuffer searchTermBuilder = new StringBuffer();
		while (searchTermMatcher.find()) {
			searchTermMatcher.appendReplacement(searchTermBuilder, "\\\\$0");
		}
		searchTermMatcher.appendTail(searchTermBuilder);
		return searchTermBuilder.toString();
	}

	private SchemaEntry getSchemaEntry(String searchKey, SearchBuildObject sbo) {

		SchemaEntry schemaEntry = SearchConfiguration.getSearchableFieldByEndecaName(searchKey, true);
		if (schemaEntry == null) {
			try {
				throw new SearchException(searchKey + " is not configured for Search (even when considering Search Interfaces).");
			} catch (SearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!schemaEntry.isSearchInterface()) {
			return schemaEntry;
		}
		if (sbo.searchInterfaceToUse == null) {
			sbo.searchInterfaceToUse = schemaEntry;
			return schemaEntry;
		}
		if (sbo.searchInterfaceToUse.equals(schemaEntry)) {
			return schemaEntry;
		}
		if (sbo.searchInterfaceToUse.getSolrName().equals(schemaEntry.getaFineSubstitution())) {
			return sbo.searchInterfaceToUse;
		}

		schemaEntry = SearchConfiguration.getSearchableFieldByEndecaName(searchKey, false);
		if (schemaEntry != null) {
			return schemaEntry;
		}

		try {
			throw new SearchException(searchKey + " is not configured for Search (Omitted search interfaces).");
		} catch (SearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return schemaEntry;

	}

	private String testFilterinQ(String filterString) {

		StringBuilder filterQ = new StringBuilder();
		String[] filters = pipePat.split(filterString);
		List<String> rfl = new LinkedList<String>();
		for (int i = 0; i < filters.length; i = i + 2) {
			String filter = null;
			if (filters.length > i + 1) {
				filter = filters[i] + "|" + filters[i + 1];
			} else {
				filter = filters[i];
			}
			rfl.add(filter);
		}

		for (String filter : rfl) {

			Matcher m = rangeFilterPattern.matcher(filter);
			boolean valid = true;
			SchemaEntry schemaEntry = null;
			String from = "*";
			String to = "*";
			String operator = null;
			if (m.find() && m.groupCount() >= 3) {
				String field = m.group(1);
				schemaEntry = getSchemaEntry(field, sbo);
				operator = m.group(2).toLowerCase();
				String operand = m.group(3);
				if ("null".equals(operand)) {
					operand = "*";
				}

				if ("lt".equals(operator)) {
					to = bumpValue(operand, schemaEntry, true);
				} else if ("lteq".equals(operator)) {
					to = operand;
				} else if ("gt".equals(operator)) {
					from = bumpValue(operand, schemaEntry, false);
				} else if ("gteq".equals(operator)) {
					from = operand;
				} else if ("btwn".equals(operator) && m.groupCount() == 4) {
					from = operand;
					to = "null".equals(m.group(4)) ? "*" : m.group(4);
				} else {
					valid = false;
				}
			} else {
				valid = false;
			}

			if (!valid) {
				try {
					throw new SearchException("Invalid Range Filter: " + filterString);
				} catch (SearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (!sbo.searchFirstTime) {
					filterQ.append(" AND ");
				}
				filterQ.append("(");
				if (!schemaEntry.isSearchInterface()) {
					filterQ.append(schemaEntry.getSolrName()).append(":");
				}
				filterQ.append("[").append(from).append(" TO ").append(to).append("]");
				filterQ.append(")");
				sbo.searchFirstTime = false;
			}
		}
		System.out.println("range filter: " + filterQ.toString());
		return filterQ.toString();

	}
	
	public static void testEanRemovals(){
		String startQry = "q=9781289072742+OR+9781494958244+OR+9783656546412+OR+9781456456726+OR+9781578861651+OR+9780789747884+OR+9781289223250+OR+9781591020189+OR+9781494945732+OR+9781249891161+OR+9783639140859+OR+9781480017290+OR+9780838999400+OR+9780838999417+OR+9783639230215+OR+9783659292736+OR+9781424155552+OR+9781463448660+OR+9781463448677+OR+9781742441412+OR+9781486431540+OR+9781283816052+OR+9781484954287+OR+9780984564507&facet=true&facet.missing=false&facet.mincount=1&facet.sort=count&facet.limit=6&facet.field=Format&facet.field=Binding&facet.field=Media&facet.field=Features&facet.field=Language&facet.query=Price%3A%5B*+TO+9.99%5D&facet.query=Price%3A%5B10+TO+20%5D&facet.query=Price%3A%5B20+TO+35%5D&facet.query=Price%3A%5B35+TO+50%5D&facet.query=Price%3A%5B50+TO+*%5D&start=0&rows=25&fq=%28ITEM_OHND%3AY+AND+%28ITEM_OHND_QTY_CI%3A%5B2+TO+*%5D+OR+ITEM_OHND_QTY_DD%3A%5B2+TO+*%5D+OR+ITEM_OHND_QTY_EE%3A%5B2+TO+*%5D+OR+ITEM_OHND_QTY_NV%3A%5B2+TO+*%5D%29%29&sort=Title_Exact+asc%2CTTL_ID+asc&fl=ALIBRIS_IND%2CALT_EAN%2CBinding%2CBSAC_DN1%2CBSAC_DN2%2CBSAC_DN3%2CCIS_AUTHAFFILDESC%2CCIS_BOOK_TYPE%2CCIS_COUNTRIES_EXCLUDED%2CCIS_COUNTRIES_INCLUDED%2CCIS_COUTTSNUMBER%2CCIS_DATEADDED%2CCIS_EBP_CATPARTNER%2CCIS_EBP_CURRENCY%2CCIS_EBP_DOWNLOADABLE%2CCIS_EBP_DOWNLOADDETAILS%2CCIS_EBP_EBSCOCATEGORY%2CCIS_EBP_EBSCOLEVEL%2CCIS_EBP_LICENCE%2CCIS_EBP_MILACCESS%2CCIS_EBP_PRICE%2CCIS_EBP_TITLEID%2CCIS_GEOGRAPHIC_AREA%2CCIS_NATCURRICULUM%2CCIS_NOTES%2CCOUTTS_DIVISION_APPROVAL_DATE%2CCOUTTS_DIVISION_CODE%2CCTBR%2CCTBR_ID%2CCTBR_ID_NRTV%2CCTBR_ROLE%2CData_Source%2CDEWEY_DN2%2CDEWEY_DN3%2CEAN%2CEdition%2CEXCP_IND%2CFeatures%2CFormat%2CIMPT_ID%2CISBN%2CISSN_ID%2CITEM_CST_DISC_CD%2CITEM_CST_RTL_AMT%2CITEM_INTL_DISC_CD%2CITEM_LIB_DISC_CD%2CITEM_LIB_RTL_AMT%2CITEM_OHND_QTY_AA%2CITEM_OHND_QTY_BB%2CITEM_OHND_QTY_CI%2CITEM_OHND_QTY_DD%2CITEM_OHND_QTY_EE%2CITEM_OHND_QTY_FF%2CITEM_OHND_QTY_HH%2CITEM_OHND_QTY_JJ%2CITEM_OHND_QTY_NV%2CITEM_OORD_QTY_AA%2CITEM_OORD_QTY_BB%2CITEM_OORD_QTY_CI%2CITEM_OORD_QTY_DD%2CITEM_OORD_QTY_EE%2CITEM_OORD_QTY_FF%2CITEM_OORD_QTY_HH%2CITEM_OORD_QTY_JJ%2CITEM_OORD_QTY_NV%2CITEM_PRE_BOOK_DT%2CITEM_RTL_STK_CD%2CLanguage%2CMedia%2CMYILIBRARY_ID%2CNRTV_PRI_IND%2CNRTV_RV_IND%2CPRI_SERS%2CPrice%2CPROD_ID%2CPUB_AVAL_STAT%2CPublisher%2CReading_Program%2CSeries%2CSERS_ID%2CSERS_ISSU_NBR%2CSJCG_DEWY_DEC_NBR%2CSRC_CITA_DN%2CSRC_CITA_RCMD_CD%2CSRC_CITA_ID%2CSRC_CITA_PUB_DT%2CSRC_CITA_PAGE_NBR%2CCITA_IMGE_LOGO_AVAL_IND%2CTHEM_DN1%2CTHEM_DN2%2CTHEM_DN1%2CTHEM_DN2%2CTitle%2CTOC_IND%2CTTL_ARTC_LDNG_NM%2CTTL_FWRD_ID%2CTTL_HGT_MEAS%2CTTL_ID%2CTTL_IMGE_IND%2CTTL_PUB_DT%2CTTL_THCK_MEAS%2CTTL_WDTH_MEAS%2CTTL_WT_MEAS&timeAllowed=10000";
		String [] nrEans = {"9781289072742","9781494958244","9783656546412","9781456456726","9780984564507"};
				
		String eans = "9781289072742 OR 9781494958244 OR 9783656546412 OR 9781456456726 OR 9781578861651 OR 9780789747884 OR 9781289223250 OR 9781591020189 OR 9781494945732 OR 9781249891161 OR 9783639140859 OR 9781480017290 OR 9780838999400 OR 9780838999417 OR 9783639230215 OR 9783659292736 OR 9781424155552 OR 9781463448660 OR 9781463448677 OR 9781742441412 OR 9781486431540 OR 9781283816052 OR 9781484954287 OR 9780984564507";
		System.out.println("eans: " + eans);
		
		
		//String [] nrEans = {"9781289072742","9781494958244","9783656546412","9781456456726","9780984564507","9781289223250 "};
		//new SolrQueryReuseService().removeEansFromSearchQry(solrQuery, Arrays.asList(nrEans));
		//System.out.println("eans: " + solrQuery.getQuery());
		
		for (String ean : nrEans) {
			if (StringUtils.contains(eans, ean + " OR ")) {
				eans = StringUtils.replace(eans, ean + " OR ", "");
			} else if (StringUtils.contains(eans, " OR " + ean)) {
				eans = StringUtils.replace(eans, " OR " + ean, "");
			}
		}

		System.out.println("eans: " + eans);
		
		//try {
			//SolrQuery qry = new SolrQueryReuseService().removeEansFromSearchQry(startQry, Arrays.asList(nrEans));
			
			//System.out.println("startQry: " + startQry);
			//System.out.println("finalQry: " + qry);
		//} catch (SearchException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
	}

	public static void main(String[] args) {
		//TestSolrQuery hdl = new TestSolrQuery();
		//hdl.loadSchema();

		//SolrQuery solrquery = new SolrQuery();

		//hdl.getQt("Title_SI", solrquery, hdl.sbo);
		// hdl.getQ(solrquery, hdl.sbo);
		// hdl.sbo.setFilterQuery(hdl.testFilterinQ("TOT_DSRB_CNT|GTEQ 564"));

		// hdl.parseRangeSearches(hdl.sbo);
		// System.out.println("hdl.sbo: " + hdl.sbo.toString());
		// System.out.println(solrquery.toString());

		//hdl.playDims();
		
		testEanRemovals();

	}

}
