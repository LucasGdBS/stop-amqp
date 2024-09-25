package stop.clientplayer;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Queue;



import java.util.Scanner;

@Component
public class AmpqClientProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    static final String EXCHANGE_NAME = "exchange_resposta";
    static final String ROUTING_KEY = "stop-game";


    @Bean
    Queue queue() {
        return new Queue("response_queue", true, false, true);
    }



    // Callback: recebe uma letra do servidor e inicia o jogo
    @RabbitListener(queues = "response_queue")
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
