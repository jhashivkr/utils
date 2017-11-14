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

//_opaplst - type data
public class OPAPListProcessor {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	public List<Map<String, String>> dataProcess(String file) {

		BufferedWriter bufWriter = null;
		List<Map<String, String>> data = null;

		try {
			//String file = "D:/ingram/Tasks/account-data-cis-ipage/upload-data/to-prod-011215/ipage_201501121621_0001_opaplst.txt";
			//

			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));

			String logFileName = prop.getFileParserLog() + "-.log";
			bufWriter = new BufferedWriter(new FileWriter(new File(logFileName)));

			FlatFileProps flatFileProps = (FlatFileProps) ServiceLocator.getBean("flatFileProps");
			List<String> flds = Arrays.asList(flatFileProps.getFields("_opaplst").split("\\,"));
			System.out.println("fields: " + flds);

			FlatFileParser parser = (FlatFileParser) ServiceLocator.getBean("flatFileParser");
			parser.setDataFileName(file);
			parser.setFlatFileFields(flds);
			parser.setBufferWriter(bufWriter);

			data = parser.getData();
			
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return data;
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
