package com.ibg.data.downloaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import com.ibg.db.ServiceLocator;
import com.ibg.utils.FlatFileProps;
import com.ibg.utils.PropertyReader;

public class FlatDownloadScheduler {

	private PropertyReader prop;
	private String custGrpName;

	public void startLoadProcess(DataSource dataSource) {

		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));

		List<String> customers = prop.getCisCustomers();
		if (null != customers && !customers.isEmpty()) {

			System.out.println("customers: " + customers);
			for (String custGrp : customers) {
				this.custGrpName = custGrp;
				System.out.println("Processing for " + this.custGrpName);
				startSelectiveFlatFileLoadProcess(dataSource);
				AcdmSelectionRecordDownloader.startDownload(dataSource, custGrpName);
			}
		}

	}

	private void startSelectiveFlatFileLoadProcess(DataSource dataSource) {

		try {
			loadData("_sfcontact", dataSource);
		} catch (Exception e) {
			// do nothing - just swallow the exception
		}

	}

	private void loadData(String obj, DataSource dataSource) {

		BufferedWriter bufWriter = null;
		StringBuilder bldr = new StringBuilder();

		try {

			obj = obj.concat("Download");

			// get the particular bean reference and load
			// the data in the table
			if (null != ServiceLocator.getBean(obj)) {
				AcdmDataDownloaders loader = (AcdmDataDownloaders) ServiceLocator.getBean(obj);
				loader.setDataSource(dataSource);
				loader.startDataDownload(custGrpName);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufWriter != null) {
					bufWriter.close();
				}

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
