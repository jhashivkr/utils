// _sfcontact.txt
package com.ibg.data.downloaders;

import java.util.Date;
import java.util.List;

import com.ibg.db.ServiceLocator;
import com.ibg.models.selrecords.AcdmOrderInfoTemplate;
import com.ibg.models.selrecords.AcdmOrderInfoTemplateData;
import com.ibg.utils.PropertyReader;

public class AcdmOrderInfoTemplateDownloader {

	public AcdmOrderInfoTemplateDownloader() {
	}

	public static void downloadDataFromTable(String custGrp, List<String> userIds) {

		int totRecordsdownloaded = 0;
		BufferDataWriter bufWriter = null;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "orderinfo",
					((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());

			System.out.println("Downloading OrderInfo Template Data - start at " + new Date());

			for (String userId : userIds) {
				// get all the customer no for this customer group
				List<AcdmOrderInfoTemplate> templateList = AcdmOrderInfoTemplate.where("USER_OWNER_ID=?", userId);

				if (null != templateList && !templateList.isEmpty()) {
					for (AcdmOrderInfoTemplate tmplt : templateList) {

						List<AcdmOrderInfoTemplateData> templateData = tmplt.getAll(AcdmOrderInfoTemplateData.class);

						StringBuilder bldr = new StringBuilder();

						// ID,USER_ID,TMPLT_NAME,DEF_TMPLT,COPIES,DT_TM_LAST_USED,DT_TM_LAST_MOD,PARAMS
						bldr.append((null != tmplt.get("TEMPLATE_ID") ? tmplt.get("TEMPLATE_ID").toString().toUpperCase() : "")).append('\t');
						bldr.append((null != tmplt.get("USER_OWNER_ID") ? tmplt.get("USER_OWNER_ID").toString() : "")).append('\t');
						bldr.append((null != tmplt.get("TEMPLATE_NAME") ? tmplt.get("TEMPLATE_NAME").toString() : "")).append('\t');
						bldr.append((0 == tmplt.getInteger("IS_DEFAULT_TEMPLATE") ? "FALSE" : "TRUE")).append('\t');

						// copies
						// scope for lambda
						// bldr.append(cv -> copiesValue() = return
						// ("MULTI".equalsIgnoreCase(tmplt.get("TEMPLATE_NAME").toString())
						// ? 2 : 1);
						bldr.append(
								(null != tmplt.get("TEMPLATE_VIEW_MODE") ? ("MULTI".equalsIgnoreCase(tmplt.get("TEMPLATE_NAME").toString())
										? "2"
										: "1") : "1")).append('\t');

						if (null != templateData && !templateData.isEmpty()) {
							for (AcdmOrderInfoTemplateData data : templateData) {
								bldr.append((null != data.get("FIELD_KEY") ? data.get("FIELD_KEY").toString() : "")).append('\t');
								bldr.append((null != data.get("FIELD_VALUE") ? data.get("FIELD_VALUE").toString() : "")).append('\t');
							}// for
							bldr.append('~');
						}// if

						// remove the extra ~
						try {
							bldr.replace(bldr.lastIndexOf("~"), bldr.lastIndexOf("~") + 1, "");
						} catch (Exception e) {
							//do nothing
						}

						bldr.append('\n');
						totRecordsdownloaded++;
						bufWriter.writeBufferedData(bldr.toString());

					}// for
				}// if

			}// for
			
			if(totRecordsdownloaded <= 0){
				//write blank line
				bufWriter.writeBufferedData("");
			}
			bufWriter.closeWriter();
			System.out.println("Total Order Info records downloaded: " + totRecordsdownloaded);
			System.out.println();

		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
		}

	} // End Method

	interface Copies {
		int value(String data);
	}

	public int copiesValue(String data, Copies copy) {
		return copy.value(data);
	}

} // END CLASS
