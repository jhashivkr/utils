package com.ibg.parsers.json;

public class ExternalPartReservationDetail {

	private String SKU;
	private String Active;
	private String Description;
	private String Price;
	private String PriceNotes;
	private String Details;
	private String AlibrisSellPrice;
	private String Condition;
	private String Publisher;
	private String oidSupplier;
	private String SellerID;
	private String SellerName;
	private String SellerReliability;
	private String ShipsFrom;
	private String SellingPrice;
	private String SellingCurrency;
	private String Viewed;

	public String getSKU() {
		return (null != SKU) ? SKU : "0";
	}

	public void setSKU(String sKU) {
		SKU = sKU;
	}

	public String getActive() {
		return (null != Active) ? Active : "NA";
	}

	public void setActive(String active) {
		Active = active;
	}

	public String getDescription() {
		return (null != Description) ? Description : "NA";
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getPrice() {
		return (null != Price) ? Price  : "0";
	}

	public void setPrice(String price) {
		Price = price;
	}

	public String getPriceNotes() {
		return (null != PriceNotes) ? PriceNotes : "NA";
	}

	public void setPriceNotes(String priceNotes) {
		PriceNotes = priceNotes;
	}

	public String getDetails() {
		return (null != Details) ? Details : "NA";
	}

	public void setDetails(String details) {
		Details = details;
	}

	public String getAlibrisSellPrice() {
		return (null != AlibrisSellPrice) ? AlibrisSellPrice : "0";
	}

	public void setAlibrisSellPrice(String alibrisSellPrice) {
		AlibrisSellPrice = alibrisSellPrice;
	}

	public String getCondition() {
		return (null != Condition) ? Condition : "NA";
	}

	public void setCondition(String condition) {
		Condition = condition;
	}

	public String getPublisher() {
		return (null != Publisher) ? Publisher : "NA";
	}

	public void setPublisher(String publisher) {
		Publisher = publisher;
	}

	public String getOidSupplier() {
		return (null != oidSupplier) ? oidSupplier : "NA";
	}

	public void setOidSupplier(String oidSupplier) {
		this.oidSupplier = oidSupplier;
	}

	public String getSellerID() {
		return (null != SellerID) ? SellerID : "NA";
	}

	public void setSellerID(String sellerID) {
		SellerID = sellerID;
	}

	public String getSellerName() {
		return (null != SellerName) ? SellerName : "NA";
	}

	public void setSellerName(String sellerName) {
		SellerName = sellerName;
	}

	public String getSellerReliability() {
		return (null != SellerReliability) ? SellerReliability : "NA";
	}

	public void setSellerReliability(String sellerReliability) {
		SellerReliability = sellerReliability;
	}

	public String getShipsFrom() {
		return (null != ShipsFrom) ? ShipsFrom : "NA";
	}

	public void setShipsFrom(String shipsFrom) {
		ShipsFrom = shipsFrom;
	}

	public String getSellingPrice() {
		return (null != SellingPrice) ? SellingPrice : "0";
	}

	public void setSellingPrice(String sellingPrice) {
		SellingPrice = sellingPrice;
	}

	public String getSellingCurrency() {
		return (null != SellingCurrency) ? SellingCurrency : "NA";
	}

	public void setSellingCurrency(String sellingCurrency) {
		SellingCurrency = sellingCurrency;
	}

	public String getViewed() {
		return (null != Viewed) ? Viewed : "F";
	}

	public void setViewed(String viewed) {
		Viewed = viewed;
	}

	@Override
	public String toString() {
		return "ExternalPartReservationDetail [SKU=" + SKU + ", Active=" + Active + ", Description=" + Description + ", Price=" + Price
				+ ", PriceNotes=" + PriceNotes + ", Details=" + Details + ", AlibrisSellPrice=" + AlibrisSellPrice + ", Condition=" + Condition
				+ ", Publisher=" + Publisher + ", oidSupplier=" + oidSupplier + ", SellerID=" + SellerID + ", SellerName=" + SellerName
				+ ", SellerReliability=" + SellerReliability + ", ShipsFrom=" + ShipsFrom + ", SellingPrice=" + SellingPrice + ", SellingCurrency="
				+ SellingCurrency + ", Viewed=" + Viewed + "]";
	}

}
