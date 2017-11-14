package test.data.reader;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.db.ServiceLocator;
import com.ibg.parsers.json.SelectionRecord;
import com.ibg.parsers.json.SelectionRecordReader;
import com.ibg.utils.PropertyReader;

public class JsonDataProcessor {

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	private SelectionRecordReader<SelectionRecord> selRecRdr = null;
	private List<SelectionRecord> selectionRecord = null;
	private PropertyReader prop = null;
	private List<Map<String, String>> listData = null;
	private Map<String, Map<String, List<SelectionRecord>>> remappedSelectionRecord = null;
	//private String fileStr = "D:/ingram/Tasks/account-data-cis-ipage/upload-data/to-prod-UK-01282015/ipage_201501282202_51390000_01.json";
	//private String opapFileStr = "D:/ingram/Tasks/account-data-cis-ipage/upload-data/to-prod-UK-01282015/ipage_201501282202_51390000_opaplst.txt";
	
	private String fileStr = "D:/ingram/Tasks/account-data-cis-ipage/upload-data/to-prod-UK-01282015/ipage_201501282202_51390000a_01.json";
	private String opapFileStr = "D:/ingram/Tasks/account-data-cis-ipage/upload-data/to-prod-UK-01282015/ipage_201501282202_51390000a_opaplst.txt";

	@SuppressWarnings("unchecked")
	public JsonDataProcessor() {
		selRecRdr = ((SelectionRecordReader<SelectionRecord>) ServiceLocator.getBean("selRecRdr"));
		try {
			selRecRdr.setParameterizedType(SelectionRecord.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));

		processData();
		// reArrangeData();
	}

	public void dataProcess(String userId, String listTpId, boolean printJsonData) {
		printUserListIds(listData, userId, listTpId, printJsonData);
		printSelectionRecords(selectionRecord, userId, printJsonData, listTpId);
	}

	public void dataProcessMuliList(String userId, boolean printJsonData, String... listTpIds) {
		for (String listTpId : listTpIds) {
			printSelectionRecords(selectionRecord, userId, printJsonData, listTpId);
		}
	}

	public void dataProcess(String userId, boolean printJsonData) {

		printUserListIds(listData, userId, printJsonData);
		// printSelectionRecords(selectionRecord, userId, printJsonData);
	}

	// public void dataAnalyze(String userId) {
	// printAnalytics(userId);
	// }

