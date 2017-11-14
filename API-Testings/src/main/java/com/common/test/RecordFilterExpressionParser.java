package com.common.test;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import solr.api.SchemaEntry;
import solr.api.SearchConfiguration;
import solr.api.SearchException;
import solr.api.endeca.EndecaDimensionValue;
import solr.api.endeca.NValueRepository;
import solr.api.endeca.RecordFilterElement;
import solr.api.endeca.RecordFilterElement.Operator;

public class RecordFilterExpressionParser {
	private static final Pattern PARENTHESIS_PATTERN = Pattern.compile("[\\(\\)]");
	private static final Pattern ELEMENT_PATTERN = Pattern.compile("(?:(AND|OR|NOT)?\\(|\\))");
	private static final Pattern COMMA_PATTERN = Pattern.compile("\\s*,\\s*");
	private static final Pattern COLON_PATTERN = Pattern.compile("[:]");
	private static final Pattern ESCAPE_CHARACTER_PATTERN = Pattern.compile("[\"*+\\-&|!(){}\\[\\]\\^~?:\\\\]");

	public RecordFilterExpressionParser() {
	}

	public String decode(RecordFilterElement root) {
		StringBuilder expr = new StringBuilder();
		addDecodeElement(root, expr);
		
		return expr.toString();
	}

	private void addDecodeElement(RecordFilterElement e, StringBuilder sb) {
		if (RecordFilterElement.Operator.NOT.equals(e.getOperator())) {
			sb.append('-');
		}
		if (e.getOperator() != null) {
			sb.append('(');
		}
		boolean gotOne = false;
		if (e.getOperand() != null) {
			if (!RecordFilterElement.Operator.NOT.equals(e.getOperator())) {
				sb.append(e.getOperand());
			}
			gotOne = true;
		}

		for (RecordFilterElement e1 : e.getComponents()) {
			if (gotOne && e.getOperator() != null) {
				sb.append(' ').append(Operator.NOT.equals(e.getOperator()) ? "AND" : e.getOperator().toString()).append(' ');
			}
			addDecodeElement(e1, sb);
			gotOne = true;
		}
		if (e.getOperator() != null) {
			sb.append(')');
		}
	}

	public RecordFilterElement parse(String expr) throws SearchException {
		Stack<RecordFilterElement> stack = new Stack<RecordFilterElement>();
		RecordFilterElement root = new RecordFilterElement();
		stack.push(root);

		Matcher parenthesisMatch = PARENTHESIS_PATTERN.matcher(expr);
		if (!parenthesisMatch.find()) {
			expr = "(" + expr + ")";
		}

		Matcher exprMatch = ELEMENT_PATTERN.matcher(expr);
		int lastMatchEndIndex = 0;
		while (exprMatch.find()) {
			if (exprMatch.group(0).endsWith("(")) {
				RecordFilterElement element = new RecordFilterElement();
				if (exprMatch.groupCount() > 0) {
					if ("AND".equals(exprMatch.group(1))) {
						element.setOperator(RecordFilterElement.Operator.AND);
					} else if ("OR".equals(exprMatch.group(1))) {
						element.setOperator(RecordFilterElement.Operator.OR);
					} else if ("NOT".equals(exprMatch.group(1))) {
						element.setOperator(RecordFilterElement.Operator.NOT);
					}
				}
				RecordFilterElement parent = stack.peek();
				lastMatchEndIndex = fillInTokens(lastMatchEndIndex, exprMatch, expr, stack, parent);
				stack.push(element);
			} else if (exprMatch.group(0).endsWith(")")) {
				if (stack.size() < 2) {
					throw new RuntimeException("Too many closing parenthesis in expression: " + expr);
				}
				RecordFilterElement element = stack.pop();
				RecordFilterElement parent = stack.peek();
				parent.addComponent(element);
				lastMatchEndIndex = fillInTokens(lastMatchEndIndex, exprMatch, expr, stack, element);
			}
		}
		if (lastMatchEndIndex == 0) {
			root.setOperand(expr);
		}

		if (stack.size() > 1) {
			throw new RuntimeException("Too many opening parenthesis in expression: " + expr);
		}
		System.out.println("root: " + root.toString());
		return root;
	}

	private int fillInTokens(int lastMatchEndIndex, Matcher exprMatch, String expr, Stack<RecordFilterElement> stack, RecordFilterElement appendToElement) throws SearchException {
		int matchIndex = exprMatch.start();
		if (matchIndex - 1 > lastMatchEndIndex) {
			String guts = expr.substring(lastMatchEndIndex, matchIndex);
			String[] elementTokens = COMMA_PATTERN.split(guts);
			
			for (String elementToken : elementTokens) {
				if (elementToken.length() > 0) {
					RecordFilterElement child = new RecordFilterElement();
					long elementTokenLong = Long.MIN_VALUE;
					try {
						elementTokenLong = Long.parseLong(elementToken);
					} catch (Exception e) {
						// do nothing
					}

					if (elementTokenLong > Long.MIN_VALUE) {
						// If the Record Filter contained an Endeca Dimension N-Value, translate the N-Value to the
						// actual Solr Field Name
						EndecaDimensionValue endecaDimVal = NValueRepository.findDimensionValue(elementTokenLong);
						if (endecaDimVal == null) {
							throw new SearchException("Endeca N-Value: " + elementTokenLong + " is used in a record filter but is not in the N-Value Repository.");
						}

						String value = escapeValue(endecaDimVal.getValue());
						if (value.contains(" ")) {
							value = "\"" + value + "\"";
						}
						SchemaEntry entry = SearchConfiguration.getFacetableFieldByEndecaName(endecaDimVal.getDimensionName());
						if (entry == null) {
							throw new SearchException(endecaDimVal.getDimensionName()
									+ " is configured as an Endeca Dimension and is Used in a Record Filter.  Yet it is not configured in Solr for Faceting.");
						}
						child.setOperand(entry.getSolrName() + ":" + value);
					}//if (elementTokenLong > Long.MIN_VALUE)
					else {
						// If this is an Endeca Field Name, translate that to the Solr Field Name
						String[] tokens = COLON_PATTERN.split(elementToken);
						String endecaFieldName = tokens[0];
						String filterValue = null;
						if (tokens.length == 2 && tokens[1].trim().length() > 0) {
							filterValue = escapeValue(tokens[1]);
						} else {
							filterValue = "[* TO *]";
						}
						SchemaEntry entry = SearchConfiguration.getFacetableFieldByEndecaName(endecaFieldName);
						if (entry == null) {
							throw new SearchException(endecaFieldName + " is Used in a Record Filter but not configured in Solr for Faceting.");
						}
						child.setOperand(entry.getSolrName() + ":" + filterValue);
					}
					appendToElement.addComponent(child);
				}
			}
		}
		lastMatchEndIndex = exprMatch.end();
		return lastMatchEndIndex;
	}

	private String escapeValue(String inVal) {
		Matcher searchTermMatcher = ESCAPE_CHARACTER_PATTERN.matcher(inVal);
		StringBuffer searchTermBuilder = new StringBuffer();
		while (searchTermMatcher.find()) {
			searchTermMatcher.appendReplacement(searchTermBuilder, "\\\\$0");
		}
		searchTermMatcher.appendTail(searchTermBuilder);
		return searchTermBuilder.toString();
	}
}
