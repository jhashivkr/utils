package com.ibg.test;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrServer;


public final class SolrJServerFactory  {

	private final String core;
	private final String host;
	private final int port;
	private final int maxQueryMs;
	private static SolrServerProperties defSolServerProps;
	private static Map<SolrServerProperties, SolrServer> servers = null;

	public SolrJServerFactory(String core, String host, int port, int maxQueryMs) {
		this.core = core;
		this.host = host;
		this.port = port;
		this.maxQueryMs = maxQueryMs;

		defSolServerProps = new SolrServerProperties(host, port, core);
		servers = new ConcurrentHashMap<SolrServerProperties, SolrServer>();
		
		addDefaultServerInstance();
	}

	private final SolrServer addSolrServer(SolrServerProperties props) throws MalformedURLException {
		SolrServer server = servers.get(props);
		if (server == null) {
			HttpSolrServer aServer = null;

			aServer = new HttpSolrServer("http://" + props.getHost() + ":" + props.getPort() + "/solr" + (props.getCore() == null ? "" : ("/" + props.getCore())));
			aServer.setRequestWriter(new BinaryRequestWriter());
			aServer.setParser(new BinaryResponseParser());
			servers.put(props, aServer);
			return aServer;
		} else {
			return server;
		}
	}

	private final void addDefaultServerInstance() {

		SolrServerProperties props = new SolrServerProperties();
		props.setHost(host);
		props.setPort(port);
		props.setCore(core);
		try {
			addSolrServer(props);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public final SolrServer getDefaultInstance() throws SearchException {
		return servers.get(defSolServerProps);
	}

	public final static SolrServer getServerInstance(String host, int port, String core) throws SearchException {
		SolrServerProperties props = new SolrServerProperties();
		props.setHost(host);
		props.setPort(port);
		props.setCore(core);

		SolrServer server = servers.get(props);
		if (server == null) {
			server = new SolrServerUtil() {
				public SolrServer addSolrServer(SolrServerProperties props) {
					return addSolrServer(props);
				}
			}.addSolrServer(props);

		}
		return server;
	}
	

	public int getMaxQueryMs() {
		return maxQueryMs;
	}

	public String getCore() {
		return core;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	interface SolrServerUtil {
		public SolrServer addSolrServer(SolrServerProperties props);
	}

}
