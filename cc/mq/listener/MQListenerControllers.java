package ibg.cc.mq.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

import ibg.common.VariableData;

public class MQListenerControllers implements ApplicationListener<ContextRefreshedEvent> {

	private DefaultMessageListenerContainer listenerContainer1;
	private DefaultMessageListenerContainer listenerContainer2;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		startMessageConsumtion();

	}

	private void startMessageConsumtion() {

		String isListenerOn = "FALSE".intern();
		try{
			//isListenerOn = VariableData.getProperties().getProperty("jms.queue.listen");
			isListenerOn = System.getProperty("processCCMQueue");
			isListenerOn = null != isListenerOn ? isListenerOn.trim().toUpperCase() : "FALSE";
			System.out.println("isListenerOn: " + isListenerOn);
		}catch(Exception e){
			isListenerOn = "FALSE";
		}
		
		if (Boolean.parseBoolean(isListenerOn)) {
			System.out.println("turning listeners on");
			listenerContainer1.setMessageListener(new MessageListener());
			listenerContainer2.setMessageListener(new MessageListener());
			
			listenerContainer1.setAcceptMessagesWhileStopping(false);
			listenerContainer2.setAcceptMessagesWhileStopping(false);
			
			listenerContainer1.start();
			listenerContainer2.start();
			
			System.out.println("listeners started");
		}

	}

	/**
	 * @param listenerContainer1
	 *            the listenerContainer1 to set
	 */
	@Autowired
	public void setListenerContainer1(DefaultMessageListenerContainer listenerContainer1) {
		this.listenerContainer1 = listenerContainer1;
	}

	/**
	 * @param listenerContainer1
	 *            the listenerContainer1 to set
	 */
	@Autowired
	public void setListenerContainer2(DefaultMessageListenerContainer listenerContainer2) {
		this.listenerContainer2 = listenerContainer2;
	}
	
	public boolean isListener1Running(){
		return this.listenerContainer1.isRunning();
	}
	
	public boolean isListener2Running(){
		return this.listenerContainer2.isRunning();
	}
	
	public boolean isListener1Active(){
		return this.listenerContainer1.isActive();
	}
	
	public boolean isListener2Active(){
		return this.listenerContainer2.isActive();
	}

}
