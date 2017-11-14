package com.ibg.parsers.json;

public enum SelectionAction {

	None(0, "None"), Block(1, "Block"), Claim(2, "Claim"), FacultyReceived(3, "FacultyReceived"), Forward(4, "Forward"), Rejected(5, "Rejected"), RejectedBoth(
			6, "RejectedBoth"), Order(7, "Order"), Tagged(8, "Tagged"), Cart(9, "Cart"), Recycle(10, "Recycle"), Accepted(11, "Accepted");

	private int code;
	private String action;

	SelectionAction(int code, String action) {
		this.setCode(code);
		this.setAction(action);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
}
