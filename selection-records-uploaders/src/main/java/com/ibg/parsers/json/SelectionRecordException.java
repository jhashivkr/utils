package com.ibg.parsers.json;

public class SelectionRecordException extends SelectionRecord {

	private String uploadType;

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	@Override
	public String toString() {
		return "SelectionRecordException [uploadType=" + uploadType + ", get_id()=" + get_id() + ", getAction()=" + getAction() + ", getActive()="
				+ getActive() + ", getDateCreated()=" + getDateCreated() + ", getDateModified()=" + getDateModified() + ", getDateToExpire()="
				+ getDateToExpire() + ", getExpirationDate()=" + getExpirationDate() + ", getFailReason()=" + getFailReason()
				+ ", getFromDescription()=" + getFromDescription() + ", getLineCount()=" + getLineCount() + ", getListDescription()="
				+ getListDescription() + ", getListType()=" + getListType() + ", getOrderType()=" + getOrderType() + ", getTargetDetails()="
				+ getTargetDetails() + ", getRatifierID()=" + getRatifierID() + ", getSelectorID()=" + getSelectorID() + ", getWasNotified()="
				+ getWasNotified() + ", getOidApprovalCenter()=" + getOidApprovalCenter() + ", getOidApprovalPlan()=" + getOidApprovalPlan()
				+ ", getOidContact()=" + getOidContact() + ", getOidCustomer()=" + getOidCustomer() + ", getOidIDList()=" + getOidIDList()
				+ ", getOidLibraryGroup()=" + getOidLibraryGroup() + ", getOidPartNo()=" + getOidPartNo() + ", getOidPromotion()="
				+ getOidPromotion() + ", getOidSalesOrderDetail()=" + getOidSalesOrderDetail() + ", getOidSupplier()=" + getOidSupplier()
				+ ", getOidWeek()=" + getOidWeek() + ", getCanOrderMessage()=" + getCanOrderMessage() + ", getRequestID()=" + getRequestID()
				+ ", getExternalPartReservation()=" + getExternalPartReservation() + ", getCopiedFrom()=" + getCopiedFrom()
				+ ", getOriginalOrderInfo()=" + getOriginalOrderInfo() + ", getHistory()=" + getHistory() + ", getLines()=" + getLines()
				+ ", getHeaderParameters()=" + getHeaderParameters() + ", getCount()=" + getCount() + ", toString()=" + super.toString()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
