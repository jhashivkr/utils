package com.ibg.solr.json;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SolrResponseWorker<T> {

	private URL solrUrl;
	private HttpURLConnection conn;
	private InputStream in;
	private BufferedInputStream bis;
	private InputStreamReader isr;
	private BufferedReader br;
	private T respData;
	private T solrRecords;
	private String URLStr;
	private Class<T> dataBeanType;

	private SolrResponseReader<T> solrRespRdr;

	public SolrResponseWorker(SolrResponseReader<T> solrRespRdr) {

		this.solrRespRdr = solrRespRdr;
	}
	
	public void setDataBeanType(Class<T> dataBeanType) {
		this.dataBeanType = dataBeanType;
	}



	public void setUrl(String URLStr) {
		try {
			this.solrUrl = new URL(URLStr);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Method is used get the SolrResponse object.
	 */
	public T getSolrResponse() {

		try {
			getSolrJsonData();
			parseSolrResponse();
			closeResource(in, bis, isr, br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return respData;
	}

	private void parseSolrResponse() {

		try {
			solrRespRdr.setParameterizedType(dataBeanType);			
		} catch (Exception e) {
			e.printStackTrace();
		}

		respData = solrRespRdr.read(solrUrl);

	}
	
	/***
	 * Method is used get the SolrResponse object.
	 */
	public T getSolrModelResponse() {

		try {
			getSolrJsonData();
			parseSolrModelResponse();
			closeResource(in, bis, isr, br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return solrRecords;
	}

	private void parseSolrModelResponse() {

		try {
			solrRespRdr.setParameterizedType(dataBeanType);			
		} catch (Exception e) {
			e.printStackTrace();
		}

		solrRecords = solrRespRdr.readByModel(solrUrl);

	}

	private void getSolrJsonData() {

		try {
			conn = (HttpURLConnection) solrUrl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoOutput(false);
			conn.setDoInput(true);
			in = conn.getInputStream();
			bis = new BufferedInputStream(in);
			isr = new InputStreamReader(bis);
			br = new BufferedReader(isr);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param in
	 * @param bis
	 * @param isr
	 * @param br
	 * @throws IOException
	 */
	private void closeResource(InputStream in, BufferedInputStream bis, InputStreamReader isr, BufferedReader br) throws IOException {
		if (null != br)
			br.close();
		if (null != isr)
			isr.close();
		if (null != bis)
			bis.close();
		if (null != in)
			in.close();
	}

}
