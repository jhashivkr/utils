package com.api.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class DataToCSV {
	
	private Map<Integer, Map<String, String>> data = null;
	private Set<String> suppressions = null;
	private Set<String> colHdrs = null;
	
	public boolean prntCSV(){
		return true;
	}
	
	

	public void setData(Map<Integer, Map<String, String>> data) {
		this.data = data;
	}



	public void setSuppressions(Set<String> suppressions) {
		this.suppressions = suppressions;
	}



	public void setColHdrs(Set<String> colHdrs) {
		this.colHdrs = colHdrs;
	}


	private void createCSV() {

		try {

			StringBuffer strBuf = new StringBuffer();

			// header first
			for (String col_name : this.colHdrs) {
				if (!this.suppressions.contains(col_name)) {
					strBuf.append(col_name);
					strBuf.append(',');
				}
			}
			strBuf.deleteCharAt(strBuf.lastIndexOf(","));

			// line break
			strBuf.append('\n');

			// data
			Set<Integer> off_ids = this.data.keySet();
			for (Integer id : off_ids) {
				Map<String, String> row_val = this.data.get(id);
				Set<String> labels = row_val.keySet();

				// Offering Id
				strBuf.append(id);
				strBuf.append(',');

				// iterate through the column header set
				for (String hdrs : labels) {
					if (!this.suppressions.contains(hdrs)) {
						if (null != row_val.get(hdrs))
							strBuf.append(row_val.get(hdrs).replaceAll(
									"(?<!\\\\), ", " "));
						strBuf.append(',');
					}
				}
				strBuf.deleteCharAt(strBuf.lastIndexOf(","));

				// line break
				strBuf.append('\n');
			}

			FileWriter out = new FileWriter(new File(
					"E:/ingram/csv/offerings-Sample.csv"));
			out.write(strBuf.toString());

			out.close();
			System.out.println("CSV written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}