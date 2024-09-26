package clientside;

import io.github.cdimascio.dotenv.Dotenv;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class AmqpChannel {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String HOST = dotenv.get("HOST");
    private static final int PORT = Integer.parseInt(dotenv.get("PORT"));
    private static final String VIRTUAL_HOST = dotenv.get("VIRTUAL_HOST");
    private static final String RABBIT_USERNAME = dotenv.get("RABBIT_USERNAME");
    private static final String PASSWORD = dotenv.get("PASSWORD");
    public Channel channel;

    public AmqpChannel() {
        this.channel = createChannel();
    }

    private Channel createChannel() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setVirtualHost(VIRTUAL_HOST);
        factory.setUsername(RABBIT_USERNAME);
        factory.setPassword(PASSWORD);

        try {
            Connection connection = factory.newConnection();
            return connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Channel getChannel() {
        return channel;
    }


}
