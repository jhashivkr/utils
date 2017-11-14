package ibg.cc.mq.writers;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class RequestQMessageWriter {

	@Autowired
	private JmsTemplate mqTestMsgWriteTemplate;

	public RequestQMessageWriter() {
	}

	public void send() {

		mqTestMsgWriteTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage("09TESTHEALTH0000      HEALTH     CHK             10122015                                                          000000.00                                                                                                                                                                                                                                            TESTHL                                                        2815554933040176056176");
				
				message.setJMSCorrelationID("c44040404040404040404040404040404040404040404040");
				message.setJMSMessageID("000001");
				message.setStringProperty("JMS_IBM_Character_Set", "500");

				return message;

			}

		});
	}
}
