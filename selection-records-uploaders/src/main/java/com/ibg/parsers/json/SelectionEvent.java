package com.ibg.parsers.json;

public enum SelectionEvent {

	Created(0, "Created"), SentToAcquisitions(1, "SentToAcquisitions"), FirmOrdered(2, "FirmOrdered"), Downloaded(3, "Downloaded"), Forwarded(4, "Forwarded"), Rejected(5, "Rejected"), FacultyRejected(
			6, "FacultyRejected"), FacultyRecommended(7, "FacultyRecommended"), Accepted(8, "Accepted"), Claimed(9, "Claimed"), AcceptedByUser(10, "AcceptedByUser"), RejectedByUser(11, "RejectedByUser");

	private int code;
	private String event;

	SelectionEvent(int code, String event) {
		this.setCode(code);
		this.setEvent(event);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

}
