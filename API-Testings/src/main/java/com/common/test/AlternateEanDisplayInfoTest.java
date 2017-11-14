package com.common.test;

import ibg.product.information.EBPDataObject;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class AlternateEanDisplayInfoTest {

	private String productEan;
	private boolean eblEligible;
	private boolean ebscoEligible;
	private boolean milEligible;
	private boolean ebraryEligible;

	private String availableEbl;
	private String availableEbrary;
	private String availableEbsco;
	private String availableMil;

	private Set<EBPDataObject> availableEblList;
	private Set<EBPDataObject> availableEbraryList;
	private Set<EBPDataObject> availableEbscoList;
	private Set<EBPDataObject> availableMilList;

	private boolean ebookDownloadable;
	private String ebookTextDisplay;
	private String dispEbookCur;
	private String dispEbookPrice;
	private String dispEbookLicense;

	public AlternateEanDisplayInfoTest() {

	}

	/**
	 * @return the productEan
	 */
	public String getProductEan() {
		return productEan;
	}

	/**
	 * @param productEan
	 *            the productEan to set
	 */
	public void setProductEan(String productEan) {
		this.productEan = productEan;
	}

	/**
	 * @return the eblEligible
	 */
	public boolean isEblEligible() {
		return eblEligible;
	}

	/**
	 * @param eblEligible
	 *            the eblEligible to set
	 */
	public void setEblEligible(boolean eblEligible) {
		this.eblEligible = eblEligible;
	}

	/**
	 * @return the ebscoEligible
	 */
	public boolean isEbscoEligible() {
		return ebscoEligible;
	}

	/**
	 * @param ebscoEligible
	 *            the ebscoEligible to set
	 */
	public void setEbscoEligible(boolean ebscoEligible) {
		this.ebscoEligible = ebscoEligible;
	}

	/**
	 * @return the milEligible
	 */
	public boolean isMilEligible() {
		return milEligible;
	}

	/**
	 * @param milEligible
	 *            the milEligible to set
	 */
	public void setMilEligible(boolean milEligible) {
		this.milEligible = milEligible;
	}

	/**
	 * @return the ebraryEligible
	 */
	public boolean isEbraryEligible() {
		return ebraryEligible;
	}

	/**
	 * @param ebraryEligible
	 *            the ebraryEligible to set
	 */
	public void setEbraryEligible(boolean ebraryEligible) {
		this.ebraryEligible = ebraryEligible;
	}

	/**
	 * @return the availableEbl
	 */
	public String getAvailableEbl() {
		availableEbl = "";
		if (null != getAvailableEblList() && !getAvailableEblList().isEmpty()) {
			availableEbl = StringUtils.join(getAvailableEblList(), ' ');
		}
		return availableEbl;
	}

	/**
	 * @param availableEbl
	 *            the availableEbl to set
	 */
	public void setAvailableEbl(String availableEbl) {
		this.availableEbl = availableEbl;
	}

	/**
	 * @return the availableEbrary
	 */
	public String getAvailableEbrary() {
		availableEbrary = "";
		if (null != getAvailableEbraryList() && !getAvailableEbraryList().isEmpty()) {
			availableEbrary = StringUtils.join(getAvailableEbraryList(), ' ');
		}
		return availableEbrary;
	}

	/**
	 * @param availableEbrary
	 *            the availableEbrary to set
	 */
	public void setAvailableEbrary(String availableEbrary) {
		this.availableEbrary = availableEbrary;
	}

	/**
	 * @return the availableEbsco
	 */
	public String getAvailableEbsco() {
		availableEbsco = "";
		if (null != getAvailableEbscoList() && !getAvailableEbscoList().isEmpty()) {
			availableEbsco = StringUtils.join(getAvailableEbscoList(), ' ');
		}
		return availableEbsco;
	}

	/**
	 * @param availableEbsco
	 *            the availableEbsco to set
	 */
	public void setAvailableEbsco(String availableEbsco) {
		this.availableEbsco = availableEbsco;
	}

	/**
	 * @return the availableMil
	 */
	public String getAvailableMil() {
		availableMil = "";
		if (null != getAvailableMilList() && !getAvailableMilList().isEmpty()) {
			availableMil = StringUtils.join(getAvailableMilList(), ' ');
		}
		return availableMil;
	}

	/**
	 * @param availableMil
	 *            the availableMil to set
	 */
	public void setAvailableMil(String availableMil) {
		this.availableMil = availableMil;
	}

	/**
	 * @return the availableEblList
	 */
	public Set<EBPDataObject> getAvailableEblList() {
		return availableEblList;
	}

	/**
	 * @param availableEblList
	 *            the availableEblList to set
	 */
	public void setAvailableEblList(Set<EBPDataObject> availableEblList) {
		this.availableEblList = availableEblList;
	}

	/**
	 * @return the availableEbraryList
	 */
	public Set<EBPDataObject> getAvailableEbraryList() {
		return availableEbraryList;
	}

	/**
	 * @param availableEbraryList
	 *            the availableEbraryList to set
	 */
	public void setAvailableEbraryList(Set<EBPDataObject> availableEbraryList) {
		this.availableEbraryList = availableEbraryList;
	}

	/**
	 * @return the availableEbscoList
	 */
	public Set<EBPDataObject> getAvailableEbscoList() {
		return availableEbscoList;
	}

	/**
	 * @param availableEbscoList
	 *            the availableEbscoList to set
	 */
	public void setAvailableEbscoList(Set<EBPDataObject> availableEbscoList) {
		this.availableEbscoList = availableEbscoList;
	}

	/**
	 * @return the availableMilList
	 */
	public Set<EBPDataObject> getAvailableMilList() {
		return availableMilList;
	}

	/**
	 * @param availableMilList
	 *            the availableMilList to set
	 */
	public void setAvailableMilList(Set<EBPDataObject> availableMilList) {
		this.availableMilList = availableMilList;
	}

	/**
	 * @return the ebookDownloadable
	 */
	public boolean isEbookDownloadable() {
		return ebookDownloadable;
	}

	/**
	 * @param ebookDownloadable
	 *            the ebookDownloadable to set
	 */
	public void setEbookDownloadable(boolean ebookDownloadable) {
		this.ebookDownloadable = ebookDownloadable;
	}

	/**
	 * @return the ebookTextDisplay
	 */
	public String getEbookTextDisplay() {
		return ebookTextDisplay;
	}

	/**
	 * @param ebookTextDisplay
	 *            the ebookTextDisplay to set
	 */
	public void setEbookTextDisplay(String ebookTextDisplay) {
		this.ebookTextDisplay = ebookTextDisplay;
	}

	/**
	 * @return the dispEbookCur
	 */
	public String getDispEbookCur() {
		return dispEbookCur;
	}

	/**
	 * @param dispEbookCur
	 *            the dispEbookCur to set
	 */
	public void setDispEbookCur(String dispEbookCur) {
		this.dispEbookCur = dispEbookCur;
	}

	/**
	 * @return the dispEbookPrice
	 */
	public String getDispEbookPrice() {
		return dispEbookPrice;
	}

	/**
	 * @param dispEbookPrice
	 *            the dispEbookPrice to set
	 */
	public void setDispEbookPrice(String dispEbookPrice) {
		this.dispEbookPrice = dispEbookPrice;
	}

	/**
	 * @return the dispEbookLicense
	 */
	public String getDispEbookLicense() {
		return dispEbookLicense;
	}

	/**
	 * @param dispEbookLicense
	 *            the dispEbookLicense to set
	 */
	public void setDispEbookLicense(String dispEbookLicense) {
		this.dispEbookLicense = dispEbookLicense;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AlternateEanDisplayInfoTest [productEan=" + productEan + ", eblEligible=" + eblEligible + ", ebscoEligible=" + ebscoEligible + ", milEligible=" + milEligible + ", ebraryEligible="
				+ ebraryEligible + ", availableEbl=" + availableEbl + ", availableEbrary=" + availableEbrary + ", availableEbsco=" + availableEbsco + ", availableMil=" + availableMil
				+ ", availableEblList=" + availableEblList + ", availableEbraryList=" + availableEbraryList + ", availableEbscoList=" + availableEbscoList + ", availableMilList=" + availableMilList
				+ ", ebookDownloadable=" + ebookDownloadable + ", ebookTextDisplay=" + ebookTextDisplay + ", dispEbookCur=" + dispEbookCur + ", dispEbookPrice=" + dispEbookPrice
				+ ", dispEbookLicense=" + dispEbookLicense + "]";
	}

}
