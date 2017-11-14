package ibg.cc.mq.writers;

import ibg.cc.mq.message.TransactionMessage;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class MqTransactionRecorder<E> implements Runnable {

	private TransactionMessage transactionMessage;

	public MqTransactionRecorder() {
	}

	public MqTransactionRecorder(E obj) {
		transactionMessage = (TransactionMessage) obj;
	}

	@Override
	public void run() {
		recordTransaction();
	}

	private void recordTransaction() {
		transactionMessage = transactionMessage.builder();
		MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
				.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);

		mqTransactionHistoryService.recordTransaction(transactionMessage);
	}

}
