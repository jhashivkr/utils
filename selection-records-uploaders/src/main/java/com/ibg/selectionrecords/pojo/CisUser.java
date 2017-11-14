package com.ibg.selectionrecords.pojo;

// Generated Nov 14, 2013 3:33:55 PM by Hibernate Tools 3.2.1.GA

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * CisUser generated by hbm2java
 */
public class CisUser implements java.io.Serializable {

	private String userId;
	private AplcUser aplcUser;
	private String administrator;
	private String checkoutType;
	private String checkoutValue;
	private BigDecimal contCancelOrder;
	private BigDecimal contClaimOrder;
	private BigDecimal contPlaceOrder;
	private String customerNo;
	private String defaultBudget;
	private String defaultLoanType;
	private String defaultLocationCode;
	private String defaultPurchase;
	private String department;
	private String displayReviews;
	private BigDecimal firmCancelOrder;
	private BigDecimal firmClaimOrder;
	private BigDecimal firmPlaceOrder;
	private String initials;
	private String institution;
	private String ispublic;
	private String lite;
	private String oidcustomer;
	private String position;
	private BigDecimal provisionalAccess;
	private String ratifier;
	private String ratifierPriority;
	private String recipientListing;
	private String reqRecipient;
	private String requestMethod;
	private String shibbolethid;
	private String solutionAccounts;
	private String webserviceClient;
	private Set<AcdmOriginalOrderInfo> acdmOriginalOrderInfos = new HashSet<AcdmOriginalOrderInfo>(0);
	private Set<AcdmExternalPartReservation> acdmExternalPartReservations = new HashSet<AcdmExternalPartReservation>(0);

	public CisUser() {
	}

	public CisUser(String userId, AplcUser aplcUser, String administrator, String customerNo) {
		this.userId = userId;
		this.aplcUser = aplcUser;
		this.administrator = administrator;
		this.customerNo = customerNo;
	}

