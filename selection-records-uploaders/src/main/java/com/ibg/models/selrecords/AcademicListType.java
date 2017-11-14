package com.ibg.models.selrecords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum AcademicListType {

	/*
	SEARCH_RESULTS("Search Results", "SR", 0), USER_DEFINED_LIST("User Defined Lists", "UD", 83), SHOPPING_CART("Shopping Cart", "SC", 46), AUTHORIZE_ORDERS(
			"Authorize Orders", "AO", 43), INBOX("<User Name>'s Inbox", "IN", 82), HTF_CANCELLED_ORDERS("iFound Cancelled Orders", "HC", 0), HTF_LIST(
			"iFound Order List", "HL", 41), SEARCH_ORDERS("Search Orders", "SO", 500), SLIP_NOTIFICATION("<User Name>'s Slip Notifications", "SN", 81), SENT_TO_ACQUISITIONS(
			"Acquisitions", "SA", 42), APPROVAL_BOOKS_SELECTED("Selected Books", "AS", 11), REVIEW_APPROVAL_BOOKS("Review Approval Books", "AB", 60), REVIEW_APPROVAL_REJECTS(
			"Review Approval Rejects", "AR", 100), PATRON_SELECTION_RECORDS("Patron Selection", "PS", 80), ON_HOLD("On Hold for Alternate Eds", "OH",
			70), REJECT_BOX("Recycling Box", "RB", 100), ORDER_DETAILS("Order Details", "OD", 20), DOWNLOAD("Downloads", "DN", 20), REQUESTS(
			"Requests", "RQ", 20), NULL("null", "NL", 0);
			*/
	
	SEARCH_RESULTS("Search Results", "SR", 0), USER_DEFINED_LIST("User Defined Lists", "UD", 83), SHOPPING_CART("Shopping Cart", "SC", 46), AUTHORIZE_ORDERS(
			"Authorize Orders", "AO", 43), INBOX("Inbox", "IN", 82), HTF_CANCELLED_ORDERS("iFound Cancelled Orders", "HC", 0), HTF_LIST(
			"iFound Order List", "HL", 41), SEARCH_ORDERS("Search Orders", "SO", 500), SLIP_NOTIFICATION("Slip Notifications", "SN", 81), SENT_TO_ACQUISITIONS(
			"Acquisitions", "SA", 42), APPROVAL_BOOKS_SELECTED("Selected Books", "AS", 11), REVIEW_APPROVAL_BOOKS("Review Approval Books", "AB", 60), REVIEW_APPROVAL_REJECTS(
			"Review Approval Rejects", "AR", 100), PATRON_SELECTION_RECORDS("Patron Selection", "PS", 80), ON_HOLD("On Hold for Alternate Eds", "OH",
			70), REJECT_BOX("Recycling Box", "RB", 100), ORDER_DETAILS("Order Details", "OD", 20), DOWNLOAD("Downloads", "DN", 20), REQUESTS(
			"Requests", "RQ", 20), NULL("null", "NL", 0);

	private String listTypeDesc;

	/**
	 * Referred id list_tp_id from ACDM_LIST_TP table
	 */
	private String listTypeId;

	private int priority; // priority of this list - to determine check status

	private static Pattern inboxPattern = Pattern.compile("(.+?)'s Inbox");
	private static Pattern slipPattern = Pattern.compile("(.+?)'s Slip Notifications");

	private AcademicListType(String listTypeDesc, String listTypeId, int priority) {
		this.listTypeDesc = listTypeDesc;
		this.listTypeId = listTypeId;
		this.priority = priority;

	}

	/**
	 * @return the listTypeDesc
	 */
	public String getListTypeDesc() {
		return listTypeDesc;
	}

	/**
	 * @param listTypeDesc
	 *            the listTypeDesc to set
	 */
	public void setListTypeDesc(String listTypeDesc) {
		this.listTypeDesc = listTypeDesc;
	}

	/**
	 * @return the listTypeId
	 */
	public String getListTypeId() {
		return listTypeId;
	}

	/**
	 * @param listTypeId
	 *            the listTypeId to set
	 */
	public void setListTypeId(String listTypeId) {
		this.listTypeId = listTypeId;
	}

	public int getListPriority() {
		return priority;
	}

	public static String getListTypeNameById(String id) {
		String listName = USER_DEFINED_LIST.getListTypeDesc();

		for (AcademicListType lt : AcademicListType.values()) {
			if (id.equalsIgnoreCase(lt.getListTypeId())) {
				listName = lt.getListTypeDesc();
				return listName;
			}
		}

		return listName;
	}

	public static String getListIdByName(String name) {
		String listId = USER_DEFINED_LIST.getListTypeId();
		
		if(inboxPattern.matcher(name).matches())
			return "IN";
		if(slipPattern.matcher(name).matches())
			return "SN";
		
		for (AcademicListType lt : AcademicListType.values()) {
			if (name.equalsIgnoreCase(lt.getListTypeDesc())) {
				listId = lt.getListTypeId();
				return listId;
			}

		}

		return listId;
	}

	public static int getListTypePriority(String id) {
		int priority = SEARCH_RESULTS.getListPriority();

		for (AcademicListType lt : AcademicListType.values()) {
			if (id.equalsIgnoreCase(lt.getListTypeId())) {
				priority = lt.getListPriority();
				return priority;
			}
		}

		return priority;
	}
	
	public static String getListId(String id){
		String value = USER_DEFINED_LIST.getListTypeId();

		for (AcademicListType lt : AcademicListType.values()) {
			if (id.equalsIgnoreCase(lt.getListTypeId())) {
				value = lt.getListTypeId();
				return value;
			}
		}

		return value;
	}
}
