package clientside;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientSideMain extends AmqpChannel {

    private DeliverCallback callback;
    private String jogador;

    public ClientSideMain(String jogador) {
        super();
        this.callback = callback();
        this.jogador = jogador;
    }

    public void setQueue(String queueName) throws IOException {
        channel.queueDeclare(queueName, true, false, true, null);
    }

    public void setExchange(String exchangeName, String exchangeType) throws IOException {
        channel.exchangeDeclare(exchangeName, exchangeType);
    }

    public void bindQueueToExchange(String queueName, String exchangeName, String routingKey) throws IOException {
        channel.queueBind(queueName, exchangeName, routingKey);
    }

    public void setConsume(String queueName, String exchangeName) throws IOException {
        setQueue(queueName);
        bindQueueToExchange(queueName, exchangeName, "");
        channel.basicConsume(queueName, true, callback, consumerTag -> {});
    }

    public void sendMessage(String exchangeName, String routingKey, String message) throws IOException {
        setExchange(exchangeName, "topic");
        bindQueueToExchange("response_queue", exchangeName, routingKey);
        channel.basicPublish(exchangeName, routingKey, null, message.getBytes());
    }

    public void startConsuming(String queueName, String exchangeName) throws IOException {
        System.out.println("Aguardando Letra...");
        setConsume(queueName, exchangeName);

    }

    private DeliverCallback callback() {
        return new DeliverCallback() {
            @Override
            public void handle(String consumerTag, Delivery delivery) throws IOException {
                Scanner scanner = new Scanner(System.in);

                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                JSONObject jsonMessage = new JSONObject(message);

                if (!jsonMessage.has("letter")) {

                    JSONObject pontuacoes = jsonMessage.getJSONObject("pontuacoes");

                    for(String jogador : pontuacoes.keySet()) {
                        int pontuacao = pontuacoes.getInt(jogador);
                        System.out.println("Jogador " + jogador + " fez " + pontuacao + " pontos.");
                    }

                    JSONObject vencedor = jsonMessage.getJSONObject("vencedor");
                    String jogadorVencedor = vencedor.getString("jogador");
                    System.out.println("Vencedor foi " + jogadorVencedor);

                    return;
                }

                String letra = jsonMessage.getString("letter");
                System.out.println("Letra sorteada: " + letra);

                System.out.println("Digite um País que comece com a letra " + letra + ":");
                String pais = scanner.nextLine();

                System.out.println("Digite uma Fruta que comece com a letra " + letra + ":");
                String fruta = scanner.nextLine();

                System.out.println("Digite uma Cor que comece com a letra " + letra + ":");
                String cor = scanner.nextLine();

                // Enviar os dados digitados para o servidor
                JSONObject response = new JSONObject();
                response.put("jogador", jogador);
                JSONObject resposta = new JSONObject();
                resposta.put("pais", pais);
                resposta.put("fruta", fruta);
                resposta.put("cor", cor);
                response.put("resposta", resposta);

                sendMessage( "exchange_resposta", "resposta.send", response.toString());
                System.out.println("Resposta enviada para o servidor");

            }
        };
    }

    public static void main(String[] args) throws IOException {

        String exchangePontuacao = "exchange_pontuacao";
        String exchangeResposta = "exchange_resposta";
        String JogadorQueue;
        String responseQueue = "response_queue";

        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite seu nome: ");
        JogadorQueue = scanner.nextLine();

        ClientSideMain client = new ClientSideMain(JogadorQueue);
        client.startConsuming(JogadorQueue, exchangePontuacao);
    }


}
