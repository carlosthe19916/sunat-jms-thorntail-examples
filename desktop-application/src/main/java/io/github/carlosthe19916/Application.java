package io.github.carlosthe19916;

import java.util.logging.Logger;
import java.util.Properties;
import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Application {

    private static final Logger log = Logger.getLogger(Application.class.getName());

    // Set up all the default values
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/queue/SunatQueue";

    private static final String INITIAL_CONTEXT_FACTORY = "org.jboss.naming.remote.client.InitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://localhost:8080";

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        Destination destination = null;
        TextMessage message = null;
        Context context = null;

        try {
            // Set up the context for the JNDI lookup
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
            env.put(Context.SECURITY_PRINCIPAL, "admin");
            env.put(Context.SECURITY_CREDENTIALS, "admin");
            context = new InitialContext(env);

            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.log(Level.INFO, "Attempting to acquire connection factory \"{0}\"", connectionFactoryString);
            connectionFactory = (ConnectionFactory) context.lookup(connectionFactoryString);
            log.log(Level.INFO, "Found connection factory \"{0}\" in JNDI", connectionFactoryString);

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.log(Level.INFO, "Attempting to acquire destination \"{0}\"", destinationString);
            destination = (Destination) context.lookup(destinationString);
            log.log(Level.INFO, "Found destination \"{0}\" in JNDI", destinationString);

            // Create the JMS connection, session, producer
            // make sure to add the password created in Main WFS startup
            connection = connectionFactory.createConnection("admin", "password");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);

            connection.start();

            log.info("Sending message");

            message = session.createTextMessage("something");
            producer.send(message);

        } catch (NamingException | JMSException | NumberFormatException e) {
            log.severe(e.getMessage());
            throw e;
        } finally {
            if (context != null) {
                context.close();
            }

            // closing the connection takes care of the session, producer, and consumer
            if (connection != null) {
                connection.close();
            }
        }
    }

}