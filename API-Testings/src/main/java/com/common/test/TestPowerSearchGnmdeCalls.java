package com.common.test;

import ibg.common.util.SelectableOptions;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestPowerSearchGnmdeCalls {

	private final static void testPowerSearchLCCall() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre&program=ipage/cachetables.p&mode=iclcsubject";
		url = url.replaceAll(" ", "%20");
		GanimedeResponse ganimedeResponse = ganimedeWorker
				.getGanimedeResponse(url);

		FileWriter writer = null;
		BufferedWriter bufWriter = null;

		try {
			writer = new FileWriter(
					new File(
							"E:/ingram/Tasks/Academics-OASIS-Migration/power-search/lc.txt"),
					true);
			bufWriter = new BufferedWriter(writer);

			List<Map<String, String>> ganimadeResponseList = ganimedeResponse
					.getDatalist();

			for (Map<String, String> object : ganimadeResponseList) {
				for (String key : object.keySet()) {
					bufWriter.write(key + ":" + object.get(key) + "|");
				}
				bufWriter.write("\n");
			}

			bufWriter.close();
			writer.close();

		} catch (IOException e) {
			System.out.println("Error from DataLoadScheduler: " + e);
			e.printStackTrace();
		} finally {
			try {
				bufWriter.close();
				writer.close();
			} catch (IOException e) {

			}

		}
		// printGanimedeResponseData(ganimedeResponse);
	}

	private final static void testPowerSearchDeweyCall() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest"
				+ "&sandbox=kboudre&program=ipage/cachetables.p&mode=icdcsubject";
		url = url.replaceAll(" ", "%20");
		GanimedeResponse ganimedeResponse = ganimedeWorker
				.getGanimedeResponse(url);
		FileWriter writer = null;
		BufferedWriter bufWriter = null;

		try {
			writer = new FileWriter(
					new File(
							"E:/ingram/Tasks/Academics-OASIS-Migration/power-search/dewey.txt"),
					true);
			bufWriter = new BufferedWriter(writer);

			List<Map<String, String>> ganimadeResponseList = ganimedeResponse
					.getDatalist();

			for (Map<String, String> object : ganimadeResponseList) {
				for (String key : object.keySet()) {
					bufWriter.write(key + ":" + object.get(key) + "|");
				}
				bufWriter.write("\n");
			}

			bufWriter.close();
			writer.close();

		} catch (IOException e) {
			System.out.println("Error from DataLoadScheduler: " + e);
			e.printStackTrace();
		} finally {
			try {
				bufWriter.close();
				writer.close();
			} catch (IOException e) {

			}

		}
		// printGanimedeResponseData(ganimedeResponse);

	}

	
	private static void powerSearch() {

		// mode = icareastudies / icreadership / icdcsubject / iclcsubject /
		// sfbt / country / language /
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=ipage/cachetables.p"
				+ "&mode=icreadership&cust-group=11100";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker
				.getGanimedeResponse(url);

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void powerSearchDataTest(String field) {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/cachetables.p&mode=" + field;

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker
				.getGanimedeResponse(url);

		System.out.println("Ganimade Header :"
				+ ganimedeResponse.getHeaderinfo());
		System.out.println("-----------");
		System.out.println("Key : Value");
		System.out.println("-----------");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse
				.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			System.out.println(object.get("code") + ","
					+ object.get("description"));
		}
		System.out.println("Ganimade Footer :" + ganimedeResponse.getStatus());
	}

	private static void printGanimedeResponseData(
			GanimedeResponse ganimedeResponse) {
		System.out.println("Ganimade Header :"
				+ ganimedeResponse.getHeaderinfo());
		System.out.println("-----------");
		System.out.println("Key : Value");
		System.out.println("-----------");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse
				.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			for (String key : object.keySet()) {
				System.out.println(key + ":" + object.get(key));
			}
			System.out.println();
		}
		System.out.println("Ganimade Footer :" + ganimedeResponse.getStatus());
	}

	private static void printGanimedeResponseData(Set<SelectableOptions> data,
			String title) {

		for (SelectableOptions object : data) {
			System.out.println(object.getValue() + ","
					+ object.getDisplayValue());
		}
	}

}
