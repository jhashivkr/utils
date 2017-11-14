package com.ibg.parsers.json;

public class TargetDetail {
	private String oidIDListTarget;
	private String oidContact;
	private String DaysHistoryStored;
	private String Destination;
	private String TargetListDescription;
	private String TargetListType;
	private String TargetOrderType;
	private String TargetFromDescription;
	private String TargetInitials;
	private String EmailAddress;
	private String EmailType;
	private String EmailFrequency;
	private String EmailRecommendFormat;

	public String getOidIDListTarget() {
		return (null != oidIDListTarget) ? oidIDListTarget : "NA";
	}

	public void setOidIDListTarget(String oidIDListTarget) {
		this.oidIDListTarget = oidIDListTarget;
	}

	public String getOidContact() {
		return (null != oidContact) ? oidContact : "NA";
	}

	public void setOidContact(String oidContact) {
		this.oidContact = oidContact;
	}

	public String getDaysHistoryStored() {
		return (null != DaysHistoryStored) ? DaysHistoryStored : "NA";
	}

	public void setDaysHistoryStored(String daysHistoryStored) {
		DaysHistoryStored = daysHistoryStored;
	}

	public String getDestination() {
		return (null != Destination) ? Destination : "NA";
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	public String getTargetListDescription() {
		return (null != TargetListDescription) ? TargetListDescription : "NA";
	}

	public void setTargetListDescription(String targetListDescription) {
		TargetListDescription = targetListDescription;
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

	public String getTargetFromDescription() {
		return (null != TargetFromDescription) ? TargetFromDescription : "NA";
	}

	public void setTargetFromDescription(String targetFromDescription) {
		TargetFromDescription = targetFromDescription;
	}

	public String getTargetInitials() {
		return (null != TargetInitials) ? TargetInitials : "NA";
	}

	public void setTargetInitials(String targetInitials) {
		TargetInitials = targetInitials;
	}

	public String getEmailAddress() {
		return (null != EmailAddress) ? EmailAddress : "NA";
	}

	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}

	public String getEmailType() {
		return (null != EmailType) ? EmailType : "NA";
	}

	public void setEmailType(String emailType) {
		EmailType = emailType;
	}

	public String getEmailFrequency() {
		return (null != EmailFrequency) ? EmailFrequency : "NA";
	}

	public void setEmailFrequency(String emailFrequency) {
		EmailFrequency = emailFrequency;
	}

	public String getEmailRecommendFormat() {
		return (null != EmailRecommendFormat) ? EmailRecommendFormat : "NA";
	}

	public void setEmailRecommendFormat(String emailRecommendFormat) {
		EmailRecommendFormat = emailRecommendFormat;
	}

	@Override
	public String toString() {
		return "TargetDetails [oidIDListTarget=" + oidIDListTarget + ", oidContact=" + oidContact + ", DaysHistoryStored=" + DaysHistoryStored
				+ ", Destination=" + Destination + ", TargetListDescription=" + TargetListDescription + ", TargetListType=" + TargetListType
				+ ", TargetOrderType=" + TargetOrderType + ", TargetFromDescription=" + TargetFromDescription + ", TargetInitials=" + TargetInitials
				+ ", EmailAddress=" + EmailAddress + ", EmailType=" + EmailType + ", EmailFrequency=" + EmailFrequency + ", EmailRecommendFormat="
				+ EmailRecommendFormat + "]";
	}

}
