package com.ibg.parsers.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SelectionRecord {

	@JsonIgnore
	private String uploadType;
	private String _id;
	private SelectionAction Action;
	private String Active;

	private DateField DateCreated;
	private DateField DateModified;
	private DateField DateToExpire;
	
	private String ExpirationDate;
	@JsonIgnore
	private String FailReason;
	private String FromDescription;
	private String LineCount;
	private String ListDescription;
	private String ListType;
	private String OrderType;

	private String RatifierID;
	private String SelectorID;
	private String WasNotified;
	private String oidApprovalCenter;
	private String oidApprovalPlan;
	@JsonIgnore
	private String oidContact;
	private String oidCustomer;
	private String oidIDList;
	private String oidLibraryGroup;
	private String oidPartNo;
	private String oidPromotion;
	private String oidSalesOrderDetail;
	@JsonIgnore
	private String oidSupplier;
	private String oidWeek;

	private String CanOrderMessage;
	private String RequestID;
	private String CopiedFrom;

	@JsonIgnore
	private String Count;
	
	@JsonIgnore
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	private OriginalOrderInfoDetail OriginalOrderInfo;
	private ExternalPartReservationDetail ExternalPartReservation;

	protected List<History> History;
	private List<SelectionLines> Lines;
	private List<TargetDetail> TargetDetails;
	private Map<String, String> HeaderParameters = new LinkedHashMap<String, String>();

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public SelectionAction getAction() {
		return (null != Action) ? Action : SelectionAction.None;
	}

	public void setAction(SelectionAction action) {
		Action = action;
	}

	/*
	 * public Boolean getActive() { return Active; }
	 * 
	 * public void setActive(Boolean active) { Active = active; }
	 */

	public String getActive() {
		return Active;
	}

	public void setActive(String active) {

		this.Active = active;
	}

	public DateField getDateCreated() {
		return DateCreated;
	}

	public void setDateCreated(DateField dateCreated) {
		DateCreated = dateCreated;
	}

	public DateField getDateModified() {
		return DateModified;
	}

	public void setDateModified(DateField dateModified) {
		DateModified = dateModified;
	}

	public DateField getDateToExpire() {
		return DateToExpire;
	}

	public void setDateToExpire(DateField dateToExpire) {
		DateToExpire = dateToExpire;
	}

	public String getExpirationDate() {
		if (null != ExpirationDate) {
			try {
				return ExpirationDate.substring(0, 4) + "-" + ExpirationDate.substring(4, 6) + "-" + ExpirationDate.substring(6, 8) + " 00:00:00";

			} catch (Exception e) {
				return null;
			}
		}else{
			return null;
		}
	}
	public void setExpirationDate(String expirationDate) {
		ExpirationDate = expirationDate;
	}

	public String getFailReason() {
		return (null != FailReason) ? FailReason : "NA";
	}

	public void setFailReason(String failReason) {
		FailReason = failReason;
	}

	public String getFromDescription() {
		return (null != FromDescription) ? FromDescription : "NA";
	}

	public void setFromDescription(String fromDescription) {
		FromDescription = fromDescription;
	}

	public String getLineCount() {
		return (null != LineCount) ? LineCount : "NA";
	}

	public void setLineCount(String lineCount) {
		LineCount = lineCount;
	}

	public String getListDescription() {
		return (null != ListDescription) ? ListDescription : "NA";
	}

	public void setListDescription(String listDescription) {
		ListDescription = listDescription;
	}

	public String getListType() {
		return (null != ListType) ? ListType : "NA";
	}

	public void setListType(String listType) {
		ListType = listType;
	}

	public String getOrderType() {
		return (null != OrderType) ? OrderType : "NA";
	}

	public void setOrderType(String orderType) {
		OrderType = orderType;
	}

	public List<TargetDetail> getTargetDetails() {
		return TargetDetails;
	}

	public void setTargetDetails(List<TargetDetail> targetDetails) {
		TargetDetails = targetDetails;
	}

	public String getRatifierID() {
		return (null != RatifierID) ? RatifierID : "";
	}

	public void setRatifierID(String ratifierID) {
		RatifierID = ratifierID;
	}

	public String getSelectorID() {
		return (null != SelectorID) ? SelectorID : "";
	}

	public void setSelectorID(String selectorID) {
		SelectorID = selectorID;
	}

	public String getWasNotified() {
		return (null != WasNotified) ? WasNotified : "0";
	}

	public void setWasNotified(String wasNotified) {
		WasNotified = wasNotified;
	}

	public String getOidApprovalCenter() {
		return (null != oidApprovalCenter) ? oidApprovalCenter : "NA";
	}

	public void setOidApprovalCenter(String oidApprovalCenter) {
		this.oidApprovalCenter = oidApprovalCenter;
	}

	public String getOidApprovalPlan() {
		return (null != oidApprovalPlan) ? oidApprovalPlan : "NA";
	}

	public void setOidApprovalPlan(String oidApprovalPlan) {
		this.oidApprovalPlan = oidApprovalPlan;
	}

	public String getOidContact() {
		return (null != oidContact) ? oidContact : "NA";
	}

	public void setOidContact(String oidContact) {
		this.oidContact = oidContact;
	}

	public String getOidCustomer() {
		return (null != oidCustomer) ? oidCustomer : "NA";
	}

	public void setOidCustomer(String oidCustomer) {
		this.oidCustomer = oidCustomer;
	}

	public String getOidIDList() {
		return (null != oidIDList) ? oidIDList : "NA";
	}

	public void setOidIDList(String oidIDList) {

		this.oidContact = null;
		this.oidIDList = null;

		// it comes with list owner and list id separated by |
		if (null != oidIDList && !oidIDList.isEmpty()) {
			String[] listId = null;
			try {

				listId = oidIDList.split("\\|");
				this.oidContact = listId[0];
				this.oidIDList = listId[1];
				listId = null;

			} catch (Exception ex) {
				this.oidContact = null;
				this.oidIDList = null;
			}
		}

	}

	public String getOidLibraryGroup() {
		return (null != oidLibraryGroup) ? oidLibraryGroup : "NA";
	}

	public void setOidLibraryGroup(String oidLibraryGroup) {
		this.oidLibraryGroup = oidLibraryGroup;
	}

	public String getOidPartNo() {
		return (null != oidPartNo) ? oidPartNo : "NA";
	}

	public void setOidPartNo(String oidPartNo) {
		this.oidPartNo = oidPartNo;
	}

	public String getOidPromotion() {
		return (null != oidPromotion) ? oidPromotion : "NA";
	}

	public void setOidPromotion(String oidPromotion) {
		this.oidPromotion = oidPromotion;
	}

	public String getOidSalesOrderDetail() {
		return (null != oidSalesOrderDetail) ? oidSalesOrderDetail : "NA";
	}

	public void setOidSalesOrderDetail(String oidSalesOrderDetail) {
		this.oidSalesOrderDetail = oidSalesOrderDetail;
	}

	public String getOidSupplier() {
		return (null != oidSupplier) ? oidSupplier : "NA";
	}

	public void setOidSupplier(String oidSupplier) {
		this.oidSupplier = oidSupplier;
	}

	public String getOidWeek() {
		return (null != oidWeek) ? oidWeek : "0";
	}

	public void setOidWeek(String oidWeek) {
		this.oidWeek = oidWeek;
	}

	public String getCanOrderMessage() {
		return (null != CanOrderMessage) ? CanOrderMessage : "NA";
	}

	public void setCanOrderMessage(String canOrderMessage) {
		CanOrderMessage = canOrderMessage;
	}

	public String getRequestID() {
		return (null != RequestID) ? RequestID : "NA";
	}

	public void setRequestID(String requestID) {
		RequestID = requestID;
	}

	public ExternalPartReservationDetail getExternalPartReservation() {
		return ExternalPartReservation;
	}

	public void setExternalPartReservation(ExternalPartReservationDetail externalPartReservation) {
		ExternalPartReservation = externalPartReservation;
	}

	public String getCopiedFrom() {
		return (null != CopiedFrom) ? CopiedFrom : "NA";
	}

	public void setCopiedFrom(String copiedFrom) {
		CopiedFrom = copiedFrom;
	}

	public OriginalOrderInfoDetail getOriginalOrderInfo() {
		return OriginalOrderInfo;
	}

	public void setOriginalOrderInfo(OriginalOrderInfoDetail originalOrderInfo) {
		OriginalOrderInfo = originalOrderInfo;
	}

	public List<? extends History> getHistory() {
		return History;
	}

	@SuppressWarnings("unchecked")
	public void setHistory(List<? extends History> history) {
		this.History = (List<History>) history;
	}

	public List<SelectionLines> getLines() {
		return Lines;
	}

	public void setLines(List<SelectionLines> lines) {
		Lines = lines;
	}

	public Map<String, String> getHeaderParameters() {
		return HeaderParameters;
	}

	public void setHeaderParameters(Map<String, String> headerParameters) {
		HeaderParameters = headerParameters;
	}

	public String getCount() {
		return (null != Count) ? Count : "0";
	}

	public void setCount(String count) {
		Count = count;
	}

	@Override
	public String toString() {

		return "SelectionRecord [uploadType=" + uploadType + ", _id=" + _id + ", Action=" + Action + ", Active="
				+ ("true".equalsIgnoreCase(Active) ? "1" : "0") + ", DateCreated=" + DateCreated + ", DateModified=" + DateModified
				+ ", DateToExpire=" + DateToExpire + ", ExpirationDate=" + ExpirationDate + ", FailReason=" + FailReason + ", FromDescription="
				+ FromDescription + ", LineCount=" + LineCount + ", ListDescription=" + ListDescription + ", ListType=" + ListType + ", OrderType="
				+ OrderType + ", RatifierID=" + RatifierID + ", SelectorID=" + SelectorID + ", WasNotified=" + WasNotified + ", oidApprovalCenter="
				+ oidApprovalCenter + ", oidApprovalPlan=" + oidApprovalPlan + ", oidContact=" + oidContact + ", oidCustomer=" + oidCustomer
				+ ", oidIDList=" + oidIDList + ", oidLibraryGroup=" + oidLibraryGroup + ", oidPartNo=" + oidPartNo + ", oidPromotion=" + oidPromotion
				+ ", oidSalesOrderDetail=" + oidSalesOrderDetail + ", oidSupplier=" + oidSupplier + ", oidWeek=" + oidWeek + ", CanOrderMessage="
				+ CanOrderMessage + ", OriginalOrderInfo=" + OriginalOrderInfo + ", RequestID=" + RequestID + ", ExternalPartReservation="
				+ ExternalPartReservation + ", CopiedFrom=" + CopiedFrom + ", History=" + History + ", Lines=" + Lines + ", TargetDetails="
				+ TargetDetails + ", HeaderParameters=" + HeaderParameters + "]";
	}

}
