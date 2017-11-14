package ibg.cc.mq.listener;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public enum RequestQueueInterface implements Serializable {

	MQReadQueue("reqQ") {
		@SuppressWarnings("unchecked")
		@Override
		public <T> DataQueue<T> getData() {
			// return (DataQueue<T>)weakReqQ.get();
			return (DataQueue<T>) reqQ;
		}
		
		@Override
		public <T> MQDBQueue<T> getDbData(){
			return null;
		}
	},	
	TranDbQueue("tranDbQ") {
		@SuppressWarnings("unchecked")
		@Override
		public <T> MQDBQueue<T> getDbData() {
			return (MQDBQueue<T>)weakDbReqQ.get();
			//return (MQDBQueue<T>) reqDbQ;
		}
		
		@Override
		public <T> DataQueue<T> getData() {
			return null;
		}
	};

	private RequestQueueInterface(String type) {
		this.type = type;
	}

	public abstract <T> DataQueue<T> getData();
	public abstract <T> MQDBQueue<T> getDbData();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static DataQueue<? extends MessageObject> reqQ = new DataQueue();
	private static WeakReference<DataQueue<? extends MessageObject>> weakReqQ = new WeakReference<DataQueue<? extends MessageObject>>(reqQ);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static MQDBQueue<? extends MessageObject> reqDbQ = new MQDBQueue();
	private static WeakReference<MQDBQueue<? extends MessageObject>> weakDbReqQ = new WeakReference<MQDBQueue<? extends MessageObject>>(reqDbQ);

	private String type;

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "TYPE CODE -> " + type;
	}

}
