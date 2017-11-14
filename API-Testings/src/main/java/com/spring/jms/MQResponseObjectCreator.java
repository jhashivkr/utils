package com.spring.jms;

import java.nio.ByteBuffer;
import java.util.Map;

import ibg.cc.mq.message.ResponseMessage;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.common.emsqueue.dto.MQRspField;
import ibg.cybersource.creditcard.constants.CyberSourceConstants;
import ibg.cybersource.creditcard.customer.service.CreditCardCustomerProfileService;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class MQResponseObjectCreator {

	private String[] csResponse;
	private ResponseMessage responseMessage;
	private CreditCardInfo ccInfo;

	public MQResponseObjectCreator(CreditCardInfo ccInfo) {
		responseMessage = new ResponseMessage();
		if (null == ccInfo.getResponse()) {
			ccInfo.setResponse(ByteBuffer.allocate(CreditCardInfo.MQRspLenV1));
		}
		for (MQRspField myRF : MQRspField.values()) {
			ccInfo.setResponseField(myRF, "");
		}
		this.ccInfo = ccInfo;
		fetchSubscriptionDetails();
	}

	public ResponseMessage createPreAuthResponse(String line) {

		try {

			if (null != line && !line.isEmpty()) {
				csResponse = line.split("\\|");
			}
			setCommonResponseField(csResponse[1], csResponse[0], "");

			// TODO After Adding Subscription ID to the CCAUTHresp Object
			// assign it to below code
			if (ccInfo.getResponseField(MQRspField.Resp_Code).equalsIgnoreCase("A")) {
				ccInfo.setResponseField(MQRspField.SubscriptionStatus, "A");
			}

			responseMessage.setMsgBody(ccInfo.getResponse());

			return responseMessage;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;

		}
	}

	public ResponseMessage createAuthChargeResponse(String line) {

		try {

			responseMessage = new ResponseMessage();
			if (null != line && !line.isEmpty()) {
				csResponse = line.split("|");
			}
			setCommonResponseField(csResponse[0], csResponse[1], "");

			responseMessage.setMsgBody(ccInfo.getResponse());
			return responseMessage;
		} catch (Exception e) {
			return null;
		}
	}

	public ResponseMessage createSaleResponse(String line) {
		try {

			responseMessage = new ResponseMessage();

			if (null != line && !line.isEmpty()) {
				csResponse = line.split("|");
			}
			setCommonResponseField(csResponse[0], csResponse[1], "");

			if (ccInfo.getResponseField(MQRspField.Resp_Code).equalsIgnoreCase("A")) {
				ccInfo.setResponseField(MQRspField.SubscriptionStatus, "A");
			}
			responseMessage.setMsgBody(ccInfo.getResponse());
			return responseMessage;
		} catch (Exception e) {
			return null;
		}
	}

	public ResponseMessage createCreditResponse(String line) {
		/*
		 * You can only do a credit on a subscription or on the Card with
		 * billing info. Credits cannot be run on a request ID.
		 **/
		try {

			responseMessage = new ResponseMessage();

			if (null != line && !line.isEmpty()) {
				csResponse = line.split("|");
			}
			setCommonResponseField(csResponse[0], csResponse[1], "");

			if (ccInfo.getResponseField(MQRspField.Resp_Code).equalsIgnoreCase("A")) {
				ccInfo.setResponseField(MQRspField.SubscriptionStatus, "A");
			}
			responseMessage.setMsgBody(ccInfo.getResponse());

			return responseMessage;
		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * @param ccInfo
	 * @return This method deals with the basic response message parameter that
	 *         are set from request parameter
	 */
	private void initializeResponse() {

		ccInfo.setResponseField(MQRspField.Ver, "01");
		if (ccInfo.getRequestField(MQReqField.TieBack) != null) {
			ccInfo.setResponseField(MQRspField.TieBack, ccInfo.getRequestField(MQReqField.TieBack));
		}
		if (ccInfo.getRequestField(MQReqField.TranType) != null) {
			ccInfo.setResponseField(MQRspField.TranType, ccInfo.getRequestField(MQReqField.TranType));
		}
		if (ccInfo.getRequestField(MQReqField.Division) != null) {
			ccInfo.setResponseField(MQRspField.Division, ccInfo.getRequestField(MQReqField.Division));
		}
		if (ccInfo.getRequestField(MQReqField.Merch_Bill_To) != null) {
			ccInfo.setResponseField(MQRspField.Merch_Bill_To, ccInfo.getRequestField(MQReqField.Merch_Bill_To));
		}
	}

	private void setCommonResponseField(String ccDecision, String ccReasonCode, String reqID) {
		initializeResponse();

		String valitionError = "Validation Error";

		try {
			if (null != ccReasonCode) {
				ccInfo.setResponseField(MQRspField.CCRespCode, ccReasonCode.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (null != ccDecision) {
				ccDecision = ccDecision.trim();
				ccInfo.setResponseField(MQRspField.CCRespText, ccDecision);
				if (null != reqID) {
					ccInfo.setResponseField(MQRspField.CCRequestID, reqID.trim());
				}

				if (ccDecision.equalsIgnoreCase(CyberSourceConstants.RESPONSE_ACCEPTED)) {
					ccInfo.setResponseField(MQRspField.Resp_Code, "A");

				} else if (ccDecision.equalsIgnoreCase(CyberSourceConstants.RESPONSE_ERROR)) {
					ccInfo.setResponseField(MQRspField.Resp_Code, "F");
				} else if (ccDecision.equalsIgnoreCase(CyberSourceConstants.RESPONSE_REJECT)) {
					ccInfo.setResponseField(MQRspField.Resp_Code, "R");
				} else if (ccDecision.equalsIgnoreCase(CyberSourceConstants.RESPONSE_REVIEW)) {
					ccInfo.setResponseField(MQRspField.Resp_Code, "F");
				} else {
					ccInfo.setResponseField(MQRspField.Resp_Code, "F");
				}

				// ccInfo.setResponseField(MQRspField.CCRespText, ccResponseMsg);
			} else {
				ccInfo.setResponseField(MQRspField.Resp_Code, "F");
				ccInfo.setResponseField(MQRspField.CCRespText, valitionError);
			}
		} catch (Exception e) {

			ccInfo.setResponseField(MQRspField.Resp_Code, "F");
			ccInfo.setResponseField(MQRspField.CCRespText, valitionError);
		}

	}

	private void fetchSubscriptionDetails() {
		String subId = null;
		Map<String, String> subsIdMap = null;
		boolean subsIdFoundFlag = false;
		String merchRefCode = "";

		if (null != ccInfo.getRequestField(MQReqField.Card_Bill_To)
				&& !ccInfo.getRequestField(MQReqField.Card_Bill_To).trim().isEmpty()) {
			merchRefCode = ccInfo.getRequestField(MQReqField.Card_Bill_To);
		} else {
			merchRefCode = ccInfo.getRequestField(MQReqField.Merch_Bill_To);
		}

		// check if merchant reference code is still blank
		if (null == merchRefCode || merchRefCode.trim().isEmpty()) {
			merchRefCode = randomAlphaNumeric(6);
		}
		
		if (null != ccInfo.getRequestField(MQReqField.Subscript_Id)
				&& !ccInfo.getRequestField(MQReqField.Subscript_Id).trim().isEmpty()) {
			ccInfo.setResponseField(MQRspField.SubscriptionID, ccInfo.getRequestField(MQReqField.Subscript_Id));
			subsIdFoundFlag = true;
		} else if (null != ccInfo.getRequestField(MQReqField.Card_Bill_To)	&& null != ccInfo.getRequestField(MQReqField.Division)) {
			try {
				CreditCardCustomerProfileService creditCardCustomerProfileSrvc = (CreditCardCustomerProfileService) CreditCardServiceLocator
						.getService(CreditCardServiceLocator.ServiceName.CRECRD_CUSTOMER_PROFILE_SERVICE);
				subsIdMap = creditCardCustomerProfileSrvc.getSubscriptionIDInfo(ccInfo);
		
				
				if ((null != subsIdMap && !subsIdMap.isEmpty())) {
					subsIdFoundFlag = true;
					for (String subid : subsIdMap.keySet()){
						ccInfo.setResponseField(MQRspField.SubscriptionID, subid);
						ccInfo.setResponseField(MQRspField.Card_Type, subsIdMap.get(subid));
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				subsIdMap = null;
				subsIdFoundFlag = false;
			}
		}

		// if it doesnot have a subscription id and the bill to account is
		// taschen (20G5464)
		// then pick up the subscription id from the transaction history
		// table
		// matching the CUST_PO_ID with a preauth TRAN_TP_DN
		if (!subsIdFoundFlag) {
			if (CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(ccInfo.getRequestField(MQReqField.TranType))
					|| CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(ccInfo.getRequestField(MQReqField.TranType))
					|| CyberSourceConstants.CC_SALE.equalsIgnoreCase(ccInfo.getRequestField(MQReqField.TranType))) {

				MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
						.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
				String subsId = mqTransactionHistoryService.getPreAuthSubscriptionId(ccInfo.getRequestField(MQReqField.Cust_PO), merchRefCode);

				if (null != subsId && !subsId.isEmpty()) {
					ccInfo.setResponseField(MQRspField.SubscriptionID, subsId);
					ccInfo.setResponseField(MQRspField.Card_Type, subsIdMap.get(subId));
				}

			}
		}
		
	}

	// To get the alphanumeric random String of desire length.
	// @count length of string required
	private static String randomAlphaNumeric(int count) {

		String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		StringBuilder builder = new StringBuilder();

		while (count-- != 0) {

			int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());

			builder.append(ALPHA_NUMERIC_STRING.charAt(character));

		}

		return builder.toString();

	}

}
