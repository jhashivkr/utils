package com.ibg.data.downloaders;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.db.ServiceLocator;
import com.ibg.models.selrecords.AcdmList;
import com.ibg.models.selrecords.AcdmOrderInfo;
import com.ibg.utils.PropertyReader;

public class AcdmListOrderInfoDownloader extends ActiveJdbcCon {

	public AcdmListOrderInfoDownloader() {
	}

	@SuppressWarnings("unchecked")
	public static void startDownload(String custGrp, List<AcdmList> acdmList) {

		StringBuilder bldr = new StringBuilder();
		BufferDataWriter bufWriter = null;
		int totRecordsdownloaded = 0;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "_listorderinfo",
					((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());

			// List<AcdmList> acdmList =
			// AcdmList.where("LIB_GRP=? order by user_ownr_id,list_tp_id",
			// custGrp);

			for (AcdmList list : acdmList) {

				List<AcdmOrderInfo> orders = list.getAll(AcdmOrderInfo.class);
				Collections.sort(orders, new Comparator<AcdmOrderInfo>() {

					@Override
					public int compare(AcdmOrderInfo o1, AcdmOrderInfo o2) {
						return (o1.getInteger("LINE_NO") > o2.getInteger("LINE_NO")) ? 1 : -1;
					}

				});

				if (null != orders && !orders.isEmpty()) {
					for (AcdmOrderInfo order : orders) {
						// USER_ID|LIST_ID|OPR_ID|FIELD_KEY|FIELD_VALUE|LINE_NO
						bldr.append((null != list.get("USER_OWNR_ID") ? list.getString("USER_OWNR_ID") : "")).append('|');
						bldr.append((null != list.get("LIST_TP_ID") ? list.getString("LIST_TP_ID") : "")).append('|');
						bldr.append((null != order.get("OPR_ID") ? order.getString("OPR_ID") : "")).append('|');
						bldr.append((null != order.get("FIELD_KEY") ? order.getString("FIELD_KEY") : "")).append('|');
						bldr.append((null != order.get("FIELD_VALUE") ? order.getString("FIELD_VALUE") : "")).append('|');
						bldr.append((null != order.get("LINE_NO") ? order.getString("LINE_NO") : "")).append('\n');

						totRecordsdownloaded++;
						bufWriter.writeBufferedData(bldr.toString());
						bldr.delete(0, bldr.length());
					}
				}

			}

			if (totRecordsdownloaded <= 0) {
				// write blank line
				bufWriter.writeBufferedData("");
			}
			bufWriter.closeWriter();
			System.out.println("Total Order Info records downloaded: " + totRecordsdownloaded);
			System.out.println();

			// not required
			// itemList.set("FAIL_REASON", selectionRecord.getFailReason());
			// itemList.set("SUPLR", selectionRecord.getOidSupplier());
			// itemList.set("OIDIDLIST", selectionRecord.getOidIDList());

		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
		}
	}

	private void sortOrderInfoList() {
		SortedSet<Map.Entry<String, Double>> sortedset = new TreeSet<>(new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {

				if (e1.getValue().doubleValue() <= 0) {
					return 1;
				}

				else if (e2.getValue().doubleValue() <= 0) {
					return -1;
				}

				else if (e1.getValue().doubleValue() > e2.getValue().doubleValue()) {
					return 1;
				}

				else if (e1.getValue().doubleValue() < e2.getValue().doubleValue()) {
					return -1;
				} else if (e1.getValue().doubleValue() == e2.getValue().doubleValue()) {
					return 1;
				}

				return 0;
			}
		});

		SortedMap<String, Double> eanMap = new TreeMap<>();
	}

}
