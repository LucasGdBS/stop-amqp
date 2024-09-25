package stop.clientplayer;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AmpqClientConsumer {
    static final String fanoutExchangeName = "fanout-exchange";
    static  String queueName;
    static final String routingKey = "rota-um";


    void set_queue_name(String nome){
        queueName = nome;
    }


    @Bean
    Queue queue() {
        return new Queue(queueName, false, true, true);
    }
    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange(fanoutExchangeName, false, true);
    }


    // Binding the queue to the exchange
    @Bean
    Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange());
    }

    // Configuring the container
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

}
