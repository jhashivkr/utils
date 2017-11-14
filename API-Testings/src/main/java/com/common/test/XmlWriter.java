// ================================================================================
//
//	 Title: XmlWriter.java
//
//	 Created on Oct 30, 2009
//	 
//	 Copyright(c) 2008 Ingram Digital
//
//	 ================================================================================

package com.common.test;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.api.test.utils.StringUtils;

public class XmlWriter
{
  private StringBuilder strBuf;
  private StringBuilder attributes;
  private boolean empty;
  private boolean closed;
  private Stack<String> stack;
  
  private String lineFeed = System.getProperty( "line.separator" );
	
  
  public XmlWriter()
  {
  	strBuf = new StringBuilder(3000);
  	stack = new Stack<String>();
  	closed = true;
  }
  

  /**
   * Method closeOpeningTag closes the opening tag
   * <p>
   * <p>
   * Creation date: Oct 30, 2009
   */
  private void closeOpeningTag()
  {
  	if ( !closed )
  	{
  		writeAttributes();
  		strBuf.append( ">" );
  		closed=true;
  	}
  	
  }
  
  public XmlWriter writeEncoding(String encoding)
  
  {
  	if ( StringUtils.isValidString( encoding ))
  	{
        
  		strBuf.append( "<?xml version=\"1.0\" encoding=\"" )
  		          .append( encoding )
  		          .append( "\"?>" )
  		          .append( lineFeed );
  	}
  	
  	return this;
  }
  
  public XmlWriter writeDocType(String docType, String url)
  {
  	
  	if ( StringUtils.isValidString( docType ))
  	{
  		strBuf.append("<!DOCTYPE ")
  		      .append( docType )
  		      .append(  " " );
  	
	  	if ( StringUtils.isValidString( url ))
	  	{
	  		strBuf.append("\"")
	  		      .append( url )
	  		      .append("\">")
	  		      .append( lineFeed );
	  	}
  	}
  	return this;
  }
  
  /**
   * Method writeAttributes writes out all the attributes
   * <p>
   * <p>
   * Creation date: Oct 30, 2009
   */
  private void writeAttributes()
  {
  	if ( attributes != null )
  	{
  		strBuf.append(attributes.toString());
  		attributes.setLength( 0 );
  		empty = false;
  	}
  }
  
  public XmlWriter writeAttribute(String attribute, String value)
  {
  	if ( attribute != null )
  	{
	  	if ( attributes == null )
	  	{
	  		attributes = new StringBuilder(200);
	  	}
	  	
	  	attributes.append( " " )
	  	          .append( attribute )
	  	          .append("=\"")
	  	          .append(escapeXml( value ))
	  	          .append( "\"" );
  	} 	
  	
  	return this;
  }
  
  public XmlWriter writeText(String value)
  {
  	if ( value != null)
  	{
  		closeOpeningTag();
  		empty = false;
  		strBuf.append(escapeXml( value ));
  	}
  	
  	return this;
  }
  
  public XmlWriter writeTextNoEscape(String value)
  {
  	if ( value != null)
  	{
  		closeOpeningTag();
  		empty = false;
  		strBuf.append(value);
  	}
  	
  	return this;
  }
  
  public XmlWriter writeCDATA(String value)
  {
  	if ( value != null )
  	{
  		closeOpeningTag();
  		empty = false;
  		if (!value.toUpperCase().contains("<![CDATA[")) {
	  		strBuf.append("<![CDATA[")
	  		      .append( removeUnprintableCharacters(value) )
	  		      .append("]]>");
  		}
  		else {
	  		strBuf.append( removeUnprintableCharacters(value) );
  		}
  	}
  	
  	return this;
  }
  
  
  public XmlWriter writeEntity(String name)
  {
  	if ( empty )
  	{
  		closeOpeningTag();
  		strBuf.append(lineFeed);
  	}
  	
  	if ( name != null )
  	{
  		closeOpeningTag();
  		closed = false;
  		strBuf.append("<");
  		strBuf.append(name);
  		stack.add( name );
  		empty = true;
  	}
  	
  	return this;
  }
  
  public XmlWriter endEntity() throws XmlWriterException
  {
  	if ( stack.empty() )
  	{
  		throw new XmlWriterException("Called endEntity too many times");
  	}
  	
		String name = stack.pop();
		if (name != null )
		{
			if ( empty )
			{
				writeAttributes();
				strBuf.append("/>")
				      .append( lineFeed );
			}
			else
			{
				strBuf.append("</")
				      .append( name )
				      .append( ">" )
				      .append( lineFeed );
			}
			empty = false;
			closed = true;
		}
  	return this;
  }
  
  public void close() throws XmlWriterException
  {
  	if ( !stack.empty())
  	{
  		throw new XmlWriterException("Tags are not all closed. Possibly " + stack.pop() + " is unclosed");
  	}
  }
  
  static public String escapeXml(String str)
  {
  	return escapeXml(str, false);
  }
  
  
  
  static public String escapeXml(String str, boolean replaceExtended) 
  {
  	if ( StringUtils.isValidString( str ) )
  	{
  		str = removeUnprintableCharacters( str );
  		
  		StringBuffer b = new StringBuffer(str);
  		
  		int i = 0;
  		
  		while (i < b.length()) {
  			char c = b.charAt(i);
  			String replaceWith = null;
  			
  			switch(c) {
  			case '&':
  				replaceWith = "&amp;";
  				break;
  			case '<':
  				replaceWith = "&lt;";
  				break;
  			case '>':
  				replaceWith = "&gt;";
  				break;
  			default:
  				if ( replaceExtended )
  				{
	  				if (c >= 128) 
	  				{
	  					replaceWith = "&#" + (int)c + ";";
	  				}
  				}
  			}
  			
  			if (replaceWith != null) {
  				b.replace(i, i+1, replaceWith);
  				i = i + replaceWith.length();
  			}
  			else {
  				i++;
  			}
  		}
  		
  		str = b.toString();
  	}
  	
  	return str;
  }  
 
  static public String removeUnprintableCharacters(String str)
  {
  	String retStr = null;
  	
  	if ( StringUtils.isValidString( str ))
  	{
  		StringBuilder strBuf = new StringBuilder(str.length());
  		
  		int index = 0;
  		while ( index < str.length())
  		{
  			char c = str.charAt( index );
  			if ( c >= 32 || c == 10 || c == 9 || c == 13)
  			{
  				strBuf.append( c );
  			}
  			else
  			{
  				strBuf.append(' ');
  			}
  			index++;
  		}
  		
  		retStr = strBuf.toString();
  	}
  	
  	return retStr;
  }
  
  public void addNode(String nodeString)
  {
  	if ( nodeString != null )
  	{
  		strBuf.append( nodeString );
  	}
  }
  
  
  public String toString()
  {
  	return strBuf.toString();
  }
  
	public String prettifyXmlString()
	{
		String retStr = null;	
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse( new ByteArrayInputStream(this.toString().getBytes( "UTF-8" )));
			Source in = new DOMSource(document);
			StringWriter sw = new StringWriter();
			StreamResult out = new StreamResult(sw);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			t.transform(in, out);
			retStr = sw.toString();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		return retStr;
	}		
}
