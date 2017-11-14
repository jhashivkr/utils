package com.ibg.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class UploadVariables {

	private static File uploadStartLocation = null;
	private static Set<File> jsonDmpfiles = new LinkedHashSet<File>();
	private static Set<File> badJsonDmpfiles = new LinkedHashSet<File>();	
	private static Set<File> erroJsonfiles = new LinkedHashSet<File>();
	private static Map<String, String> uploadSelection = new LinkedHashMap<String, String>();
	private static Map<File, File> errorDmpJsonMap = new LinkedHashMap<File, File>();
	private static String uploadLog = null;
	private static boolean proceed = false;

	static {
		uploadSelection.put("A", "ALL");
		uploadSelection.put("H", "History");
		uploadSelection.put("E", "ExternalPartReservation");
		uploadSelection.put("O", "OriginalOrderInfo");
		uploadSelection.put("I", "OrderInfo");

	}

	public File getUploadStartLocation() {
		return uploadStartLocation;
	}

	public static void setUploadStartLocation(File uploadStartLocation) {
		UploadVariables.uploadStartLocation = uploadStartLocation;
	}

	public static Set<File> getJsonDmpfiles() {
		return jsonDmpfiles;
	}

	public static void setJsonDmpfiles(Set<File> jsonDmpfiles) {
		UploadVariables.jsonDmpfiles = jsonDmpfiles;
	}
	
	public static Set<File> getBadJsonDmpfiles(){
		return badJsonDmpfiles;
	}
	
	public static void addBadJsonDmpfiles(File jsonDmpfiles) {
		UploadVariables.badJsonDmpfiles.add(jsonDmpfiles);
	}

	public static Set<File> getErrorJsonfiles() {
		return erroJsonfiles;
	}

	public static void setErrorJsonfiles(Set<File> erroJsonfiles) {
		UploadVariables.erroJsonfiles = erroJsonfiles;
	}

	public static Map<String, String> getUploadSelection() {
		return uploadSelection;
	}

	public static String getUploadLog() {
		return uploadLog;
	}

	public static void setUploadLog(String uploadLog) {
		UploadVariables.uploadLog = uploadLog;
	}

	public static boolean isProceed() {
		return proceed;
	}

	public static void setProceed(boolean proceed) {
		UploadVariables.proceed = proceed;
	}

	public static void addErrorDmpJsonMap(File errFile, File dmpFile) {
		errorDmpJsonMap.put(errFile, dmpFile);
	}

	public static Map<File, File> getErrorDmpJsonMap() {
		mapErrDmpJson();
		return errorDmpJsonMap;
	}

	private static void mapErrDmpJson() {
		// map the error files with the dump files
		for (File errorFile : erroJsonfiles) {
			for (File dmpFile : jsonDmpfiles) {
				if (errorFile.getName().contains(dmpFile.getName())) {
					errorDmpJsonMap.put(errorFile, dmpFile);
				}
			}
		}
	}

}
