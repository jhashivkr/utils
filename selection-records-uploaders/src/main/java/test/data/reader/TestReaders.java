package test.data.reader;


public class TestReaders {

	public static void main(String[] args) {

		//new AccountsDataProcessor().dataProcess();
		//new ContactsDataProcessor().dataProcess();
		//new CustomerDataProcessor().dataProcess();
		
		//new JsonDataProcessor().dataProcess("KATHYB", "IN");
		//new JsonDataProcessor().dataProcessMuliList("KATHYB", "IN","SN");
		//new JsonDataProcessor().dataProcessMuliList("RSU4NO", true, "IN","SN","UD","01QTR8QH");
		
		//new JsonDataProcessor().dataProcessMuliList("RSU4NO", true, "01QTR8QH"); //Medicine wishlist
		new JsonDataProcessor().dataProcess("RSU4NO", false);
		
		new JsonDataProcessor().dataProcessMuliList("RSU4NO", true, "1005U8DM"); //Medicine wishlist2
		
		
		
		//new JsonDataProcessor().dataProcess("KATHYB");
		//new JsonDataProcessor().dataProcess("RSU4NO", false);
		//new JsonDataProcessor().dataAnalyze("KATHYB");
	}
}
