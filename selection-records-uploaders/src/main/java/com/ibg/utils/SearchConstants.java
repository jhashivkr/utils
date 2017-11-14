package com.ibg.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public interface SearchConstants {
	public static final Pattern spacePattern = Pattern.compile("\\s+");
	public static final Pattern nonDigitPattern = Pattern.compile("[\\D]");
	public static final Pattern singleSpacePattern = Pattern.compile("\\s");
	public static final Pattern andOrNotPattern = Pattern.compile("(?i)\\b(and|or|not)\\b");
	public static final Pattern onePipePattern = Pattern.compile("\\|");
	public static final Pattern twoPipesPattern = Pattern.compile("\\|{2}");
	public static final Pattern escapeCharPattern = Pattern.compile("[+\\-&|!(){}\\[\\]\\^~?:\\\\]");
	public static final Pattern quotePattern = Pattern.compile("[\"]");
	public static final Pattern wildcardPattern = Pattern.compile("[*]");
	public static final Pattern multiDimensionSplitPattern = Pattern.compile("\\s\\|\\s");
	public static final Pattern rangeFilterPattern = Pattern
			.compile("(?i)^(.*)[\\|](lt|gt|lteq|gteq|btwn)\\s?[\\+|\\s]([a-z0-9.]+)(?:\\s?[\\+|\\s]([a-z0-9.]+))?(?:\\|)?$");
	public static final Pattern citationIdPattern = Pattern.compile("^__(\\d+)$");
	public static final Pattern floatingPunctuationPattern = Pattern.compile("(?:^|\\s)[^A-Za-z0-9_ ](?:\\s|$)");
	public static final Pattern dimListPat = Pattern.compile("(?:\\+|\\s)+");
	public static final Pattern pipePat = Pattern.compile("\\|");
	public static final Pattern PARENTHESIS_PATTERN = Pattern.compile("[\\(\\)]");
	public static final Pattern ELEMENT_PATTERN = Pattern.compile("(?:(AND|OR|NOT)?\\(|\\))");
	
	public static final Pattern COLON_PATTERN = Pattern.compile("[:]");
	public static final Pattern ESCAPE_CHARACTER_PATTERN = Pattern.compile("[\"*+\\-&|!(){}\\[\\]\\^~?:\\\\]");
	public static final String endecaTitleStripChars = "@;#+\"':./\\,()!?=*%~_<>{}|`^-[]";
	public static final Pattern nonSearchablePattern = Pattern.compile("[^\\w\\&*#$+%]");
	public static final Pattern nonAlphaNumeric = Pattern.compile("[^A-Za-z0-9]");
	public static final Pattern nsoPat = Pattern.compile("Nso=(.*?)(?:&|\\s|\\z)");
	public static final Pattern nsPat = Pattern.compile("Ns=(.*?)(?:&|\\s|\\z)");
	public static final Pattern ampPat = Pattern.compile("&");
	public static final Pattern eqPat = Pattern.compile("=");

	public static final Pattern filterRecordPattern = Pattern.compile("\\s*&#x1e\\s*");
	public static final Pattern filterDataPattern = Pattern.compile("\\s*&#x1f\\s*");
	public static final Pattern oneHashPattern = Pattern.compile("\\#");
	public static final Pattern oneStarPattern = Pattern.compile("\\*");

	public static SimpleDateFormat cis_df = new SimpleDateFormat("MM/dd/yy");
	public static SimpleDateFormat db_df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
	public static final Pattern acdmListPattern = Pattern.compile("\\s*acdm_list_disp\\s*");
	public static final Pattern oneTildePattern = Pattern.compile("\\s*~\\s*");
	public static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");

}
