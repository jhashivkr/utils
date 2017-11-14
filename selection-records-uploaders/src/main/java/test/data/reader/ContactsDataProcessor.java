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

public class ContactsDataProcessor {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	public void dataProcess() {
		
		BufferedWriter bufWriter = null;

		try {
			String file = "C:/ingram/Tasks/account-data-cis-ipage/test/ipage_201501121621_0001_sfcontact.txt";

			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
			String load_db_name = prop.getLoadDbEnv();
			
			System.out.println("load_db_name: " + load_db_name);

			String logFileName = prop.getFileParserLog() + "-.log";
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields("_sfcontact").split("\\,"));

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(flds);
			parser.setBufferWriter(bufWriter);

			List<Map<String, String>> data = parser.getData();

			for (Map<String, String> objMap : data) {
				
				if (!"ORP".equalsIgnoreCase(load_db_name)) {
					objMap.put("COUTTSPASSWORD", "java123");
				}
				//System.out.println(objMap);
				System.out.println(objMap.get("COUTTSPASSWORD") + ", " + objMap.get("COUTTSUSERID"));
				
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

}
