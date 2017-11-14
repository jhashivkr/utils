package com.ibg.file.loader;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.db.ServiceLocator;
import com.ibg.parsers.json.SelectionRecord;
import com.ibg.parsers.json.SelectionRecordReader;
import com.ibg.utils.PropertyReader;
import com.ibg.utils.TxtFileFilter;
import com.ibg.utils.UploadVariables;

public class JsonPreProcessor {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	private void startLoadProcess() {

		System.out.println("reading files");
		CollectFiles<TxtFileFilter> dataFiles = new CollectFiles<TxtFileFilter>();
		dataFiles.getAllDataFiles();
		System.out.println("get all json files");
		Set<File> jsonDataFiles = dataFiles.getAllJsonFile();
		separateFiles(preProcessJsonFiles(jsonDataFiles));

		System.out.println("Done ...");
	}

	private Set<File> preProcessJsonFiles(Set<File> jsonDataFiles) {

		System.out.println("Searching for bad and good json data files");

		List<SelectionRecord> selectionRecordList = null;
		Set<File> badJsonFiles = new LinkedHashSet<File>();
		@SuppressWarnings("unchecked")
		SelectionRecordReader<SelectionRecord> selRecRdr = ((SelectionRecordReader<SelectionRecord>) ServiceLocator.getBean("selRecRdrFileFactory"));

		try {
			selRecRdr.setParameterizedType(SelectionRecord.class);

			// get the good files and bad files separated
			for (File file : jsonDataFiles) {

				selectionRecordList = selRecRdr.read(file);

				if (null == selectionRecordList) {
					badJsonFiles.add(file);
					System.err.println("Not a valid file: " + file);
				} else {
					for (SelectionRecord rec : selectionRecordList) {
						System.out.println(rec.getOidSupplier());
						System.out.println(rec.getOidIDList());
					}
				}
				selectionRecordList = null;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("total files processed: " + UploadVariables.getJsonDmpfiles().size());
		System.out.println("Total bad files: " + UploadVariables.getBadJsonDmpfiles().size());
		System.out.println("Total good files: " + (UploadVariables.getJsonDmpfiles().size() - UploadVariables.getBadJsonDmpfiles().size()));

		return badJsonFiles;

	}

	private void separateFiles(Set<File> jsonBadFiles) {
		System.out.println("Separating outr bad json data files");
		String badFileLoc = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getJsonBadDataHome();
		File badDirLoc = new File(badFileLoc);

		try {

			for (File file : jsonBadFiles) {
				file.renameTo(new File(badDirLoc, file.getName()));
			}
		} catch (Exception e) {
			System.out.println("error: " + e.toString());
		}

	}

	public static void main(String[] args) {
		new JsonPreProcessor().startLoadProcess();
	}

}
