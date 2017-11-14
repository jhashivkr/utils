package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.utils.PropertyReader;

public interface AcdmDataUploaders {
	public void startDataUpload(String [] dataFlds);
	public void setData(List<Map<String, String>> data);
	public void setPropertyReader(PropertyReader propertyReader);
	public void setBufferWriter(BufferedWriter bufferWriter);
	public void setDataSource(DataSource dataSource);
}
