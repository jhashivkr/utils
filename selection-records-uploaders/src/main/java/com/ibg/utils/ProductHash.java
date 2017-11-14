package com.ibg.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ProductHash {

	private Long srcListId;
	private Long destListId;
	private Long ean;
	private long timestamp;

	public ProductHash(Long srcListId, Long destListId, Long ean) {
		super();
		this.srcListId = srcListId;
		this.destListId = destListId;
		this.ean = ean;

		Calendar calendar = new GregorianCalendar();
		this.timestamp = calendar.getTimeInMillis();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destListId == null) ? 0 : destListId.hashCode());
		result = prime * result + ((ean == null) ? 0 : ean.hashCode());
		result = prime * result + ((srcListId == null) ? 0 : srcListId.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductHash other = (ProductHash) obj;
		if (destListId == null) {
			if (other.destListId != null)
				return false;
		} else if (!destListId.equals(other.destListId))
			return false;
		if (ean == null) {
			if (other.ean != null)
				return false;
		} else if (!ean.equals(other.ean))
			return false;
		if (srcListId == null) {
			if (other.srcListId != null)
				return false;
		} else if (!srcListId.equals(other.srcListId))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProductHash [srcListId=" + srcListId + ", destListId=" + destListId + ", ean=" + ean + ", timestamp=" + timestamp + "]";
	}
	
	

}
