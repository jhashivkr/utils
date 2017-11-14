package ibg.cc.mq.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import ibg.cc.mq.message.TransactionMessage;
import ibg.cc.mq.writers.MqTransactionRecorder;
import ibg.lib.activity.ReqCallback;

public final class MQDBQueue<E> {

	private BlockingQueue<E> dbDataReqQ = new ArrayBlockingQueue<E>(100000);

	public final void addRequest(final E reqObj) {
		try {
			dbDataReqQ.put(reqObj);
			pickOneAndProcess();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	protected final <E> E getRequest() {
		try {
			return (E) dbDataReqQ.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return (E) new Object();
	}

	protected final boolean hasRequestObj() {
		return dbDataReqQ.isEmpty();
	}

	private final void pickOneAndProcess() {
				
		@SuppressWarnings("unchecked")
		ReqCallback reqCallBack = ()->{
			try {
					MQThreadExecutor.getReqFutures().add((Future<Runnable>) MQThreadExecutor.getDbExecutor().submit(new MqTransactionRecorder<TransactionMessage>((TransactionMessage) dbDataReqQ.take())));
				} catch (InterruptedException e) {
					e.printStackTrace();
					}
			};
		
		reqCallBack.executeNewTask();		
			
	}

}
