package com.common.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

	// CS-7672 - filter out all the html tags from the text value
	private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", Pattern.CASE_INSENSITIVE
			| Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern HTML_TAG_PATTERN_EXTRA = Pattern.compile("\\<.*?\\>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
	
	private static final Pattern DATE_MM_DD_YYYY = Pattern.compile("^(0[1-9]|1[0-2])\\/(0[1-9]|1\\d|2\\d|3[01])\\/(19|20)\\d{2}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
	//private static final Pattern DATE_MM_YYYY = Pattern.compile("^(0[1-9]|1\\d|2\\d|3[01])\\/(19|20)\\d{2}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
	private static final Pattern DATE_MM_YYYY = Pattern.compile("^(0[1-9]|1\\d|2\\d|3[01])\\/\\d{4}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
	
	 
	public static String[] dateMMDDYYYYTagParser(String dataString) {

		Matcher matcher = DATE_MM_DD_YYYY.matcher(dataString);
		if (!matcher.find()) {
			return null;
		}

		return dataString.split("\\/");
	}
	
	public static String[] dateMMYYYYTagParser(String dataString) {

		Matcher matcher = DATE_MM_YYYY.matcher(dataString);
		if (!matcher.find()) {
			return null;
		}

		return dataString.split("\\/");
	}
	
	
	public static String htmlTagFilter(String dataString) {

		Matcher matcher = HTML_TAG_PATTERN.matcher(dataString);
		if (!matcher.find()) {
			return dataString;
		}

		StringBuffer gsb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(gsb, "".intern());
		}

		return matcher.appendTail(gsb).toString().replaceAll(HTML_TAG_PATTERN_EXTRA.pattern(), "");
	}
	
	public static String cleanStringWithRpttiveWords(String input, String word){
		Pattern AND_PATTERN = Pattern.compile("\\b[" + word + "]+\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
		
		StringBuffer gsb = new StringBuffer();
		Matcher matcher = AND_PATTERN.matcher(input);

		if (!matcher.find()) {
			System.out.println("No Match Found");
		} else {

			while (matcher.find()) {
				matcher.appendReplacement(gsb, "");
				if(!matcher.hitEnd()){
					gsb.append(word);
				}
				
			}
		}
		
		return matcher.appendTail(gsb).toString();
	}
}
