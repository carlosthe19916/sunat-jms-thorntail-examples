package io.github.carlosthe19916;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Application {

    public static void main(String... args) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        try {
            try {
                InitialContext initialContext = new InitialContext();
                Queue queue = (Queue) initialContext.lookup("jms/P2PQueue");
                connectionFactory = (ConnectionFactory) initialContext.lookup("jms/__defaultConnectionFactory");
                connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer(queue);
                TextMessage textMessage = session.createTextMessage(args[0]);
                messageProducer.send(textMessage);
            } finally {
                if (connection != null) connection.close();
            }
        } catch (NamingException e) {
            System.out.println("JNDI API lookup failed: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        } catch (JMSException e) {
            e.printStackTrace();
            System.exit(2);
        }

    }

}