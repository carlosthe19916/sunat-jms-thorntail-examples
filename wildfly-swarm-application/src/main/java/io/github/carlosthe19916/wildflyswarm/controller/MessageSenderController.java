package io.github.carlosthe19916.wildflyswarm.controller;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@ApplicationScoped
@Path("/send-message")
public class MessageSenderController {

    @Inject
    @JMSConnectionFactory("java:/jms/remote-mq")
    private JMSContext context;

    @Resource(mappedName = "jms/queue/SunatQueue")
    private Queue queue;

    @GET
    @Produces("text/plain")
    public String sendMessage() throws JMSException {
        Destination replyDestination = context.createTemporaryQueue();

        TextMessage message = context.createTextMessage("Hello");
        message.setJMSReplyTo(replyDestination);

        JMSProducer producer = context.createProducer();
        producer.send(queue, message);

        JMSConsumer consumer = context.createConsumer(replyDestination);
        Message replyMessage = consumer.receive();

        System.out.println(replyMessage);

        return "Message sended";
    }
}
