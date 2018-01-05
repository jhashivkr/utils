package ibg.lib.activity.browserowp.db.objects;

public final class JsonCheckStatusObj {

	private int priority;
	private String ean;
	private boolean CommunityGroup;
	private String browseRowMessage;
	private String consortiaShortName;
	private String userID;
	private String listName;
	private String listOwner;
	private String listType;
	private String oidSalesOrderDetail;

	private String actionRejectBoth;
	private String actionAllowed;
	private String actionAllowBlock;
	private String actionAllowClaim;
	private String lastReceived;
	private String netPrice;

	public JsonCheckStatusObj() {
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public boolean isCommunityGroup() {
		return CommunityGroup;
	}

	public void setCommunityGroup(boolean communityGroup) {
		CommunityGroup = communityGroup;
	}

	public String getBrowseRowMessage() {
		return browseRowMessage;
	}

	public void setBrowseRowMessage(String browseRowMessage) {
		this.browseRowMessage = browseRowMessage;
	}

	public String getConsortiaShortName() {
		return consortiaShortName;
	}

	public void setConsortiaShortName(String consortiaShortName) {
		this.consortiaShortName = consortiaShortName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getListOwner() {
		return listOwner;
	}

	public void setListOwner(String listOwner) {
		this.listOwner = listOwner;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public String getOidSalesOrderDetail() {
		return oidSalesOrderDetail;
	}

	public void setOidSalesOrderDetail(String oidSalesOrderDetail) {
		this.oidSalesOrderDetail = oidSalesOrderDetail;
	}

	public String getActionRejectBoth() {
		return actionRejectBoth;
	}

	public void setActionRejectBoth(String actionRejectBoth) {
		this.actionRejectBoth = actionRejectBoth;
	}

	public String getActionAllowed() {
		return actionAllowed;
	}

	public void setActionAllowed(String actionAllowed) {
		this.actionAllowed = actionAllowed;
	}

	public String getActionAllowBlock() {
		return actionAllowBlock;
	}

	public void setActionAllowBlock(String actionAllowBlock) {
		this.actionAllowBlock = actionAllowBlock;
	}

	public String getActionAllowClaim() {
		return actionAllowClaim;
	}

	public void setActionAllowClaim(String actionAllowClaim) {
		this.actionAllowClaim = actionAllowClaim;
	}

	public String getLastReceived() {
		return lastReceived;
	}

	public void setLastReceived(String lastReceived) {
		this.lastReceived = lastReceived;
	}

	public String getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(String netPrice) {
		this.netPrice = netPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ean == null) ? 0 : ean.hashCode());
		result = prime * result + ((listName == null) ? 0 : listName.hashCode());
		result = prime * result + priority;
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonCheckStatusObj other = (JsonCheckStatusObj) obj;
		if (ean == null) {
			if (other.ean != null)
				return false;
		} else if (!ean.equals(other.ean))
			return false;
		if (listName == null) {
			if (other.listName != null)
				return false;
		} else if (!listName.equals(other.listName))
			return false;
		if (priority != other.priority)
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JsonCheckStatusObj [priority=");
		builder.append(priority);
		builder.append(", ean=");
		builder.append(ean);
		builder.append(", CommunityGroup=");
		builder.append(CommunityGroup);
		builder.append(", browseRowMessage=");
		builder.append(browseRowMessage);
		builder.append(", consortiaShortName=");
		builder.append(consortiaShortName);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", listName=");
		builder.append(listName);
		builder.append(", listOwner=");
		builder.append(listOwner);
		builder.append(", listType=");
		builder.append(listType);
		builder.append(", oidSalesOrderDetail=");
		builder.append(oidSalesOrderDetail);
		builder.append(", actionRejectBoth=");
		builder.append(actionRejectBoth);
		builder.append(", actionAllowed=");
		builder.append(actionAllowed);
		builder.append(", actionAllowBlock=");
		builder.append(actionAllowBlock);
		builder.append(", actionAllowClaim=");
		builder.append(actionAllowClaim);
		builder.append(", netPrice=");
		builder.append(netPrice);
		builder.append("]");
		return builder.toString();
	}

}