	private void processData() {

		OPAPListProcessor listInfo = null;

		try {

			Long totalRecsProcessed = 0l;

			File file = new File(fileStr);

			selectionRecord = selRecRdr.read(file);

			if (null != selectionRecord) {

				System.out.printf("\nStarting ... %s ... at ... %s\n", selRecRdr.getJsonFileName(), new Date());

				// read the list information for this user from the opaplst
				listInfo = new OPAPListProcessor();
				listData = listInfo.dataProcess(opapFileStr);

				// AcdmSelectionRecordUploader.setSelectionRecord(selectionRecord);
				// AcdmSelectionRecordUploader.setLibGrpUserListDet(libGrpUserListDet);

				// selective run - only for histroy
				// AcdmSelectionRecordUploader.startHistoryUpdate();

				// selective run - only for updating records
				// AcdmItemUpdate.setSelecionRecordList(selectionRecord);
				// AcdmItemUpdate.updateItem();

				System.out.printf("\nTotal records processed for %s ... %d\n", selRecRdr.getJsonFileName(), selectionRecord.size());
				System.out.println("-----------------------------------------------------------------------------------------");
				totalRecsProcessed += selectionRecord.size();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printUserListIds(List<Map<String, String>> listData, String userId, boolean printJsonData) {

		System.out.println("Following list information made available for: <" + userId + ">");
		for (Map<String, String> map : listData) {

			if (userId.equalsIgnoreCase(map.get("USER_OWNR_ID"))) {
				System.out.println("OLD_UD_ID : DESCRIPTION => " + map.get("OLD_UD_ID") + " : " + map.get("DESCRIPTION"));
				printSelectionRecords(selectionRecord, userId, printJsonData, map.get("OLD_UD_ID"));
				// for (String key : map.keySet()) {
				// System.out.println(key + " : " + map.get(key));
				// }
			}

		}
	}

	private void printUserListIds(List<Map<String, String>> listData, String userId, String listTpId, boolean printJsonData) {

		System.out.println("Following list information made available for: <" + userId + "> and <" + listTpId + ">");
		for (Map<String, String> map : listData) {

			if (userId.equalsIgnoreCase(map.get("USER_OWNR_ID")) && listTpId.equalsIgnoreCase(map.get("OLD_UD_ID"))) {
				for (String key : map.keySet()) {
					System.out.println(key + " : " + map.get(key));
				}
			}

		}
	}

	private void printSelectionRecords(List<SelectionRecord> selectionRecord, String userId, boolean printJsonData) {

		System.out.println("\n\nFollowing backfill information made available for: <" + userId + ">");

		for (SelectionRecord rec : selectionRecord) {
			if (userId.equalsIgnoreCase(rec.getOidContact()) && printJsonData) {
				System.out.println("data: " + rec.toString());
			}
		}

	}

	private void printSelectionRecords(List<SelectionRecord> selectionRecord, String userId, boolean printData, String listTpId) {

		int ctr = 0;
		System.out.println("\nFollowing backfill information made available for: <" + userId + "> and <" + listTpId + ">");
		System.out.println("-----------------------------------------------------------------------------------------");

		for (SelectionRecord rec : selectionRecord) {
			try {
				if (userId.equalsIgnoreCase(rec.getOidContact()) && listTpId.equalsIgnoreCase(rec.getOidIDList())) {
					if (printData) {
						System.out.println("data: " + rec);
					}
					ctr++;

				}
			} catch (Exception e) {
				ctr--;
				e.printStackTrace();
				continue;
			}
		}
		System.out.printf("Total %d records found for list %s\n\n ", ctr, listTpId);
		System.out.println("-----------------------------------------------------------------------------------------");

	}

	/*
	 * private void reArrangeData() {
	 * 
	 * // remap the selectionRecordData //map => user => list_tp_id => selection
	 * record remappedSelectionRecord = new HashMap<String, Map<String,
	 * List<SelectionRecord>>>();
	 * 
	 * Map<String, List<SelectionRecord>> topLvl; StatusMap<String,
	 * SelectionRecord> statusMap = new StatusMap<String, SelectionRecord>();
	 * 
	 * for (SelectionRecord rec : selectionRecord) { try(Map<String,
	 * List<SelectionRecord>> topLevel =
	 * statusMap.getStatusMap().get(rec.getOidContact())) { try {
	 * remappedSelectionRecord
	 * .get(rec.getOidContact()).get(rec.getOidIDList()).add(rec);
	 * 
	 * try {
	 * remappedSelectionRecord.get(rec.getOidContact()).get(rec.getOidIDList
	 * ()).add(rec); } catch (Exception e2) {
	 * remappedSelectionRecord.get(rec.getOidContact()).put(rec.getOidIDList(),
	 * new LinkedList<SelectionRecord>());
	 * remappedSelectionRecord.get(rec.getOidContact
	 * ()).get(rec.getOidIDList()).add(rec); }
	 * remappedSelectionRecord.get(rec.getOidContact()).put(rec.getOidIDList(),
	 * new LinkedList<SelectionRecord>());
	 * 
	 * } catch (Exception e1) { remappedSelectionRecord.put(rec.getOidContact(),
	 * new HashMap<String, List<SelectionRecord>>());
	 * remappedSelectionRecord.get(rec.getOidContact()).put(rec.getOidIDList(),
	 * new LinkedList<SelectionRecord>());
	 * remappedSelectionRecord.get(rec.getOidContact
	 * ()).get(rec.getOidIDList()).add(rec); } //
	 * .get(rec.getOidIDList()).add(rec); } catch (Exception e2) {
	 * 
	 * }
	 * 
	 * }// for
	 * 
	 * }
	 * 
	 * private void printAnalytics(String userId) {
	 * 
	 * if (remappedSelectionRecord.containsKey(userId)) { for (String listTp :
	 * remappedSelectionRecord.get(userId).keySet()) {
	 * System.out.println("total records for list type <" + listTp + ">: " +
	 * remappedSelectionRecord.get(userId).get(listTp).size()); } // Map<String,
	 * List<SelectionRecord>> listTypeMap = //
	 * remappedSelectionRecord.get(userId); }
	 * 
	 * }
	 * 
	 * class statusData<T, List<V extends SelectionRecord>implements
	 * AutoCloseable {
	 * 
	 * private Map<T, Map<String, List<V>>> statusMap;
	 * 
	 * public statusData(){ statusMap = new HashMap<T, Map<String, List<V>>>();
	 * }
	 * 
	 * @Override public void close() throws Exception {
	 * System.out.println("Statusmap closed"); }
	 * 
	 * public Map<T, Map<String, List<V>>> getStatusMap() { return statusMap; }
	 * 
	 * 
	 * 
	 * }
	 */
	// String data = "SelectionRecord [Action=" + Action +
	// ", Active="
	// + ("true".equalsIgnoreCase(Active) ? "1" : "0") +
	// ", DateCreated=" + DateCreated + ", DateModified=" +
	// DateModified
	// + ", DateToExpire=" + DateToExpire + ", ExpirationDate="
	// + ExpirationDate + ", FailReason=" + FailReason +
	// ", FromDescription="
	// + FromDescription + ", LineCount=" + LineCount +
	// ", ListDescription=" + ListDescription + ", ListType=" +
	// ListType + ", OrderType="
	// + OrderType + ", RatifierID=" + RatifierID +
	// ", SelectorID=" + SelectorID + ", WasNotified=" +
	// WasNotified + ", oidApprovalCenter="
	// + oidApprovalCenter + ", oidApprovalPlan=" +
	// oidApprovalPlan + ", oidContact=" + oidContact +
	// ", oidCustomer=" + oidCustomer
	// + ", oidIDList=" + oidIDList + ", oidLibraryGroup=" +
	// oidLibraryGroup + ", oidPartNo=" + oidPartNo +
	// ", oidPromotion=" + oidPromotion
	// + ", oidSalesOrderDetail=" + oidSalesOrderDetail +
	// ", oidSupplier=" + oidSupplier + ", oidWeek=" + oidWeek +
	// ", CanOrderMessage="
	// + CanOrderMessage + ", OriginalOrderInfo=" +
	// OriginalOrderInfo + ", RequestID=" + RequestID +
	// ", ExternalPartReservation="
	// + ExternalPartReservation + ", CopiedFrom=" + CopiedFrom
	// + ", History=" + History + ", Lines=" + Lines +
	// ", TargetDetails="
	// + TargetDetails + ", HeaderParameters=" +
	// HeaderParameters + "]";

}
