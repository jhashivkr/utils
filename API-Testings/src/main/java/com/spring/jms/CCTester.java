package com.spring.jms;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ibg.common.emsqueue.dto.MQReqField;
import ibg.customer.returnstatus.dto.CreditCardTransactionHist;
import ibg.customer.returnstatus.dto.MessageStatistics;
import ibg.cybersource.creditcard.customer.service.CreditCardCustomerProfileService;
import ibg.cybersource.creditcard.maintenance.service.MQListenerMgmtService;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class CCTester {

	public static void main(String[] args) {
		CCTester app = new CCTester();
		app.run();
	}

	/**
	 * Run the migration
	 */
	public void run() {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		//MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
		//		.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);

		//List<CreditCardTransactionHist> creditCardTransactionHistList = mqTransactionHistoryService.getAllTransactions();
		//System.out.println("creditCardTransactionHistList =>" + creditCardTransactionHistList);
		
		//Map<String, String> respTypes = mqTransactionHistoryService.getAllRespTypes();
		//System.out.println("all response types =>" + respTypes);
		
		//List<MessageStatistics> stats = mqTransactionHistoryService.getStatistics();
		//System.out.println("statistics =>" + stats);
		
		//String subsId = mqTransactionHistoryService.getPreAuthSubscriptionId("1037054_303177", "20G5464");
		//System.out.println("subsId =>" + subsId);
		
		//String subsIdnCartType = mqTransactionHistoryService.getPreAuthSubscriptionNCardTypeId("1037054_303177", "20G5464");
		//System.out.println("subsIdnCartType =>" + subsIdnCartType);
		
		//Map<String, Object> lastTranDetails = mqTransactionHistoryService.searchPreviousSuccessPreAuth("14454650030030", "20M6381", "000031.57||", "01EE51F8720M6381");
		//if(null != lastTranDetails && !lastTranDetails.isEmpty()){
		//	System.out.println("lastTranDetails: " + lastTranDetails);
		//}	
		
		MQListenerMgmtService mqListenerMgmtService = (MQListenerMgmtService) CreditCardServiceLocator
				.getService(CreditCardServiceLocator.ServiceName.MQ_LISTENER_SERVICE);
		
		System.out.println("Last Transaction: " + mqListenerMgmtService.getLastTransaction());
		
		mqListenerMgmtService.startListener1("sjha", "UI");
		
		
		
	}

}
