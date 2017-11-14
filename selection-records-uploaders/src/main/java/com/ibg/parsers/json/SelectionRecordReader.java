package com.ibg.parsers.json;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
import com.ibg.db.ServiceLocator;

public class SelectionRecordReader<T> {

	private JsonParser jParser;
	private List<T> selectionRecord;
	private File jsonFile;
	private JsonFactory jfactory;
	private Class<T> entityBeanType;

	public SelectionRecordReader(File jsonFile, JsonFactory jfactory) {

		/*** read from file ***/
		try {

			this.jsonFile = jsonFile;
			this.jfactory = jfactory;

			jParser = jfactory.createParser(jsonFile);

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setParameterizedType(Class<T> sclass) throws Exception {
		//this.entityBeanType = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);		
		//this.entityBeanType = (Class<T>) parameterized.getClass();
		this.entityBeanType = (Class<T>) sclass;
	}

	public SelectionRecordReader(JsonFactory jfactory) {
		this.jfactory = jfactory;
	}

	public List<T> read(File jsonFile) {
		try {

			selectionRecord = null;

			if (null != jsonFile) {
				this.jsonFile = jsonFile;
				jParser = jfactory.createParser(jsonFile);

				// JsonNode json = new ObjectMapper().readTree(jParser);
				JsonNode json = ((ObjectMapper) ServiceLocator.getBean("jsonObjMapper")).readTree(jParser);

				readSelectionRecord(json);
				jParser.close();
			} else {				
				return null;
			}

			return selectionRecord;
		} catch (JsonParseException e) {
			//System.err.println("Error: " + e.toString());
		} catch (IOException e) {			
			//System.err.println("Error: " + e.toString());
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void readSelectionRecord(JsonNode node) {
		try {
			// ObjectMapper objectMapper = new ObjectMapper();
			ObjectMapper objectMapper = (ObjectMapper) ServiceLocator.getBean("jsonObjMapper");

			objectMapper.setVisibility(PropertyAccessor.FIELD, com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY);
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			SerializationConfig serConfig = objectMapper.getSerializationConfig();
			serConfig.with(dateFormat);

			DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
			deserializationConfig.with(dateFormat);
			
			//JavaType selRecCollection = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, SelectionRecord.class);
			JavaType selRecCollection = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, this.entityBeanType);
			selectionRecord = objectMapper.readValue(node.toString(), selRecCollection);

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

	public String getJsonFileName() {
		return jsonFile.getName();
	}

}
