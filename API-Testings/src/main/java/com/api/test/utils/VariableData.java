package com.api.test.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings("serial")
public class VariableData implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3932027539507571785L;

	public static final ArrayList<String> INGRAM_DEV_SERVER_NAMES = new ArrayList<String>(
			8);

	public static final ArrayList<String> INGRAM_QA_SERVER_NAMES = new ArrayList<String>(
			8);

	public static final ArrayList<String> INGRAM_TEST_SERVER_NAMES = new ArrayList<String>(
			8);

	public static final ArrayList<String> INGRAM_PROD_SERVER_NAMES = new ArrayList<String>(
			8);
	
	static Properties properties;
	private static HashMap<String, String> unsynchronizedProperties;

	private static final String PROPERTY_DEV_SERVER_NAMES = "devServerNames";

	private static final String PROPERTY_QA_SERVER_NAMES = "qaServerNames";
	
	private static final String PROPERTY_PROD_SERVER_NAMES = "prodServerNames";

	static
	{
		refresh();
	}
	
	private static Set<String> taschenBPIDs;


	/**
	 * @return reference to application's Properties singleton
	 */
	static public Properties getProperties()
	{
		return properties;
	}

	public static boolean isDevServerName(String serverName)
	{
		return INGRAM_DEV_SERVER_NAMES.contains(serverName);
	}

	public static boolean isQAServerName(String serverName)
	{
		return INGRAM_TEST_SERVER_NAMES.contains(serverName);
	}

	public static boolean isProdServerName(String serverName)
	{
		return INGRAM_PROD_SERVER_NAMES.contains(serverName);
	}
	
	private static void loadServerNames()
	{
		try
		{
			String devServerNames = getProperties().getProperty(PROPERTY_DEV_SERVER_NAMES);			
			String qaServerNames = getProperties().getProperty(PROPERTY_QA_SERVER_NAMES);
			String prodServerNames = getProperties().getProperty(PROPERTY_PROD_SERVER_NAMES);			

			INGRAM_DEV_SERVER_NAMES.addAll(Arrays.asList(devServerNames.split(",")));
			INGRAM_QA_SERVER_NAMES.addAll(Arrays.asList(qaServerNames.split(",")));
			INGRAM_PROD_SERVER_NAMES.addAll(Arrays.asList(prodServerNames.split(",")));

			INGRAM_TEST_SERVER_NAMES.addAll(INGRAM_DEV_SERVER_NAMES);
			INGRAM_TEST_SERVER_NAMES.addAll(INGRAM_QA_SERVER_NAMES);
		}
		catch (Exception e)
		{
			INGRAM_TEST_SERVER_NAMES.clear();
			INGRAM_DEV_SERVER_NAMES.clear();
			INGRAM_QA_SERVER_NAMES.clear();
			INGRAM_PROD_SERVER_NAMES.clear();

			System.err.println("Unable to load dev, QA and test server names from VariableData");
		}

	}

	/**
	 * This method refreshes the Variable Data by reading it from the
	 * "variabledata.properties" file. Please make sure the properties file is
	 * present in the class path.
	 */
	@SuppressWarnings("unchecked")
	public static void refresh()
	{
		try{
			InputStream in = VariableData.class
					.getResourceAsStream("/variabledata.properties");
			properties = new Properties();
			if (in != null)
			{
				try
				{
					properties.load(in);
				}
				catch (IOException ex)
				{
					System.err
							.println("SERIOUS ERROR, The variabledata.properties Read IO failed, Please make sure the file is in Class Path of the running Web App");
				}
			}
			else
			{
				System.err
						.println("SERIOUS ERROR, The variabledata.properties cannot be found in the Class Path. Please make sure the file is in Class Path of the running Web App");
			}
			loadServerNames();
			unsynchronizedProperties = new HashMap<String, String>();
			Enumeration<String> propertyEnumerator = (Enumeration<String>) properties.propertyNames();
			while(propertyEnumerator.hasMoreElements()){
				String key = propertyEnumerator.nextElement();
				String value = (String) properties.get(key);
				unsynchronizedProperties.put(key, value);
			}
			
			taschenBPIDs = new HashSet<String>();
			String bpidProperty = properties.getProperty("taschenBPIDs");
			taschenBPIDs.addAll(Arrays.asList(bpidProperty.split(",")));

		}
		catch (Throwable t)
		{
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	public static void setENEHost(String host)
	{
		properties.setProperty("ENE_Host", host);
	}

	public static void setENEPort(String port)
	{
		properties.setProperty("ENE_Port", port);
	}

	/**
	 * @return CICS server name
	 */
	public String getCICSServer()
	{
		return properties.getProperty("CICSServer");
	}

	/**
	 * @return email configuration file name
	 */
	public String getEmailConfigFile()
	{
		return properties.getProperty("emailFile");
	}

	/**
	 * @return email server name
	 */
	public String getMailServer()
	{
		return properties.getProperty("mailserver");
	}

	/**
	 * @return mainframe password for CICS purposes
	 */
	public String getPassword()
	{
		return properties.getProperty("password");
	}
	
	
	public static Set<String> getTaschenBPIDs()
	{
		return taschenBPIDs;
	}

	/**
	 * @return mainframe URL for CICS purposes
	 */
	public String getURL()
	{
		return properties.getProperty("URL");
	}

	/**
	 * @return mainframe user ID for CICS purposes
	 */
	public String getUserid()
	{
		return properties.getProperty("userid");
	}
	
	public static String getProperty(String key){
		return unsynchronizedProperties.get(key);
	}
	
	public static String getProperty(String key, Object defaultIfNull)
	{
		String value = unsynchronizedProperties.get(key);
		if (value == null)
		{
			value = defaultIfNull.toString();
		}
		return value;
	}
	
}
