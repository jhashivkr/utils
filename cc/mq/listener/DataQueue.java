package ibg.cc.mq.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import ibg.cc.mq.readers.MessageProcessor;
import ibg.lib.activity.ReqCallback;

public final class DataQueue<E> {

	private BlockingQueue<E> dataReqQ = new ArrayBlockingQueue<E>(1000000);

	public final void addRequest(final E reqObj) {
		try {
			dataReqQ.put(reqObj);

			@SuppressWarnings("unchecked")
			ReqCallback reqCallback = () -> {

				try {
					MQThreadExecutor.getReqFutures().add((Future<Runnable>) MQThreadExecutor.getqExecutor()
							.submit(new MessageProcessor<MessageObject>((MessageObject) dataReqQ.take())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
			
			reqCallback.executeNewTask();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	protected final <E> E getRequest() {
		try {
			return (E) dataReqQ.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return (E) new Object();
	}


	protected final boolean hasRequestObj() {
		return dataReqQ.isEmpty();
	}


}
