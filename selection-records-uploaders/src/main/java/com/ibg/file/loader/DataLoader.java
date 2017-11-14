package com.ibg.file.loader;

public interface DataLoader {
	
	public void setDataFileName(String fileName);
	public boolean loadData();
	public void setDbName(String dbName);

}
