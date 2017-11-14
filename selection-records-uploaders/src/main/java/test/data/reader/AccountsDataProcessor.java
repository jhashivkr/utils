package test.data.reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.db.ServiceLocator;
import com.ibg.parsers.flat.FlatFileParser;
import com.ibg.utils.FlatFileProps;
import com.ibg.utils.PropertyReader;

//_arlg - type data
public class AccountsDataProcessor {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	public void dataProcess() {

		BufferedWriter bufWriter = null;

		try {
			String file = "C:/ingram/Tasks/account-data-cis-ipage/ipage_201409190156_dev_memorial_arlg.txt";

			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));

			String logFileName = prop.getFileParserLog() + "-.log";
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields("_arlg").split("\\,"));

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(flds);
			parser.setBufferWriter(bufWriter);

			List<Map<String, String>> data = parser.getData();

			for (Map<String, String> objMap : data) {
				System.out.println(objMap);
			}
		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
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

	/*
	 * private void doUpdates(Map<String, String> msgObjMap) {
	 * 
	 * if (null == msgObjMap || msgObjMap.isEmpty()) { return; }
	 * 
	 * CisCustGroup custGroup = new CisCustGroup();
	 * 
	 * custGroup.setAllowstockrush(msgObjMap.get(_arlg_arr[0]));
	 * custGroup.setCcgroupsoptin(msgObjMap.get(_arlg_arr[1]));
	 * custGroup.setCountry(msgObjMap.get(_arlg_arr[2]));
	 * custGroup.setCustGroup(msgObjMap.get(_arlg_arr[3]));
	 * custGroup.setDisplayebookestnet(msgObjMap.get(_arlg_arr[4]));
	 * custGroup.setDisplayestnet(msgObjMap.get(_arlg_arr[5]));
	 * custGroup.setDisplayreviews(msgObjMap.get(_arlg_arr[6]));
	 * custGroup.setIfound(msgObjMap.get(_arlg_arr[7]));
	 * custGroup.setLibraryGroup(msgObjMap.get(_arlg_arr[8]));
	 * 
	 * custGroup.setMaximumorderquantity((null != msgObjMap.get(_arlg_arr[9]) ||
	 * !msgObjMap.get(_arlg_arr[9]).isEmpty()) ? new
	 * Integer(msgObjMap.get(_arlg_arr[9])) : 0);
	 * 
	 * custGroup.setMultiline(msgObjMap.get(_arlg_arr[10]));
	 * custGroup.setIlsSystem(msgObjMap.get(_arlg_arr[11]));
	 * custGroup.setOpenurl(msgObjMap.get(_arlg_arr[12]));
	 * custGroup.setReportName(msgObjMap.get(_arlg_arr[13]));
	 * custGroup.setRestrictinitials(msgObjMap.get(_arlg_arr[14]));
	 * custGroup.setRetainselector(msgObjMap.get(_arlg_arr[15]));
	 * custGroup.setShowfund(msgObjMap.get(_arlg_arr[16]));
	 * custGroup.setShowlocation(msgObjMap.get(_arlg_arr[17]));
	 * custGroup.setShowprofile(msgObjMap.get(_arlg_arr[18]));
	 * 
	 * custGroup.setShibblolethentityid(msgObjMap.get(_arlg_arr[19]));
	 * custGroup.setShibbolethidp(msgObjMap.get(_arlg_arr[20]));
	 * custGroup.setShibbolethurl(msgObjMap.get(_arlg_arr[21]));
	 * 
	 * custGroup.setDateFormat(msgObjMap.get(_arlg_arr[22]));
	 * custGroup.setApikey(msgObjMap.get(_arlg_arr[23]));
	 * custGroup.setApiip(msgObjMap.get(_arlg_arr[24]));
	 * 
	 * qService.addCustomerGroup(custGroup);
	 * 
	 * 
	 * }
	 */

}
