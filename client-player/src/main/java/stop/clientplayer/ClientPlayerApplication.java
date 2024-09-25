package stop.clientplayer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Scanner;

@SpringBootApplication
@EnableRabbit
public class ClientPlayerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ClientPlayerApplication.class, args);

        // Input para a pessoa digitar seu nome e associar esse nome à fila
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite seu nome: ");
        String nome = scanner.nextLine();

        AmpqClientConsumer consumer = context.getBean(AmpqClientConsumer.class);
        consumer.setQueueNameAndStartListener(nome);

        AmpqClientProducer producer = context.getBean(AmpqClientProducer.class);
        producer.setQueueName(nome);


    }

}