	public CisUser(String userId, AplcUser aplcUser, String administrator, String checkoutType, String checkoutValue, BigDecimal contCancelOrder,
			BigDecimal contClaimOrder, BigDecimal contPlaceOrder, String customerNo, String defaultBudget, String defaultLoanType,
			String defaultLocationCode, String defaultPurchase, String department, String displayReviews, BigDecimal firmCancelOrder,
			BigDecimal firmClaimOrder, BigDecimal firmPlaceOrder, String initials, String institution, String ispublic, String lite,
			String oidcustomer, String position, BigDecimal provisionalAccess, String ratifier, String ratifierPriority, String recipientListing,
			String reqRecipient, String requestMethod, String shibbolethid, String solutionAccounts, String webserviceClient,
			Set<AcdmOriginalOrderInfo> acdmOriginalOrderInfos, Set<AcdmExternalPartReservation> acdmExternalPartReservations) {
		this.userId = userId;
		this.aplcUser = aplcUser;
		this.administrator = administrator;
		this.checkoutType = checkoutType;
		this.checkoutValue = checkoutValue;
		this.contCancelOrder = contCancelOrder;
		this.contClaimOrder = contClaimOrder;
		this.contPlaceOrder = contPlaceOrder;
		this.customerNo = customerNo;
		this.defaultBudget = defaultBudget;
		this.defaultLoanType = defaultLoanType;
		this.defaultLocationCode = defaultLocationCode;
		this.defaultPurchase = defaultPurchase;
		this.department = department;
		this.displayReviews = displayReviews;
		this.firmCancelOrder = firmCancelOrder;
		this.firmClaimOrder = firmClaimOrder;
		this.firmPlaceOrder = firmPlaceOrder;
		this.initials = initials;
		this.institution = institution;
		this.ispublic = ispublic;
		this.lite = lite;
		this.oidcustomer = oidcustomer;
		this.position = position;
		this.provisionalAccess = provisionalAccess;
		this.ratifier = ratifier;
		this.ratifierPriority = ratifierPriority;
		this.recipientListing = recipientListing;
		this.reqRecipient = reqRecipient;
		this.requestMethod = requestMethod;
		this.shibbolethid = shibbolethid;
		this.solutionAccounts = solutionAccounts;
		this.webserviceClient = webserviceClient;
		this.acdmOriginalOrderInfos = acdmOriginalOrderInfos;
		this.acdmExternalPartReservations = acdmExternalPartReservations;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public AplcUser getAplcUser() {
		return this.aplcUser;
	}

	public void setAplcUser(AplcUser aplcUser) {
		this.aplcUser = aplcUser;
	}

	public String getAdministrator() {
		return this.administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
	}

	public String getCheckoutType() {
		return this.checkoutType;
	}

	public void setCheckoutType(String checkoutType) {
		this.checkoutType = checkoutType;
	}

	public String getCheckoutValue() {
		return this.checkoutValue;
	}

	public void setCheckoutValue(String checkoutValue) {
		this.checkoutValue = checkoutValue;
	}

	public BigDecimal getContCancelOrder() {
		return this.contCancelOrder;
	}

	public void setContCancelOrder(BigDecimal contCancelOrder) {
		this.contCancelOrder = contCancelOrder;
	}

	public BigDecimal getContClaimOrder() {
		return this.contClaimOrder;
	}

	public void setContClaimOrder(BigDecimal contClaimOrder) {
		this.contClaimOrder = contClaimOrder;
	}

	public BigDecimal getContPlaceOrder() {
		return this.contPlaceOrder;
	}

	public void setContPlaceOrder(BigDecimal contPlaceOrder) {
		this.contPlaceOrder = contPlaceOrder;
	}

	public String getCustomerNo() {
		return this.customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getDefaultBudget() {
		return this.defaultBudget;
	}

	public void setDefaultBudget(String defaultBudget) {
		this.defaultBudget = defaultBudget;
	}

	public String getDefaultLoanType() {
		return this.defaultLoanType;
	}

	public void setDefaultLoanType(String defaultLoanType) {
		this.defaultLoanType = defaultLoanType;
	}

	public String getDefaultLocationCode() {
		return this.defaultLocationCode;
	}

	public void setDefaultLocationCode(String defaultLocationCode) {
		this.defaultLocationCode = defaultLocationCode;
	}

	public String getDefaultPurchase() {
		return this.defaultPurchase;
	}

	public void setDefaultPurchase(String defaultPurchase) {
		this.defaultPurchase = defaultPurchase;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDisplayReviews() {
		return this.displayReviews;
	}

	public void setDisplayReviews(String displayReviews) {
		this.displayReviews = displayReviews;
	}

	public BigDecimal getFirmCancelOrder() {
		return this.firmCancelOrder;
	}

	public void setFirmCancelOrder(BigDecimal firmCancelOrder) {
		this.firmCancelOrder = firmCancelOrder;
	}

	public BigDecimal getFirmClaimOrder() {
		return this.firmClaimOrder;
	}

	public void setFirmClaimOrder(BigDecimal firmClaimOrder) {
		this.firmClaimOrder = firmClaimOrder;
	}

	public BigDecimal getFirmPlaceOrder() {
		return this.firmPlaceOrder;
	}

	public void setFirmPlaceOrder(BigDecimal firmPlaceOrder) {
		this.firmPlaceOrder = firmPlaceOrder;
	}

	public String getInitials() {
		return this.initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getIspublic() {
		return this.ispublic;
	}

	public void setIspublic(String ispublic) {
		this.ispublic = ispublic;
	}

	public String getLite() {
		return this.lite;
	}

	public void setLite(String lite) {
		this.lite = lite;
	}

	public String getOidcustomer() {
		return this.oidcustomer;
	}

	public void setOidcustomer(String oidcustomer) {
		this.oidcustomer = oidcustomer;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public BigDecimal getProvisionalAccess() {
		return this.provisionalAccess;
	}

	public void setProvisionalAccess(BigDecimal provisionalAccess) {
		this.provisionalAccess = provisionalAccess;
	}

	public String getRatifier() {
		return this.ratifier;
	}

	public void setRatifier(String ratifier) {
		this.ratifier = ratifier;
	}

	public String getRatifierPriority() {
		return this.ratifierPriority;
	}

	public void setRatifierPriority(String ratifierPriority) {
		this.ratifierPriority = ratifierPriority;
	}

	public String getRecipientListing() {
		return this.recipientListing;
	}

	public void setRecipientListing(String recipientListing) {
		this.recipientListing = recipientListing;
	}

	public String getReqRecipient() {
		return this.reqRecipient;
	}

	public void setReqRecipient(String reqRecipient) {
		this.reqRecipient = reqRecipient;
	}

	public String getRequestMethod() {
		return this.requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getShibbolethid() {
		return this.shibbolethid;
	}

	public void setShibbolethid(String shibbolethid) {
		this.shibbolethid = shibbolethid;
	}

	public String getSolutionAccounts() {
		return this.solutionAccounts;
	}

	public void setSolutionAccounts(String solutionAccounts) {
		this.solutionAccounts = solutionAccounts;
	}

	public String getWebserviceClient() {
		return this.webserviceClient;
	}

	public void setWebserviceClient(String webserviceClient) {
		this.webserviceClient = webserviceClient;
	}

	public Set<AcdmOriginalOrderInfo> getAcdmOriginalOrderInfos() {
		return this.acdmOriginalOrderInfos;
	}

	public void setAcdmOriginalOrderInfos(Set<AcdmOriginalOrderInfo> acdmOriginalOrderInfos) {
		this.acdmOriginalOrderInfos = acdmOriginalOrderInfos;
	}

	public Set<AcdmExternalPartReservation> getAcdmExternalPartReservations() {
		return this.acdmExternalPartReservations;
	}

	public void setAcdmExternalPartReservations(Set<AcdmExternalPartReservation> acdmExternalPartReservations) {
		this.acdmExternalPartReservations = acdmExternalPartReservations;
	}

}
