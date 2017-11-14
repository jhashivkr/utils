package com.ibg.solr.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ibg.db.ServiceLocator;


public class SolrResponseReader<T> {

	private JsonParser jParser;
	private T solrRecord;
	private T respData;
	private BufferedReader jsonBfr;
	private JsonFactory jfactory;
	private Class<T> entityBeanType;

	private List<Map<String, List<String>>> docs;
	private Set<SolrKeyType> solrRespKeySet;

	public SolrResponseReader(JsonFactory jfactory) {
		this.jfactory = jfactory;
	}

	@SuppressWarnings("unchecked")
	public void setParameterizedType(Class<T> sclass) throws Exception {
		this.entityBeanType = (Class<T>) sclass;
	}

	@SuppressWarnings("unchecked")
	public T read(URL solrUrl) {
		SolrResponseData respData = null;
		try {

			solrRecord = null;
			docs = new LinkedList<Map<String, List<String>>>();
			solrRespKeySet = new LinkedHashSet<SolrKeyType>();

			if (null != solrUrl) {
				jParser = jfactory.createParser(solrUrl);
				JsonNode json = ((ObjectMapper) ServiceLocator.getBean("jsonObjMapper")).readTree(jParser);

				// readSelectionRecord(json);

				JsonNode docs;
				Iterator<JsonNode> nodeItr = json.elements();
				while (nodeItr.hasNext()) {

					JsonNode jsonNode = nodeItr.next();
					if (null != (docs = jsonNode.get("docs"))) {
						getSolrDocData(docs);
					}
				}

				jParser.close();
			} else {
				System.out.println("Not a valid file: " + jsonBfr);
			}

			if (null != solrRespKeySet && null != docs) {
				respData = new SolrResponseData(docs, solrRespKeySet);
			}

			return (T) respData;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public T readByModel(URL solrUrl) {
		SolrResponseData respData = null;
		try {

			solrRecord = null;

			if (null != solrUrl) {
				jParser = jfactory.createParser(solrUrl);
				JsonNode json = ((ObjectMapper) ServiceLocator.getBean("jsonObjMapper")).readTree(jParser);

				readSelectionRecord(json);

				jParser.close();
			} else {
				System.out.println("Not a valid file: " + jsonBfr);
			}

			return solrRecord;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void getSolrDocData(JsonNode node) {

		try {
			JsonNode this_node;
			String name;

			Iterator<JsonNode> nodes = node.elements();
			for (; nodes.hasNext();) {
				Map<String, List<String>> node_map = new LinkedHashMap<String, List<String>>();
				JsonNode elem_node = nodes.next();
				Iterator<String> fields = elem_node.fieldNames();

				for (Iterator<JsonNode> inner = elem_node.elements(); inner.hasNext();) {
					this_node = inner.next();
					name = fields.next();

					if (this_node.getNodeType() == JsonNodeType.ARRAY) {
						node_map.put(name, Arrays.asList(this_node.toString()));

					} else {
						List<String> tmp = new LinkedList<String>();
						tmp.add(this_node.asText());
						node_map.put(name, tmp);
					}

					solrRespKeySet.add(new SolrKeyType(name, this_node.getNodeType()));

				}

				docs.add(node_map);

			}

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@SuppressWarnings("unchecked")
	private void getSolrDoc(JsonNode node) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			objectMapper.setVisibility(PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			SerializationConfig serConfig = objectMapper.getSerializationConfig();
			serConfig.with(dateFormat);// .setDateFormat(dateFormat);

			DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
			deserializationConfig.with(dateFormat);

			JavaType solrRespCollection = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, this.entityBeanType);
			solrRecord = objectMapper.readValue(node.toString(), solrRespCollection);

			// List<History> history = objectMapper.readValue(node.toString(),
			// historyCollection); // this works
			// List<History> history = objectMapper.readValue(node.toString(),
			// new TypeReference<List<History>>() { });//this too works

			System.out.println("from getSolrDoc: ");

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void readSelectionRecord(JsonNode node) throws JsonParseException, JsonMappingException, IOException {
		try {
			ObjectMapper objectMapper = (ObjectMapper) ServiceLocator.getBean("jsonObjMapper");

			objectMapper.setVisibility(PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			SerializationConfig serConfig = objectMapper.getSerializationConfig();
			serConfig.with(dateFormat);

			DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
			deserializationConfig.with(dateFormat);
			
			JavaType solrRespCollection = objectMapper.getTypeFactory().constructType(this.entityBeanType);
			solrRecord = objectMapper.readValue(node.toString(), solrRespCollection);
			
		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
