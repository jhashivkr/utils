package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.ibg.data.dao.ListDetailsDAO;
import com.ibg.data.upload.exceptions.ExceptionObjects;
import com.ibg.db.ServiceLocator;
import com.ibg.file.loader.CollectErrorJsonFiles;
import com.ibg.file.loader.CollectJsonFiles;
import com.ibg.parsers.json.ErrorSelectionRecord;
import com.ibg.parsers.json.History;
import com.ibg.parsers.json.SelectionRecord;
import com.ibg.parsers.json.SelectionRecordReader;
import com.ibg.utils.PropertyReader;
import com.ibg.utils.UploadVariables;

public class ErrorAnalyzerService {

	private SelectionRecordReader<SelectionRecord> selRecRdr;
	private SelectionRecordReader<ErrorSelectionRecord> errorSelRecRdr;
	private CollectErrorJsonFiles errorJsonFiles;
	private CollectJsonFiles jsonFiles;
	private Map<String, Map<String, String>> libGrpUserListDet;
	private FileWriter writer = null;
	private BufferedWriter bufWriter = null;

	private static Pattern oidIdListPattern = Pattern.compile("(.+?)\\|(.+?)");

	public ErrorAnalyzerService() {
	}

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private void prepareExistingListDetails(String libGrp) {

		libGrpUserListDet = null;
		libGrpUserListDet = ListDetailsDAO.findAllListId(libGrp, dataSource);

	}

	public void prepareListItemData() {

		List<SelectionRecord> errorSelectionRecord = null;
		List<ErrorSelectionRecord> jsonSelectionRecord = null;
		Map<File, File> jsonMap = null;

		// collect the error jsons
		errorJsonFiles.getAllJsonFiles();

		// collect the related upload jsons
		jsonFiles.getAllJsonFiles();

		// System.out.println(UploadVariables.getErrorDmpJsonMap());

		jsonMap = UploadVariables.getErrorDmpJsonMap();

		try {

			// normal json dump
			selRecRdr.setParameterizedType(SelectionRecord.class);
			// error json dumps
			errorSelRecRdr.setParameterizedType(ErrorSelectionRecord.class);

			// dump all the exception messages
			String logFileName = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getJsonLogsDir() + "load-errors-analyses.log";
			writer = new FileWriter(new File(logFileName));
			bufWriter = new BufferedWriter(writer);

			// read the error files - get all the mongo id
			for (File file : UploadVariables.getErrorJsonfiles()) {
				System.out.println("Anayzing: " + file.getName());

				errorSelectionRecord = selRecRdr.read(file);

				if (null != errorSelectionRecord && !errorSelectionRecord.isEmpty()) {

					prepareExistingListDetails(errorSelectionRecord.get(0).getOidLibraryGroup());

					jsonSelectionRecord = errorSelRecRdr.read(jsonMap.get(file));
					// get all the mongo id
					for (SelectionRecord rec : errorSelectionRecord) {
						System.out.print(".");
						for (ErrorSelectionRecord jrec : jsonSelectionRecord) {
							if (rec.get_id().equalsIgnoreCase(jrec.get_id())) {
								analyzeAndWriteLogs(jrec);
								// SelRecExceptionService.exceptionList().add(jrec);
							}
						}// for

					}// for

					System.out.println("----------------------------------");
				}
				jsonSelectionRecord = null;
				errorSelectionRecord = null;

				System.out.println("");
			}// for

			bufWriter.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void printUserListIds() {

		for (String key : libGrpUserListDet.keySet()) {

			System.out.println(key + " : " + libGrpUserListDet.get(key));

		}
	}

	private void printSelectionRecords(List<SelectionRecord> selectionRecord) {

		for (SelectionRecord rec : selectionRecord) {

			System.out.println("data: " + rec.toString());
		}

	}

	private void analyzeAndWriteLogs(ErrorSelectionRecord rec) {

		String[] listId = null;
		String oidContact = null;
		String oidIDList = null;

		try {

			if (null != rec.getOidIDList() && !rec.getOidIDList().isEmpty()) {

				if (!oidIdListPattern.matcher(rec.getOidIDList()).matches()) {
					bufWriter.write("ACDM_LIST_ITEM#Invalid oidIdList - " + rec.getOidIDList() + "#" + rec.getOidLibraryGroup() + "#" + rec.get_id()
							+ "#\n");
				} else {

					listId = rec.getOidIDList().split("\\|");
					oidContact = listId[0].trim();
					oidIDList = listId[1].trim();
					if (null == libGrpUserListDet.get(oidContact).get(oidIDList)) {
						System.out.println("in else - item: " + rec.getOidIDList());
						bufWriter.write("ACDM_LIST_ITEM#No List Exist for oidIdList - " + rec.getOidIDList() + "#" + rec.getOidLibraryGroup() + "#"
								+ rec.get_id() + "#\n");
					}
				}
			} else {
				bufWriter.write("ACDM_ITEM_HISTORY#Invalid oidIdList - null / blank#" + rec.getOidLibraryGroup() + "#" + rec.get_id() + "#\n");
			}

			if (null != rec.getHistory() && !rec.getHistory().isEmpty()) {

				for (History obj : rec.getHistory()) {

					if (null != obj.getOidIDList() && !obj.getOidIDList().isEmpty()) {

						if (!oidIdListPattern.matcher(obj.getOidIDList()).matches()) {
							bufWriter.write("ACDM_ITEM_HISTORY#Invalid oidIdList - " + obj.getOidIDList() + "#" + rec.getOidLibraryGroup() + "#"
									+ rec.get_id() + "#" + ExceptionObjects.jsonObjectMapper.writeValueAsString(obj) + "\n");
						} else {

							listId = obj.getOidIDList().split("\\|");
							oidContact = listId[0].trim();
							oidIDList = listId[1].trim();
							if (null == libGrpUserListDet.get(oidContact).get(oidIDList)) {
								System.out.println("in else - item history: " + obj.getOidIDList());
								bufWriter.write("ACDM_ITEM_HISTORY#No List Exist for oidIdList - " + obj.getOidIDList() + "#"
										+ rec.getOidLibraryGroup() + "#" + rec.get_id() + "#"
										+ ExceptionObjects.jsonObjectMapper.writeValueAsString(obj) + "\n");
							}
						}
					}// if
					else {
						bufWriter.write("ACDM_ITEM_HISTORY#Invalid oidIdList - null / blank#" + rec.getOidLibraryGroup() + "#" + rec.get_id() + "#"
								+ ExceptionObjects.jsonObjectMapper.writeValueAsString(obj) + "\n");

					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void setSelRecRdr(SelectionRecordReader<SelectionRecord> selRecRdr) {
		this.selRecRdr = selRecRdr;
	}

	public void setErrorSelRecRdr(SelectionRecordReader<ErrorSelectionRecord> selRecRdr) {
		this.errorSelRecRdr = selRecRdr;
	}

	public void setErrorJsonFiles(CollectErrorJsonFiles errorJsonFiles) {
		this.errorJsonFiles = errorJsonFiles;
	}

	public void setJsonFiles(CollectJsonFiles jsonFiles) {
		this.jsonFiles = jsonFiles;
	}

}
