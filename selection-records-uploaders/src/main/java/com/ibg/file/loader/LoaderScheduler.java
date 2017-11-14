package com.ibg.file.loader;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.TxtFileFilter;

public class LoaderScheduler {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}
	
	protected void startLoadProcess() {
		CollectFiles<TxtFileFilter> dataFiles = new CollectFiles<TxtFileFilter>();
		dataFiles.getAllDataFiles();
		Set<File> flatDataFiles = dataFiles.getAllFlatFile();
		Set<File> jsonDataFiles = dataFiles.getAllJsonFile();

		startFlatFileLoadProcess(flatDataFiles);
		startJsonLoadProcess(jsonDataFiles);
		// testLoadProcess(dataFiles);
	}

	protected void startFlatFileLoadProcess(Set<File> flatFiles) {
		FlatLoaderScheduler flatLoader = new FlatLoaderScheduler();
		flatLoader.setFlatDataFiles(flatFiles);
		flatLoader.startLoadProcess();
	}

	protected void startJsonLoadProcess(Set<File> jsonDataFiles) {
		new UploadMiscLogs();
		JsonLoaderScheduler jsonLoader = new JsonLoaderScheduler();
		jsonLoader.setJsonDataFiles(jsonDataFiles);
		jsonLoader.startLoadProcess();
	}
	

	protected void testLoadProcess(CollectFiles<TxtFileFilter> dataFiles) {
		System.out.println("List of flat files found: ");
		for (File file : dataFiles.getAllFlatFile()) {
			System.out.println(file.getAbsolutePath());
		}
		System.out.println("List of json files found: ");
		for (File file : dataFiles.getAllJsonFile()) {
			System.out.println(file.getAbsolutePath());
		}

	}

	public static void main(String[] args) {
		new LoaderScheduler().startLoadProcess();
	}

}
