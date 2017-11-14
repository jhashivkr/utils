package com.ibg.solr.json;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileStreamReaderOld {
	/**
	 * @param args
	 */
	/**
	 * @param args
	 */

	static JsonFactory jfactory;
	static JsonParser jParser;
	static DataOutputStream fos;

	public static void main(String[] args) {

		/*** read from file ***/
		try {
			jfactory = new JsonFactory();
			jParser = jfactory.createParser(new File("E:/Ingram/Tasks/Academics-OASIS-Migration/json-selection-records-data/sample.json"));

			fos = new DataOutputStream(new FileOutputStream(new File(
					"E:/Ingram/Tasks/Academics-OASIS-Migration/json-selection-records-data/sample.txt")));

			// JsonNode json = new ObjectMapper().readTree(jParser.);
			// JsonNode registration_fields = json.get("Lines");
			// printAll(registration_fields);

			parseJson();
			//readArray();
			fos.close();
			jParser.close();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void printAll(JsonNode node) {
		Iterator<String> fieldNames = node.fieldNames();
		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode fieldValue = node.get(fieldName);
			if (fieldValue.isObject()) {
				System.out.println(fieldName + " :");
				printAll(fieldValue);
			} else {
				String value = fieldValue.asText();
				System.out.println(fieldName + " : " + value);
			}
		}
	}

	public static void readArray() {
		try {

			JsonFactory jfactory = new JsonFactory();
			JsonParser jParser = jfactory.createParser(new File("E:/Ingram/Tasks/Academics-OASIS-Migration/json-selection-records-data/sample.json"));

			String fieldname = null;
			JsonToken token = null;

			while (jParser.nextToken() != JsonToken.END_OBJECT) {

				// System.out.println(fieldname);

				if ("Lines".equalsIgnoreCase(jParser.getCurrentName())) {
					token = jParser.nextToken();
					while (token != JsonToken.END_ARRAY) {
						token = jParser.nextToken();
						if (token == JsonToken.START_OBJECT) {
							readInsideArray(jParser);
							jParser.nextToken();
						}// if
						else
							System.out.println("readArray(): " + jParser.getCurrentName() + ":" + jParser.getText());

					}// while

				}// if

			}// while

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	private static void readInsideArray(JsonParser parser) {
		try {

			parser.nextToken();

			while (true) {
				//parser.nextToken();
				if(parser.nextToken() == JsonToken.END_OBJECT){
					parser.nextToken();
					return;
				}else{
					//parser.nextToken();
					System.out.println("readInsideArray: " + parser.getCurrentName() + ":" + parser.getText());
				}
				
				

			}// while

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public static void parseJson1() {
		try {

			String fieldname = null;
			JsonToken token = null;
			String dispStr = "";

			token = jParser.nextToken();

			if (token == JsonToken.START_OBJECT) {

				while (token != JsonToken.END_OBJECT) {

					// token = jParser.nextToken();
					fieldname = jParser.getCurrentName();

					if (null != JsonToken.FIELD_NAME) {

						// token = jParser.nextToken();

						if (token == JsonToken.START_ARRAY) {
							while (token != JsonToken.END_ARRAY) {
								token = jParser.nextToken();
							}
						}
						// System.out.println(fieldname + ":" +
						// jParser.getText());
						System.out.println(fieldname);

					}// if (token.FIELD_NAME != null)

				}// while
			}// if

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public static void parseJson() {
		try {

			String fieldname = null;
			JsonToken token = null;
			String dispStr = "";

			token = jParser.nextToken();

			if (token == JsonToken.START_OBJECT) {

				// loop until token equal to "}"
				while (token != JsonToken.END_OBJECT) {

					token = jParser.nextToken();
					fieldname = jParser.getCurrentName();
					token = jParser.nextToken();

					if (JsonToken.FIELD_NAME != null) {

						if (token == JsonToken.START_ARRAY) {
							printArrays();
						} else if (token == JsonToken.START_OBJECT) {
							printInnerObject();
						}

						// System.out.println(fieldname + ":" +
						// jParser.getText());
						dispStr = fieldname + ":" + jParser.getText();
						fos.writeUTF("\nform parseJson()\n ");
						fos.writeUTF(dispStr);
					}// if (token.FIELD_NAME != null)

				}// while
			}// if

		} catch (JsonGenerationException e) {

			e.printStackTrace();

		} catch (JsonMappingException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	private static void printInnerObject() throws JsonParseException, IOException {
		System.out.println("\n----starting new object--------\n");
		JsonToken token = null;
		String fieldname = null;
		String dispStr = "";

		token = jParser.nextToken();

		// loop until token equal to "}"
		while (token != JsonToken.END_OBJECT) {

			fieldname = jParser.getCurrentName();
			token = jParser.nextToken();

			if (JsonToken.FIELD_NAME != null) {
				dispStr = fieldname + ":" + jParser.getText();
				fos.writeUTF("\nform printInnerObject(): \n");
				fos.writeUTF(dispStr);
				// System.out.println(fieldname + ":" + jParser.getText());
			}
		}// while
	}

	private static void printArrays() throws JsonParseException, IOException {

		System.out.println("\n----starting new Array--------\n");

		JsonToken token = null;

		while (token != JsonToken.END_ARRAY) {
			// System.out.println("finding array end token");
			fos.writeUTF("\nform printArrays(): \n");
			token = jParser.nextToken();

			if (token == JsonToken.START_OBJECT) {
				printInnerObject();
			}
		}

	}
}
