package ibg.cc.mq.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class MQListenerStatController {

	private DefaultMessageListenerContainer listenerContainer1;
	private DefaultMessageListenerContainer listenerContainer2;

	public void startMessageListener1() {

		try {

			if (!listenerContainer1.isRecovering() && !listenerContainer1.isRunning()) {
				System.out.println(Thread.currentThread().getName() + " => turning listener 1 on");
				listenerContainer1.setMessageListener(new MessageListener());
				listenerContainer1.setAcceptMessagesWhileStopping(false);
				listenerContainer1.start();

				System.out.println("listener 1 started");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void startMessageListener2() {

		try {

			if (!listenerContainer2.isRecovering() && !listenerContainer2.isRunning()) {
				System.out.println(Thread.currentThread().getName() + " => turning listener 2 on");
				listenerContainer2.setMessageListener(new MessageListener());
				listenerContainer2.setAcceptMessagesWhileStopping(false);
				listenerContainer2.start();

				System.out.println("listener 2 started");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stopMessageListener1() {

		try {

			if (listenerContainer1.isActive() || listenerContainer1.isRunning() || listenerContainer1.isRecovering()) {
				System.out.println(Thread.currentThread().getName() + " => turning listener 1 off");
				listenerContainer1.stop();
				System.out.println("listener 1 stopped");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void stopMessageListener2() {

		try {

			if (listenerContainer2.isActive() || listenerContainer2.isRunning() || listenerContainer2.isRecovering()) {
				System.out.println(Thread.currentThread().getName() + " => turning listener 2 off");
				listenerContainer2.stop();
				System.out.println("listener 2 stopped");
			}

		} catch (Exception e) {
			e.printStackTrace();
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

	public boolean isListener1Running() {
		return this.listenerContainer1.isRunning();
	}

	public boolean isListener2Running() {
		return this.listenerContainer2.isRunning();
	}

	public boolean isListener1Active() {
		return this.listenerContainer1.isActive();
	}

	public boolean isListener2Active() {
		return this.listenerContainer2.isActive();
	}

	public boolean isListener1Recovering() {
		return this.listenerContainer1.isRecovering();
	}

	public boolean isListener2Recovering() {
		return this.listenerContainer2.isRecovering();
	}

}
