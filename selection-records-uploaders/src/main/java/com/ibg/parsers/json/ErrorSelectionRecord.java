package com.ibg.parsers.json;

import java.util.List;

public class ErrorSelectionRecord extends SelectionRecord {

	private String oidContact;
	private String oidIDList;
	private List<ErrorHistory> History;

	public String getOidContact() {
		return (null != oidContact) ? oidContact : "NA";
	}

	public void setOidContact(String oidContact) {
		this.oidContact = oidContact;
	}

	public String getOidIDList() {
		return (null != oidIDList) ? oidIDList : "NA";
	}

	public void setOidIDList(String oidIDList) {
		this.oidIDList = oidIDList;
	}

	public List<? extends History> getHistory() {
		return History;
	}

	@SuppressWarnings("unchecked")
	public void setHistory(List<? extends History> history) {
		this.History = (List<ErrorHistory>) history;
	}

	@Override
	public String toString() {
		return "ErrorSelectionRecord [oidContact=" + oidContact + ", oidIDList=" + oidIDList + ", History=" + History + ", get_id()=" + get_id()
				+ ", getAction()=" + getAction() + ", getActive()=" + getActive() + ", getDateCreated()=" + getDateCreated() + ", getDateModified()="
				+ getDateModified() + ", getDateToExpire()=" + getDateToExpire() + ", getExpirationDate()=" + getExpirationDate()
				+ ", getFailReason()=" + getFailReason() + ", getFromDescription()=" + getFromDescription() + ", getLineCount()=" + getLineCount()
				+ ", getListDescription()=" + getListDescription() + ", getListType()=" + getListType() + ", getOrderType()=" + getOrderType()
				+ ", getTargetDetails()=" + getTargetDetails() + ", getRatifierID()=" + getRatifierID() + ", getSelectorID()=" + getSelectorID()
				+ ", getWasNotified()=" + getWasNotified() + ", getOidApprovalCenter()=" + getOidApprovalCenter() + ", getOidApprovalPlan()="
				+ getOidApprovalPlan() + ", getOidCustomer()=" + getOidCustomer() + ", getOidLibraryGroup()=" + getOidLibraryGroup()
				+ ", getOidPartNo()=" + getOidPartNo() + ", getOidPromotion()=" + getOidPromotion() + ", getOidSalesOrderDetail()="
				+ getOidSalesOrderDetail() + ", getOidSupplier()=" + getOidSupplier() + ", getOidWeek()=" + getOidWeek() + ", getCanOrderMessage()="
				+ getCanOrderMessage() + ", getRequestID()=" + getRequestID() + ", getExternalPartReservation()=" + getExternalPartReservation()
				+ ", getCopiedFrom()=" + getCopiedFrom() + ", getOriginalOrderInfo()=" + getOriginalOrderInfo() + ", getLines()=" + getLines()
				+ ", getHeaderParameters()=" + getHeaderParameters() + "]";
	}

	
}
