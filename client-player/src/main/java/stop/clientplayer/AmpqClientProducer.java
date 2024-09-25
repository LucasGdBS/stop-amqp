package stop.clientplayer;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AmpqClientProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ConnectionFactory connectionFactory;

    static final String EXCHANGE_NAME = "exchange_resposta";
    static final String ROUTING_KEY = "stop-game";
    static String queueName;

    public void setQueueName(String queueName) {
        this.queueName = queueName;
        // Inicializa o listener para essa fila dinamicamente
        startListeningForQueue(queueName);
    }

    // Definição de uma fila default que pode ser usada se necessário
    @Bean
    Queue queue() {
        return new Queue("response_queue", true, false, true);
    }


    // Método para inicializar o listener dinamicamente para a fila
    private void startListeningForQueue(String queueName) {
        System.out.println("Iniciando listener para a fila: " + queueName);  // Log para verificação
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);

        // Quando a mensagem (letra) for recebida, chamamos o startGame
        container.setMessageListener(new MessageListenerAdapter(new Object() {
            public void handleMessage(String letter) {
                System.out.println("Letra recebida: " + letter);
                startGame(letter); // Iniciar o jogo quando a letra for recebida
            }
        }));

        container.start();
        System.out.println("Listener iniciado para a fila " + queueName);
    }

    // Callback: recebe uma letra do servidor e inicia o jogo
    public void onReceiveLetter(String letter) {
        startGame(letter);
    }

    // Método para iniciar o jogo
    public void startGame(String letter) {
        Scanner scanner = new Scanner(System.in);
        String pais, fruta, cor;

        System.out.println("Digite um País que comece com a letra " + letter + ":");
        pais = scanner.nextLine();

        System.out.println("Digite uma Fruta que comece com a letra " + letter + ":");
        fruta = scanner.nextLine();

        System.out.println("Digite uma Cor que comece com a letra " + letter + ":");
        cor = scanner.nextLine();

        // Enviar os dados digitados para o servidor
        String message = String.format("País: %s, Fruta: %s, Cor: %s", pais, fruta, cor);
        sendMessage(message);
    }

    // Enviar a mensagem para o servidor
    private void sendMessage(String message) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
            System.out.println("Mensagem enviada: " + message);
        } catch (Exception e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
        }
    }
}
