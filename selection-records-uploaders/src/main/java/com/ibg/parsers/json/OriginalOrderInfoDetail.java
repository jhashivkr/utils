package com.ibg.parsers.json;

public class OriginalOrderInfoDetail {

	public String Title;
	public String Author;
	public String Publisher;
	public String PubYear;
	public String Edition;
	public String PartNo;
	public String Format;
	public String Price;
	public String Currency;
	public String Volume;
	public String sortPartNo;

	public String getTitle() {
		return (null != Title) ? Title : "NA";
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getAuthor() {
		return (null != Author) ? Author : "NA";
	}

	public void setAuthor(String author) {
		Author = author;
	}

	public String getPublisher() {
		return (null != Publisher) ? Publisher : "NA";
	}

	public void setPublisher(String publisher) {
		Publisher = publisher;
	}

	public String getPubYear() {
		return (null != PubYear) ? PubYear : "NA";
	}

	public void setPubYear(String pubYear) {
		PubYear = pubYear;
	}

	public String getEdition() {
		return (null != Edition) ? Edition : "NA";
	}

	public void setEdition(String edition) {
		Edition = edition;
	}

	public String getPartNo() {
		return (null != PartNo) ? PartNo : "NA";
	}

	public void setPartNo(String partNo) {
		PartNo = partNo;
	}

	public String getFormat() {
		return (null != Format) ? Format : "NA";
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getPrice() {
		return (null != Price) ? Price : "NA";
	}

	public void setPrice(String price) {
		Price = price;
	}

	public String getCurrency() {
		return (null != Currency) ? Currency : "NA";
	}

	public void setCurrency(String currency) {
		Currency = currency;
	}

	public String getVolume() {
		return (null != Volume) ? Volume : "NA";
	}

	public void setVolume(String volume) {
		Volume = volume;
	}

	public String getSortPartNo() {
		return (null != sortPartNo) ? sortPartNo : "NA";
	}

	public void setSortPartNo(String sortPartNo) {
		this.sortPartNo = sortPartNo;
	}

	@Override
	public String toString() {
		return "OriginalOrderInfoDetail [Title=" + Title + ", Author=" + Author + ", Publisher=" + Publisher + ", PubYear=" + PubYear + ", Edition="
				+ Edition + ", PartNo=" + PartNo + ", Format=" + Format + ", Price=" + Price + ", Currency=" + Currency + ", Volume=" + Volume
				+ ", sortPartNo=" + sortPartNo + "]";
	}

}
