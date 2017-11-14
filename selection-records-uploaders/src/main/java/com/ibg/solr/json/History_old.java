package com.ibg.solr.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class History_old {

	@JsonIgnore
	private String DateTime;
	@JsonIgnore
	private String Event;
	@JsonIgnore
	private String Detail;
	@JsonIgnore
	private String Initials;
	@JsonIgnore
	private String TargetInitials;
	@JsonIgnore
	private String TargetListDescription;
	@JsonIgnore
	private String oidIDList;
	@JsonIgnore
	private String oidContact;
	@JsonIgnore
	private String TargetListType;
	@JsonIgnore
	private String TargetOrderType;
	@JsonIgnore
	private String IsRatifier;

	/**
	 * 
	 * 
	 RetainSelector GetActionDescription()
	 * 
	 * @return
	 */

	public String getDateTime() {
		return DateTime;
	}

	public void setDateTime(String dateTime) {
		DateTime = dateTime;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getDetail() {
		return Detail;
	}

	public void setDetail(String detail) {
		Detail = detail;
	}

	public String getInitials() {
		return Initials;
	}

	public void setInitials(String initials) {
		Initials = initials;
	}

	public String getTargetInitials() {
		return TargetInitials;
	}

	public void setTargetInitials(String targetInitials) {
		TargetInitials = targetInitials;
	}

	public String getTargetListDescription() {
		return TargetListDescription;
	}

	public void setTargetListDescription(String targetListDescription) {
		TargetListDescription = targetListDescription;
	}

	public String getOidIDList() {
		return oidIDList;
	}

	public void setOidIDList(String oidIDList) {
		this.oidIDList = oidIDList;
	}

	public String getOidContact() {
		return oidContact;
	}

	public void setOidContact(String oidContact) {
		this.oidContact = oidContact;
	}

	public String getTargetListType() {
		return TargetListType;
	}

	public void setTargetListType(String targetListType) {
		TargetListType = targetListType;
	}

	public String getTargetOrderType() {
		return TargetOrderType;
	}

	public void setTargetOrderType(String targetOrderType) {
		TargetOrderType = targetOrderType;
	}

	public String getIsRatifier() {
		return IsRatifier;
	}

	public void setIsRatifier(String isRatifier) {
		IsRatifier = isRatifier;
	}

	@Override
	public String toString() {
		return "History [DateTime=" + DateTime + ", Event=" + Event + ", Detail=" + Detail + ", Initials=" + Initials + ", TargetInitials="
				+ TargetInitials + ", TargetListDescription=" + TargetListDescription + ", oidIDList=" + oidIDList + ", oidContact=" + oidContact
				+ ", TargetListType=" + TargetListType + ", TargetOrderType=" + TargetOrderType + ", IsRatifier=" + IsRatifier + "]";
	}

}
