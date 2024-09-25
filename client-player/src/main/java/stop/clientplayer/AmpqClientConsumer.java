package stop.clientplayer;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import stop.clientplayer.Receiver;

@Component
public class AmpqClientConsumer {

    static final String fanoutExchangeName = "exchange_pontuacao";  // Certifique-se de que esta exchange já existe

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private Receiver receiver;

    private SimpleMessageListenerContainer container;

    public void setQueueNameAndStartListener(String queueName) {
        // Cria a fila ativamente (caso ela não exista) sem a exclusividade
        try {
            connectionFactory.createConnection().createChannel(false)
                    .queueDeclare(queueName, false, false, true, null);
            System.out.println("Fila " + queueName + " foi criada.");

            // Faz o binding da fila à exchange
            connectionFactory.createConnection().createChannel(false)
                    .queueBind(queueName, fanoutExchangeName, ""); // routingKey vazio para Fanout
            System.out.println("Fila " + queueName + " foi vinculada à exchange " + fanoutExchangeName);
        } catch (Exception e) {
            System.err.println("Erro ao criar a fila ou fazer o binding: " + e.getMessage());
        }

        // Inicia o listener dinamicamente
        container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter(receiver)); // Usa o listener dinamicamente
        container.start();

        System.out.println("Listener iniciado para a fila " + queueName);
    }

    @DependsOn
    public MessageListenerAdapter listenerAdapter(Receiver receiver) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage");
        adapter.setMessageConverter(new SimpleMessageConverter()); // Define o conversor de mensagens
        return adapter;
    }
}
