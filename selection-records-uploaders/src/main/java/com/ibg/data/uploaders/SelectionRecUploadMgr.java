package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.data.dao.ListDetailsDAO;
import com.ibg.data.upload.exceptions.ExceptionObjects;
import com.ibg.data.upload.exceptions.SelRecExceptionService;
import com.ibg.data.upload.exceptions.UploadExceptionService;
import com.ibg.db.ServiceLocator;
import com.ibg.file.loader.CollectJsonFiles;
import com.ibg.parsers.json.SelectionRecord;
import com.ibg.parsers.json.SelectionRecordReader;
import com.ibg.utils.PropertyReader;
import com.ibg.utils.UploadVariables;

public class SelectionRecUploadMgr {

	private SelectionRecordReader<SelectionRecord> selRecRdr;
	private CollectJsonFiles jsonFiles;
	private long stTmpStmp;
	private Map<String, Map<String, String>> libGrpUserListDet;
	private DataSource dataSource;

	public SelectionRecUploadMgr() {
	}

	public SelectionRecUploadMgr(SelectionRecordReader<SelectionRecord> selRecRdr, CollectJsonFiles jsonFiles) {
		this.selRecRdr = selRecRdr;
		this.jsonFiles = jsonFiles;
	}

	public SelectionRecUploadMgr(SelectionRecordReader<SelectionRecord> selRecRdr) {
		this.selRecRdr = selRecRdr;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private void prepareExistingListDetails(String libGrp) {

		libGrpUserListDet = null;
		libGrpUserListDet = ListDetailsDAO.findAllListId(libGrp, dataSource);
	}

	public void prepareListItemData() {

		List<SelectionRecord> selectionRecord = null;
		Long totalRecsProcessed = 0l;

		try {
			selRecRdr.setParameterizedType(SelectionRecord.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (File file : UploadVariables.getJsonDmpfiles()) {

			selectionRecord = selRecRdr.read(file);

			if (null != selectionRecord) {

				Calendar cldr = new GregorianCalendar();
				stTmpStmp = cldr.getTimeInMillis();
				System.out.printf("\nStarting ... %s ... at ... %s\n", selRecRdr.getJsonFileName(), new Date());

				prepareExistingListDetails(selectionRecord.get(0).getOidLibraryGroup());

				// printUserListIds();
				// printSelectionRecords(selectionRecord);

				UploadExceptionService.clearExceptions();
				SelRecExceptionService.clearExceptions();

				AcdmSelectionRecordUploader.setSelectionRecord(selectionRecord);
				AcdmSelectionRecordUploader.setLibGrpUserListDet(libGrpUserListDet);

				AcdmSelectionRecordUploader.startDataUpload(dataSource);

				// selective run - only for histroy
				// AcdmSelectionRecordUploader.startHistoryUpdate();

				// selective run - only for updating records
				// AcdmItemUpdate.setSelecionRecordList(selectionRecord);
				// AcdmItemUpdate.updateItem();

				writeLogs(selectionRecord.size());

				System.out.printf("\nTotal records processed for %s ... %d\n", selRecRdr.getJsonFileName(), selectionRecord.size());
				System.out.println("-----------------------------------------------------------------------------------------");
				totalRecsProcessed += selectionRecord.size();
			}
			selectionRecord = null;

		}

		System.out.println("total records processed: " + totalRecsProcessed);
		System.out.println("List of bad files: " + UploadVariables.getBadJsonDmpfiles());

	}

	private void writeLogs(int size) {

		FileWriter writer = null;
		BufferedWriter bufWriter = null;

		Calendar cldr = new GregorianCalendar();
		long endTmpStmp = cldr.getTimeInMillis();
		String endTime = cldr.get(Calendar.HOUR_OF_DAY) + ":" + cldr.get(Calendar.MINUTE) + ":" + cldr.get(Calendar.SECOND) + ":"
				+ cldr.get(Calendar.MILLISECOND);

		System.out.printf("End processing ... %s ... at ... %s\n", selRecRdr.getJsonFileName(), endTime);
		System.out.println("Total time (minutes): " + (Math.round(Math.round((endTmpStmp - stTmpStmp) / 1000)) / 60));

		endTime = cldr.get(Calendar.YEAR) + "" + (cldr.get(Calendar.MONTH) + 1) + "" + cldr.get(Calendar.DAY_OF_MONTH) + ""
				+ cldr.get(Calendar.HOUR_OF_DAY) + "" + cldr.get(Calendar.MINUTE);

		try {
			// dump all the exception messages
			// 2013126150 - 81200_T_00.json
			String logFileName = ((PropertyReader) ServiceLocator.getBean("propertyReader")).getJsonLogsDir() + selRecRdr.getJsonFileName()
					+ " - " + endTime + ".log";
			writer = new FileWriter(new File(logFileName));

			bufWriter = new BufferedWriter(writer);
			bufWriter.write("Total records processed: " + size + ".  Total time (minutes): "
					+ (Math.round(Math.round((endTmpStmp - stTmpStmp) / 1000)) / 60) + "\n");
			bufWriter.write("Total failures: " + SelRecExceptionService.exceptionList().getExceptions().size() + "\n");

			ExceptionObjects.jsonObjectMapper.writeValue(bufWriter, SelRecExceptionService.exceptionList().getExceptions());

			bufWriter.close();
			writer.close();

		} catch (IOException e) {
		} finally {
			try {
				bufWriter.close();
				writer.close();
			} catch (IOException e) {

			}

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

}
