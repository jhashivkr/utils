package com.ibg.models;

import java.util.List;

public class AccountDataInfo {
	private String libGrp;
	private String libGrpFileName;
	private String customerFileName;
	private String usersFileName;
	private String userPrefFileName;
	private String orderInfoFileName;
	private String acdmListFileName;
	private String excelPrefFileName;
	private List<String> selectionRecFileName;

	public AccountDataInfo(String libGrp) {
		super();
		this.libGrp = libGrp;
	}

	/**
	 * @return the libGrp
	 */
	public String getLibGrp() {
		return libGrp;
	}

	/**
	 * @return the libGrpFileName
	 */
	public String getLibGrpFileName() {
		return libGrpFileName;
	}

	/**
	 * @param libGrpFileName
	 *            the libGrpFileName to set
	 */
	public void setLibGrpFileName(String libGrpFileName) {
		this.libGrpFileName = libGrpFileName;
	}

	/**
	 * @return the customerFileName
	 */
	public String getCustomerFileName() {
		return customerFileName;
	}

	/**
	 * @param customerFileName
	 *            the customerFileName to set
	 */
	public void setCustomerFileName(String customerFileName) {
		this.customerFileName = customerFileName;
	}

	/**
	 * @return the usersFileName
	 */
	public String getUsersFileName() {
		return usersFileName;
	}

	/**
	 * @param usersFileName
	 *            the usersFileName to set
	 */
	public void setUsersFileName(String usersFileName) {
		this.usersFileName = usersFileName;
	}

	/**
	 * @return the userPrefFileName
	 */
	public String getUserPrefFileName() {
		return userPrefFileName;
	}

	/**
	 * @param userPrefFileName
	 *            the userPrefFileName to set
	 */
	public void setUserPrefFileName(String userPrefFileName) {
		this.userPrefFileName = userPrefFileName;
	}

	/**
	 * @return the orderInfoFileName
	 */
	public String getOrderInfoFileName() {
		return orderInfoFileName;
	}

	/**
	 * @param orderInfoFileName
	 *            the orderInfoFileName to set
	 */
	public void setOrderInfoFileName(String orderInfoFileName) {
		this.orderInfoFileName = orderInfoFileName;
	}

	/**
	 * @return the acdmListFileName
	 */
	public String getAcdmListFileName() {
		return acdmListFileName;
	}

	/**
	 * @param acdmListFileName
	 *            the acdmListFileName to set
	 */
	public void setAcdmListFileName(String acdmListFileName) {
		this.acdmListFileName = acdmListFileName;
	}

	/**
	 * @return the excelPrefFileName
	 */
	public String getExcelPrefFileName() {
		return excelPrefFileName;
	}

	/**
	 * @param excelPrefFileName
	 *            the excelPrefFileName to set
	 */
	public void setExcelPrefFileName(String excelPrefFileName) {
		this.excelPrefFileName = excelPrefFileName;
	}

	/**
	 * @return the selectionRecFileName
	 */
	public List<String> getSelectionRecFileName() {
		return selectionRecFileName;
	}

	/**
	 * @param selectionRecFileName
	 *            the selectionRecFileName to set
	 */
	public void setSelectionRecFileName(List<String> selectionRecFileName) {
		this.selectionRecFileName = selectionRecFileName;
	}

}
