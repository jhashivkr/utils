package com.ibg.file.loader;

import java.io.File;
import java.util.Set;

import javax.sql.DataSource;

import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.data.uploaders.SelectionRecUploadMgr;
import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;
import com.ibg.utils.UploadVariables;

public class JsonLoaderScheduler {

	private Set<File> jsonDataFiles;
	private String custGrpName;

	public JsonLoaderScheduler() {
	}
	
	public void setCustGrpName(String custGrpName) {
		this.custGrpName = custGrpName;
	}	

	public Set<File> getJsonDataFiles() {
		return jsonDataFiles;
	}

	public void setJsonDataFiles(Set<File> jsonDataFiles) {
		this.jsonDataFiles = jsonDataFiles;
	}

	public void startLoadProcess() {
		// System.out.println("json files: " + jsonDataFiles);
		startJsonLoadProcess();
	}

	public void startLoadProcess(Set<File> jsonDataFiles, DataSource dataSource) {
		// System.out.println("json files: " + jsonDataFiles);
		startJsonLoadProcess(jsonDataFiles, dataSource);
	}

	private void startJsonLoadProcess() {

		try {

			DataSource dataSource = null;
			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			dataSource = env.getDataSource("stage");

			UploadVariables.setJsonDmpfiles(jsonDataFiles);
			SelectionRecUploadMgr selectionRecUploadMgr = ((SelectionRecUploadMgr) ServiceLocator.getBean("selRecordUpldMgr"));

			selectionRecUploadMgr.setDataSource(dataSource);
			selectionRecUploadMgr.prepareListItemData();

			UploadMiscLogs.closeWriter();
			UploadVariables.setJsonDmpfiles(null);

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
		} finally {

			UploadMiscLogs.closeWriter();

		}

	}

	private void startJsonLoadProcess(Set<File> jsonDataFiles, DataSource dataSource) {

		try {

			UploadVariables.setJsonDmpfiles(jsonDataFiles);
			SelectionRecUploadMgr selectionRecUploadMgr = ((SelectionRecUploadMgr) ServiceLocator.getBean("selRecordUpldMgr"));

			selectionRecUploadMgr.setDataSource(dataSource);
			selectionRecUploadMgr.prepareListItemData();

			UploadMiscLogs.closeWriter();
			UploadVariables.setJsonDmpfiles(null);

		} catch (Exception e) {
			System.err.println("ERROR :" );
			e.printStackTrace();
		} finally {

			UploadMiscLogs.closeWriter();

		}

	}

}
