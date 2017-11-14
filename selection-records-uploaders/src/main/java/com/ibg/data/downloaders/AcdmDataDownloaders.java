package com.ibg.data.downloaders;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.utils.PropertyReader;

public interface AcdmDataDownloaders {
	public void startDataDownload(String customerGrp);
	public void setDataSource(DataSource dataSource);
}
