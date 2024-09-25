from amqp_client import AmqpClient
from typing import Callable, Any, Dict
import json
from adedonha import Adedonha


class AmqpServer(AmqpClient):
    def __init__(self, queue:str, callback:Callable[..., Any]) -> None:
        super().__init__()

        self.callback = callback
        self.queue = queue
        self.exchange = 'exchange_pontuacao' # Nome da exchange que será utilizada para enviar as mensagens

    def set_queue(self):
        self.channel.queue_declare(queue=self.queue, durable=True, auto_delete=True)

    def set_consume(self):
        self.set_queue()
        self.channel.basic_consume(
            queue=self.queue,
            on_message_callback=self.callback,
            auto_ack=True
        )

    def start_consuming(self):
        print("Jogo iniciado!\nAguardando jogadores...")
        self.set_consume()
        self.channel.start_consuming()

    def send_message(self, message:Dict):
        self.channel.exchange_declare(exchange=self.exchange, exchange_type='fanout')

        self.channel.basic_publish(
            exchange='exchange_pontuacao',
            routing_key='',
            body=json.dumps(message)
        )
        print(f'Mensagem enviada: {message}')


def callback(ch, method, properties, body:str):
    message = body.decode("utf-8")
    print(f'Recebendo mensagem: {message}')

    # TODO: Implementar lógica para tratar as respostas recebida
    if message == 'stop':
        '''
        Quando a mensagem stop chegar, o jogo será encerrado e em seguida
        será recebida uma serie de mensagens com um dicionario no seguinte formato:
        {"jogador": "nome_do_jogador", "resposta": {"pais": "nome_do_pais", "fruta": "nome_da_fruta", "cor": "nome_da_cor"}}
        quero que seja calculada a pontuação de cada jogador e enviada uma mensagem com o seguinte formato:
        [{"jogador": "nome_do_jogador", "pontuacao": 30}
        {"jogador": "nome_do_jogador", "pontuacao": 20}
        {"jogador": "nome_do_jogador", "pontuacao": 10},
        {"Vencedor": "nome_do_jogador", "pontuacao": 30}]
        '''
        pass



server = AmqpServer(queue='data2_queue', callback=callback)
adedonha = Adedonha()

start = ''
while start.lower() != 'start':
    start = input('Digite "start" para sortear a letra e iniciar o jogo: ')

sorted_letter = adedonha.sort_letter()
server.send_message({'letter': f'{sorted_letter}'})
server.start_consuming()

