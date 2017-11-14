package com.cc.log.analyze;

import java.util.Map;

import com.spring.jms.MQResponseObjectCreator;

import ibg.cc.mq.message.ResponseMessage;
import ibg.cc.mq.writers.MessageWriter;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.cybersource.creditcard.constants.CyberSourceConstants;
import ibg.cybersource.service.CreditCardServiceLocator;

public class WriteResponse {

	private static int writeCtr,ctr,nullResponses;
	
	public static void writeToResponseQueue(Map<String, String> line) {

		String correlId = "";
		int no = 0;
		CreditCardInfo ccInfo = Utilities.createCreditCardInfo(line, correlId);
		if (null == ccInfo) {
			return;
		}

		String tranType = ccInfo.getRequestField(MQReqField.TranType);
		Constants.tranTypes.add(tranType);

		MQResponseObjectCreator response = new MQResponseObjectCreator(ccInfo);
		ResponseMessage responseMessage = null;

		if (null != line.get("CSStatus")) {
			responseMessage = response.createPreAuthResponse(line.get("CSStatus").trim());
		} else {
			responseMessage = response.createPreAuthResponse(Constants.csError);
		}

		try {
			no = Constants.tranTypesStats.get(tranType);
			no++;
			Constants.tranTypesStats.put(tranType, no);
		} catch (Exception e) {
			Constants.tranTypesStats.put(tranType, 1);
		}

		if (CyberSourceConstants.CC_PREAUTH.equalsIgnoreCase(tranType)
				&& Constants.writeResponseTypes.contains(CyberSourceConstants.CC_PREAUTH)) {

			try {

				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(2);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ((CyberSourceConstants.CC_AUTHCHARGS.equalsIgnoreCase(tranType)
				|| CyberSourceConstants.CC_AUTHCHARGE.equalsIgnoreCase(tranType))
				&& (Constants.writeResponseTypes.contains(CyberSourceConstants.CC_AUTHCHARGS)
						|| Constants.writeResponseTypes.contains(CyberSourceConstants.CC_AUTHCHARGE))) {
			try {
				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(1);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (CyberSourceConstants.CC_SALE.equalsIgnoreCase(tranType)
				&& Constants.writeResponseTypes.contains(CyberSourceConstants.CC_SALE)) {

			try {
				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(1);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (CyberSourceConstants.CC_CREDIT.equalsIgnoreCase(tranType)
				&& Constants.writeResponseTypes.contains(CyberSourceConstants.CC_CREDIT)) {
			try {
				responseMessage.setMsgId(String.valueOf(ctr));
				responseMessage.setCorrelId(correlId.trim());
				responseMessage.setMessageType(1);

				// System.out.println("responseMessage: " + responseMessage);
				writeResponse(responseMessage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void writeResponse(ResponseMessage responseMessage) {
		if (null != responseMessage) {
			writeCtr++;
			MessageWriter messageWriter = (MessageWriter) CreditCardServiceLocator.getBean("messageWriter");
			messageWriter.setResponseMessage(responseMessage);
			messageWriter.send(ctr++);
		} else {
			nullResponses++;
		}

	}
}
