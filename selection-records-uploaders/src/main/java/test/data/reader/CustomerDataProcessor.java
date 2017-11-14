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

//_arcust - type data
public class CustomerDataProcessor {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	public void dataProcess() {

		BufferedWriter bufWriter = null;

		try {
			String file = "C:/ingram/Tasks/account-data-cis-ipage/ipage_201409190156_dev_memorial_arcust.txt";

			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));

			String logFileName = prop.getFileParserLog() + "-.log";
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields("_arcust").split("\\,"));
			System.out.println("fields: " + flds);

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
	 * private void doCreates(Map<String, String> msgObjMap) {
	 * 
	 * if (null == msgObjMap || msgObjMap.isEmpty()) { return; } CISCustomer
	 * customer = new CISCustomer();
	 * 
	 * customer.setCurrency(msgObjMap.get(_arcust_arr[0]));
	 * customer.setCustGroup(msgObjMap.get(_arcust_arr[1]));
	 * customer.setCustomerNo(msgObjMap.get(_arcust_arr[2]));
	 * customer.setDivision(msgObjMap.get(_arcust_arr[3]));
	 * customer.setReportName(msgObjMap.get(_arcust_arr[4]));
	 * 
	 * customer.setYearEnd((null != msgObjMap.get(_arcust_arr[5]) ||
	 * !msgObjMap.get(_arcust_arr[5]).isEmpty()) ? new
	 * Integer(msgObjMap.get(_arcust_arr[5])) : 0); //int year = (null !=
	 * msgObjMap.get(_arcust_arr[5]) ||
	 * !msgObjMap.get(_arcust_arr[5]).isEmpty()) ? new
	 * Integer(msgObjMap.get(_arcust_arr[5])) : 0;
	 * //customer.setMaximumorderquantity(year);
	 * 
	 * qService.addCustomer(customer);
	 * 
	 * }
	 */

}
