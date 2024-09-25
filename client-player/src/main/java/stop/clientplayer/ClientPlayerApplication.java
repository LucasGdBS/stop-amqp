package stop.clientplayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.Scanner;


@SpringBootApplication
public class ClientPlayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientPlayerApplication.class, args);

        // Input para a pessoa digitar seu nome e associar esse nome a fila
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite seu nome: ");
        String nome = scanner.nextLine();

        ampq_client_consumer consumer = new ampq_client_consumer();
        consumer.set_queue_name(nome);
    }

}
