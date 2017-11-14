package com.ibg.parsers.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class History {

	private DateField DateTime;
	private SelectionEvent Event;
	private String Detail;
	private String Initials;
	private String TargetInitials;
	private String TargetListDescription;
	private String oidIDList;
	@JsonIgnore
	private String oidContact;
	private String TargetListType;
	private String TargetOrderType;
	private String IsRatifier;

	/**
	 * 
	 * 
	 RetainSelector GetActionDescription()
	 * 
	 * @return
	 */
	public DateField getDateTime() {
		return DateTime;
	}

	public void setDateTime(DateField dateTime) {
		DateTime = dateTime;
	}

	public SelectionEvent getEvent() {
		return (null != Event) ? Event : SelectionEvent.Created;
	}

	public void setEvent(SelectionEvent event) {
		Event = event;
	}

	public String getDetail() {
		return (null != Detail) ? Detail : "NA";
	}

	public void setDetail(String detail) {
		Detail = detail;
	}

	public String getInitials() {
		return (null != Initials) ? Initials : "NA";
	}

	public void setInitials(String initials) {
		Initials = initials;
	}

	public String getTargetInitials() {
		return (null != TargetInitials) ? TargetInitials : "NA";
	}

	public void setTargetInitials(String targetInitials) {
		TargetInitials = targetInitials;
	}

	public String getTargetListDescription() {
		return (null != TargetListDescription) ? TargetListDescription : "NA";
	}

	public void setTargetListDescription(String targetListDescription) {
		TargetListDescription = targetListDescription;
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

	public String getOidContact() {
		return (null != oidContact) ? oidContact : "NA";
	}

	public void setOidContact(String oidContact) {
		this.oidContact = oidContact;
	}

	public String getTargetListType() {
		return (null != TargetListType) ? TargetListType : "NA";
	}

	public void setTargetListType(String targetListType) {
		TargetListType = targetListType;
	}

	public String getTargetOrderType() {
		return (null != TargetOrderType) ? TargetOrderType : "NA";
	}

	public void setTargetOrderType(String targetOrderType) {
		TargetOrderType = targetOrderType;
	}

	public String getIsRatifier() {
		return (null != IsRatifier) ? IsRatifier : "F";
	}

	public void setIsRatifier(String isRatifier) {
		IsRatifier = isRatifier;
	}

	@Override
	public String toString() {
		return "History [DateTime="
				+ (null != DateTime ? DateTime.toString() : "No Val")
				+ ", Event=" + Event + ", Detail=" + Detail + ", Initials="
				+ Initials + ", TargetInitials=" + TargetInitials
				+ ", TargetListDescription=" + TargetListDescription
				+ ", oidIDList=" + oidIDList + ", oidContact=" + oidContact
				+ ", TargetListType=" + TargetListType + ", TargetOrderType="
				+ TargetOrderType + ", IsRatifier=" + IsRatifier + "]";
	}

}
