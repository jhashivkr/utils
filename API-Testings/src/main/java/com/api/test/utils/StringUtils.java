// ================================================================================
//
//	 Title: StringUtils.java
//
//	 Created on Dec 20, 2007
//	 
//	 Copyright(c) 2007 Ingram Digital
//
//	 ================================================================================

package com.api.test.utils;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * A class to encapsulate static utilities for working with Strings
 * 
 */
public class StringUtils
{
		
	/**
	 * Method to check a String value to be sure it is not null. Generally
	 * utilized with values returned from ResultSets
	 * 
	 * @param value
	 *          String value to check
	 * @return true if value is not null or not equal to "null"
	 */
	public static boolean notNull( String value )
	{
		boolean status = false;
		if ( value != null && !value.equals( "null" ) )
		{
			status = true;
		}
		return status;
	}

	/**
	 * Method to check a String value to be sure it is null. Generally utilized
	 * with values returned from ResultSets
	 * 
	 * @param value
	 *          String value to check
	 * @return true if value is null or equal to "null"
	 */
	public static boolean isNull( String value )
	{
		return !notNull( value );
	}

	/**
	 * Method to convert a Java object to an XML representation
	 * 
	 * @param o
	 *          Object to convert to XML
	 * @return String XML representation of the object
	 */
	public static String toXML( Object o )
	{
		String retStr = null;
		if ( o != null )
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLEncoder encoder = new XMLEncoder( bos );
			encoder.writeObject( o );
			encoder.flush();
			encoder.close();
			retStr = bos.toString();
		}
		return retStr;
	}
	
	/**
	 * Method isValidString
	 * <p>
	 * @param value
	 * @return boolean true or false
	 */
	public static boolean isValidString(String value)
	{
	  boolean status = false;
	  
	  if ( !isNull( value ))
	  {
	  	if ( value.length() > 0 )
	  	{
	  		status = true;
	  	}
	  }
	  
	  return status; 
	}
	
	/**
	 * Method rightPadString will pad the right side of a String to the length
	 * specified with space characters
	 * <p>
	 * 
	 * @param input
	 *          the original value
	 * @param length
	 *          the lengt to pad to
	 * @return String
	 *         <p>
	 */
	public static String rightPadString( String input, int length )
	{
		return rightPadString(input, length, " ");
	}
	
	/**
	 * Method rightPadString
	 * <p>
	 * @param input
	 * @param length
	 * @param padChar
	 * @return
	 * <p>
	 */
	public static String rightPadString(String input, int length, String padChar)
	{
		StringBuilder strBuf = new StringBuilder( length );
		if ( input == null )
		{
			input = "";
		}

		strBuf.append( input );

		for ( int index = input.length(); index < length; index++ )
		{
			strBuf.append( padChar );
		}

		if ( padChar != null && padChar.length() == 1)
		{
			strBuf.setLength( length );
		}

		return strBuf.toString();
	}

	
	
	/**
	 * Method leftZeroPadString will pad a String to the length specified with
	 * leading zeroes
	 * <p>
	 * 
	 * @param input
	 *          the input String
	 * @param length
	 *          the length we want to pad to
	 * @return String
	 *         <p>
	 *         Creation date: Feb 20, 2008
	 */
	public static String leftZeroPadString( String input, int length )
	{
		StringBuilder strBuf = new StringBuilder( length );

		if ( input == null )
		{
			input = "";
		}

		for ( int index = input.length(); index < length; index++ )
		{
			strBuf.append( '0' );
		}

		strBuf.append( input );
		strBuf.setLength( length );

		return strBuf.toString();
	}

	/**
	 * Method isNumeric is kludgy as hell, but it works
	 * <p>
	 * 
	 * @param input
	 *          the input string to check
	 * @return true means it is numeric,
	 *         <p>
	 *         Creation date: Jan 11, 2008
	 */
	public static boolean isNumeric( String input )
	{
		boolean result = false;
    
		try
		{
			Double.parseDouble( input );
			result = true;
		}
		catch ( NumberFormatException e )
		{

		}
		return result;
	}

	/**
	 * Method isYes returns true if the input string is 'yes'
	 * <p>
	 * 
	 * @param input
	 *          Input String
	 * @return true if input.equalsIgnoreCase("yes")
	 *         <p>
	 */
	public static boolean isYes( String input )
	{
		boolean status = false;
		if ( input != null )
		{
			if ( input.toUpperCase().equals( "YES" ) )
			{
				status = true;
			}
		}
		return status;
	}

	/**
	 * Method isNo returns true if the input string is 'No', 'NO', 'no'
	 * <p>
	 * 
	 * @param input
	 *          Input String
	 * @return true if input.equalsIgnoreCase("no")
	 *         <p>
	 */
	public static boolean isNo( String input )
	{
		boolean status = false;
		if ( input != null )
		{
			if ( input.toUpperCase().equals( "NO" ) )
			{
				status = true;
			}
		}
		return status;
	}

	/**
	 * Method isTrue returns true if the input string is 'True', 'TRUE', 'true'
	 * <p>
	 * 
	 * @param input
	 *          Input String
	 * @return true if input.equalsIgnoreCase("true")
	 *         <p>
	 */
	public static boolean isTrue( String input )
	{
		boolean status = false;
		if ( input != null )
		{
			if ( input.toUpperCase().equals( "TRUE" ) )
			{
				status = true;
			}
		}
		return status;
	}

	/**
	 * Method isFalse returns true if the input string is 'False', 'FALSE',
	 * 'false'
	 * <p>
	 * 
	 * @param input
	 *          Input String
	 * @return true if input.equalsIgnoreCase("false")
	 *         <p>
	 */
	public static boolean isFalse( String input )
	{
		boolean status = false;
		if ( input != null )
		{
			if ( input.toUpperCase().equals( "FALSE" ) )
			{
				status = true;
			}
		}
		return status;
	}

	/**
	 * Method containsInvalidCharacters, will return true if there is a character
	 * within the String that is not within the ASCII range of ' ' (space) to '~'
	 * (tilde)
	 * <p>
	 * 
	 * @param input
	 *          String to check
	 * @return true if it contains a non-printable character.
	 *         <p>
	 *         Creation date: May 28, 2008
	 */
	public static boolean containsInvalidCharacters( String input )
	{
		boolean status = false;

		if ( input != null )
		{

			char[] cArray = input.toCharArray();
	
			for ( char cValue : cArray )
			{
				// --- TODO, this doesn't look right....
				if ( cValue < ' ' || cValue > '~' )
				{
					status = true;
					break;
				}
			}
		}

		return status;
	}

	/**
	 * Method containsInvalidFileNameCharacters will return true if the file name in question
	 * has invalid characters in it for some file systems.  Windows is not happy with "._" or 
	 * those that have "#" or "~"
	 * <p>
	 * @param input
	 * @return
	 * <p>
	 * Creation date: Mar 16, 2009
	 */
	public static boolean containsInvalidFileNameCharacters( String input )
	{
		boolean status = false;

		if ( input != null )
		{
			if(input.contains( "._" ))
			{
				return true;
			}

			char[] cArray = input.toCharArray();
	
			for ( char cValue : cArray )
			{
				// --- TODO, this doesn't look right....
				if ( cValue < ' ' || cValue > '~' || cValue == '*' || cValue == '#')
				{
					status = true;
					break;
				}
			}
		}

		return status;
	}
	
	
	/**
	 * Method getTitleWithoutArticle
	 * <p>
	 * 
	 * @param input
	 * @return
	 *          <p>
	 *          Creation date: Jun 11, 2008
	 */
	public static String getTitleWithoutArticle( String input )
	{
		String retStr = null;

		if ( input != null )
		{
			String article = getBeginningArticleFromTitle( input );
			if ( article != null && input.length() > (article.length() + 1))
			{
				retStr = input.substring( article.length() + 1 );
			}
			else
			{
				retStr = input;
			}
		}
		else
		{
			retStr = input;
		}
		
		return retStr;
	}

	/**
	 * Method getBeginningArticleFromTitle
	 * <p>
	 * 
	 * @param input
	 * @return
	 *          <p>
	 *          Creation date: Jun 11, 2008
	 */
	public static String getBeginningArticleFromTitle( String input )
	{
		String [] articleArray = 
		{
				"A ",
				"AN ",
				"THE ",
				"EL ",
				"LA ",
				"LOS ",
				"LE ",
				"LES ",
				"DAS ",
				"DER "
//				"DIE ",
		};
		String retStr = null;
		if ( input != null )
		{
			String tempStr = input.toUpperCase();
			for ( String article : articleArray )
			{
				if ( tempStr.startsWith( article ))
				{
					if ( input.length() > article.length() )
					{
						retStr = input.substring( 0, article.length() - 1 );
					}
					break;
				}
			}
		}
		return retStr;

	}

	/**
	 * Returns a delimiter separated string for the given Collection.
	 * 
	 * @param list List of Strings 
	 * @param delim Delimiter for the output String
	 * @return a String separated by delimiter
	 */
	public static String join( List<String> list, String delim )
	{
		return buildListString(list, delim);
	}
	
	/**
	 * Returns a delimiter separated string for the given Collection.
	 * 
	 * @param list List of Strings 
	 * @param delim Delimiter for the output String
	 * @param lastDelim Delimiter for the output String between second to last and last in the list
	 * @return a String separated by delimiter
	 */
	public static String join( List<String> list, String delim, String lastDelim )
	{
		return buildListString(list, delim, lastDelim);
	}
	
	/**
	 * Method buildListString
	 * <p>
	 * @param workList
	 * @param delimiter
	 * @return
	 * <p>
	 * Creation date: Mar 19, 2009
	 */
	public static String buildListString(List<String> workList, String delimiter)
	{
		return buildListString(workList, delimiter, null);
	}
	/**
	 * Method buildListString
	 * <p>
	 * @param workList
	 * @param delimiter
	 * @return
	 * <p>
	 * Creation date: Mar 19, 2009
	 */
	public static String buildListString(List<String> workList, String delimiter, String lastDelimiter)
	{
		String retStr = null;
		StringBuilder strBuf = new StringBuilder(300);
		boolean firstTime = true;
		if ( delimiter == null )
		{
			delimiter = " ";
		}
		
		if ( workList != null )
		{
			int count = 1;
			for( String workStr : workList )
			{
				if ( workStr != null && workStr.length() > 0 && !" ".equals( workStr ))
				{
					if ( !firstTime )
					{
						if (count == workList.size() && lastDelimiter!=null) { // if this is the last element, use last delimiter
							strBuf.append(lastDelimiter);
						} else {
							strBuf.append(delimiter);
						}
					}
					else
					{
						firstTime = false;
					}
					strBuf.append(workStr);
					count++;
				}
			}
		}
		
		if ( strBuf.length() != 0 )
		{
			retStr = strBuf.toString();
		}
		
		return retStr;
	}
	
	
	/**
	 * Method fixPaths
	 * <p>
	 * @param pathString
	 * @return
	 * <p>
	 * Creation date: Nov 17, 2008
	 */
	public static String fixPaths(String pathString)
	{
		String retStr = null;
		
		if ( pathString != null )
		{
			if ( System.getProperty( "os.name" ) != null &&
					 System.getProperty( "os.name" ).equalsIgnoreCase( "Linux" ))
			{
				// --- Linux processing here
				retStr = pathString.replaceAll( "\\\\", "/" );
			}
			else
			{
				// --- windows processing here
				retStr = pathString.replaceAll( "/", "\\\\" );
			}
		}
		return retStr;
	}

	/**
	 * Method cleanHTML performs a brute force cleaning of HTML tags out of a string passed in
	 * <p>
	 * @param s  The string to process
	 * @return A String that has been "cleaned"
	 * <p>
	 * Creation date: Feb 4, 2009
	 */
	public static String cleanHTML( String s )
	{
		String retStr = null;
		if ( s != null )
		{
			if ( s.startsWith( "<![CDATA[" ))
			{
				s = s.substring( 9 );
			}
			if ( s.endsWith( "]]>" ))
			{
				s = s.substring( 0, s.length() - 3 );
			}
			StringBuilder strBuf = new StringBuilder( s.length() );
			StringBuilder tempBuf = new StringBuilder( s.length() );
	
			char c;
			int i;
			// Search through string's characters one by one.
			// If character is < then find closing > and remove all between
			// or could append to a returnString, depends which is faster, but i don't know right now
	
			int startTag = -1;
			int endTag = -1;
			for ( i = 0; i < s.length(); i++ )
			{
				c = s.charAt( i );
	
				if ( startTag == -1 )
				{
					// --- no start tag found yet
					if ( c == '<' )
					{
						// --- then it's there
						startTag = i;
					}
				}
				else
				{
					// --- we might be in a tag already....
					if ( c == '<' )
					{
						// --- encountered another starttag
						strBuf.append( tempBuf.toString() );
						tempBuf.delete( 0, tempBuf.length() );
						tempBuf.append( c );
						startTag = i;
					}
					// want to find end tag
					if ( c == '>' )
					{
						endTag = i;
					}
				}
	
				if ( startTag == -1 )
				{ // not in tag and no tag found yet
					strBuf.append( c );
				}
				else
				{
					if ( endTag != -1 )
					{
						tempBuf.delete( 0, tempBuf.length() );
						// end tag found, so skip this char and reset
						startTag = -1; // reset
						endTag = -1;
					}
					else
					{
						tempBuf.append( c );
					}
				}
	
			}
	
			if ( tempBuf.length() > 0 )
			{
				strBuf.append( tempBuf.toString() );
			}
	
			retStr =  strBuf.toString();
		}
		return retStr;
	}
	

	public static String convertAsciiHtmlToCharacters(String input) {
		
		String returnStr = new String(input);
		int indexOf = -1;
		String asciiDec = null;
		String htmlNumber = null;
		Character asciiChar = null;
		int startIndex = -1;
		int endIndex = -1;
		int ascii;
		while (returnStr.indexOf("&#", startIndex) != -1) {
			indexOf = returnStr.indexOf("&#", startIndex);
			if (indexOf != -1) {
				// where does the code end...
				endIndex = returnStr.indexOf(";",indexOf);
				if (endIndex != -1) {
					// ascii decimal code has beginning and ending markers
					asciiDec = returnStr.substring(indexOf, endIndex+1);
					htmlNumber = returnStr.substring(indexOf+2, endIndex);
					try {
						ascii = Integer.valueOf(htmlNumber).intValue();
						asciiChar = (char)ascii;
						if (asciiChar != null) {
							returnStr = returnStr.replace(asciiDec, asciiChar.toString());
						}
					} catch (NumberFormatException e) {
						// the characters in between "&#" and the next ";" do not compose a number.
					}
				}
				startIndex = indexOf+1;
			}
		}
		
		return returnStr;
	}
	
	/**
	 * Method getInitCapsString
	 * <p>
	 * This will take a String in mixed case, upper-case, lower-case and return a representation that is in initCaps with no spaces.
	 * For example "HOW I MET YOUR MOTHER" ==> "HowIMetYourMother" 
	 * <p>
	 * @param name
	 * @return String or null
	 * <p>
	 * Creation date: Mar 24, 2009
	 */
	public static String getInitCapsString(String name)
	{
		String retStr = null;
		if ( name != null )
		{
			if ( name.contains( " " ) )
			{
				String workStr = name.toLowerCase();
				StringBuilder strBuf = new StringBuilder(name.length());
				
				char c;
				boolean firstChar = true;
				boolean whiteSpace = false;
				
				// --- we are going to walk down this string, removing the spaces and capitalizing the initial characters in each word
				for ( int index = 0; index < workStr.length(); index++)
				{
					c = workStr.charAt( index );
					if ( Character.isWhitespace( c ))
					{
						whiteSpace = true;
					}
					if ( firstChar && !whiteSpace )
					{
						strBuf.append( Character.toUpperCase( c ) );
						firstChar = false;
					}
					else
					{
						if( whiteSpace )
						{
							if ( Character.isLetterOrDigit( c ) )
							{
								strBuf.append(Character.toUpperCase( c ));
								whiteSpace = false;
							}
						}  // --- if (whiteSpace )
						else
						{
							if ( Character.isLetterOrDigit( c ))
							{
								strBuf.append(c);
							}
						}  // --- if ( !whiteSpace )
					}  // --- if ( firstChar && whiteSpace )
				}  // --- for()
				retStr = strBuf.toString();
			}  // --- if(name.contains(" ")
			else
			{
				retStr = name;
			}  // --- if !(name.contains(" ")
		}  // --- if (name != null)
		return retStr;
	}
	
	/**
	 * Method getProperCaseString
	 * <p>
	 * This method will take strings in mixed case, with tabs/spaces, etc and put them into proper Case
	 * For example "HOW I MET YOUR      MOTHER" ==> "How I Met Your Mother" 
	 * <p>
	 * @param name
	 * @return String
	 * <p>
	 * Creation date: Mar 24, 2009
	 */
	public static String getProperCaseString(String name)
	{
		String retStr = null;
		
		if ( name != null )
		{
			if ( name.contains( " " ) )
			{
				String workStr = name.toLowerCase();
				StringBuilder strBuf = new StringBuilder(name.length());
				
				char c;
				boolean firstChar = true;
				boolean whiteSpace = false;
				
				// --- we are going to walk down this string, removing the spaces and capitalizing the initial characters in each word
				for ( int index = 0; index < workStr.length(); index++)
				{
					c = workStr.charAt( index );
					if ( Character.isWhitespace( c ))
					{
						if ( !whiteSpace )
						{
							whiteSpace = true;
							strBuf.append( c );
						}
					}
					if ( firstChar && !whiteSpace )
					{
						strBuf.append( Character.toUpperCase( c ) );
						firstChar = false;
					}
					else
					{
						if( whiteSpace )
						{
							if ( !Character.isWhitespace( c ) )
							{
								strBuf.append(Character.toUpperCase( c ));
								whiteSpace = false;
							}
						}  // --- if (whiteSpace )
						else
						{
							if ( !Character.isWhitespace( c ))
							{
								strBuf.append(c);
							}
						}  // --- if ( !whiteSpace )
					}  // --- if ( firstChar && whiteSpace )
				}  // --- for()
				retStr = strBuf.toString();
			}  // --- if(name.contains(" ")
			else
			{
				retStr = name;
			}  // --- if !(name.contains(" ")
		}  // --- if (name != null)
		
		
		return retStr;
	}
	
	/**
	 * Method isPunctuation
	 * <p>
	 * @param c
	 * @return
	 * <p>
	 * Creation date: May 12, 2009
	 */
	public static boolean isPunctuation(char c )
	{
		boolean status = false;
		
		if ( c == '.' 
			|| c == ','
			|| c == ';'
			|| c == '\''
			|| c == ':'
			|| c == '"'
			|| c == '!'
			|| c == '?'
		)
		{
			status = true;
		}
		return status;
	}
	
	
	/**
	 * Method removePunctuation
	 * <p>
	 * @param inStr
	 * @return
	 * <p>
	 * Creation date: May 12, 2009
	 */
	public static String removePunctuation(String inStr)
	{
		String retStr = null;
		
		if ( inStr != null)
		{
			char c;
			StringBuilder strBuf = new StringBuilder(inStr.length());
			for ( int index = 0; index < inStr.length(); index++)
			{
				c = inStr.charAt( index );
				if ( !isPunctuation(c) )
				{
					strBuf.append( c );
				}
				
			}// --- for()
			
			retStr = strBuf.toString().trim();
			
		}  // --- if ( inStr != null)
		
		
		
		return retStr;
	}
	
	/**
	 * Method getFileNameExtension
	 * <p>
	 * @param workStr
	 * @return
	 * <p>
	 * Creation date: Jul 14, 2009
	 */
	public static String getFileNameExtension(String workStr)
	{
		String retStr = null;
		
		if ( workStr != null)
		{
			int extIndex = workStr.lastIndexOf( "." );
			if ( extIndex != -1 )
			{
				retStr = workStr.substring(extIndex + 1);
			}
		}
		
		return retStr;
	}
	
	/**
	 * Method removeSpecialCharacters
	 * <p>
	 * @param str
	 * @return
	 * <p>
	 * Creation date: Jul 21, 2009
	 */
	public static String removeSpecialCharacters(String str) 
	{
		String retStr = null;
		
		if ( str != null )
		{
			String spChar = "[-{}/!@#$%^&_*`~()+=|:;',.<>?\"\\\\]";
			retStr = str.replaceAll(spChar, "");
		}
		
		return retStr;
	}
	
	/**
	 * Collapses white space ("  ", '\n', '\t', '\r') within the given string to single spaces.
	 * i.e. "this   is a\ntest\tof the    collapse method" collapses to "this is a test of the collapse method"
	 * @param str
	 * @param trim
	 * @return
	 */
	public static String collapseWhiteSpace(String str, boolean trim) {
		
		String retStr = str;
		
		if (retStr != null) {
			if (trim) {
				retStr = retStr.trim();
			}
			
			retStr = retStr.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
			
			while (retStr.contains("  ")) {
				retStr = retStr.replaceAll("  ", " ");
			}
		}
		
		return retStr;
	}
}
