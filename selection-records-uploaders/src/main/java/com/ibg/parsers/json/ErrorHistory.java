package com.ibg.parsers.json;

public class ErrorHistory extends History {

	private String oidIDList;
	private String oidContact;

	public String getOidIDList() {
		return (null != oidIDList) ? oidIDList : "NA";
	}

	public void setOidIDList(String oidIDList) {

		this.oidIDList = oidIDList;
	}

	public String getOidContact() {
		return (null != oidContact) ? oidContact : "NA";
	}

	public void setOidContact(String oidContact) {
		this.oidContact = oidContact;
	}

	@Override
	public String toString() {
		return "ErrorHistory [oidIDList=" + oidIDList + ", oidContact=" + oidContact + ", getDateTime()=" + getDateTime() + ", getEvent()="
				+ getEvent() + ", getDetail()=" + getDetail() + ", getInitials()=" + getInitials() + ", getTargetInitials()=" + getTargetInitials()
				+ ", getTargetListDescription()=" + getTargetListDescription() + ", getTargetListType()=" + getTargetListType()
				+ ", getTargetOrderType()=" + getTargetOrderType() + ", getIsRatifier()=" + getIsRatifier() + "]";
	}

	

}
