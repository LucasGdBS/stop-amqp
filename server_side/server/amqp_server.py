from config.amqp_chanel import AmqpChanel
from typing import Callable, Any, Dict
import json
from adedonha import Adedonha
import uuid


class AmqpServer(AmqpChanel):
    def __init__(self, queue:str, callback:Callable[..., Any]) -> None:
        super().__init__()

        self.callback = callback
        self.queue_sufix = str(uuid.uuid4())[:8]
        self.queue = queue + self.queue_sufix
        self.exchange = 'exchange_pontuacao' # Nome da exchange que será utilizada para enviar as mensagens


    def set_queue(self):
        self.chanel.queue_declare(queue=self.queue, durable=True, auto_delete=True)

    def set_consume(self):
        self.set_queue()
        print(f"Consumindo a fila '{self.queue}'")
        self.chanel.basic_consume(
            queue=self.queue,
            on_message_callback=self.callback,
            auto_ack=True
        )

    def bind_queue(self, exchange:str, routing_key:str):
        self.chanel.queue_bind(
            exchange=exchange,
            queue=self.queue,
            routing_key=routing_key
        )

    def start_consuming(self):
        print("Jogo iniciado!\nAguardando jogadores...")
        self.set_consume()
        self.bind_queue('exchange_resposta', f'resposta.send.{self.queue_sufix}')

        self.chanel.start_consuming()

    def send_message(self, message:Dict):
        self.chanel.exchange_declare(exchange=self.exchange, exchange_type='fanout')

        if 'vencedor' in message:
            for jogador, pontuacao in message['pontuacoes'].items():
                print(f'Jogador {jogador} fez {pontuacao} pontos.')

            vencedor = message['vencedor']['jogador']
            print(f"Vencedor foi {vencedor}")

            self.chanel.basic_publish(
                exchange=self.exchange,
                routing_key='',
                body=json.dumps(message)
            )
        else:
            print(f'A letra sorteada foi: {message["letter"]}')
            message['sufix'] = self.queue_sufix

            self.chanel.basic_publish(
                exchange=self.exchange,
                routing_key='',
                body=json.dumps(message)
            )


respostas = []
def callback(ch, method, properties, body:str):
    message = body.decode("utf-8")
    resposta = json.loads(message)
    respostas.append(message)


    if len(respostas) == qnt_pessoas:
        print('Todas as respostas foram recebidas')
        pontuacoes = []

        for resposta in respostas:
            try:
                resposta = json.loads(resposta)
                pontuacao = adedonha.validar_resposta(sorted_letter, resposta['resposta'])
                pontuacoes.append({'jogador': resposta['jogador'], 'pontuacao': pontuacao})
            except KeyError as e:
                print(f"Chave faltando na resposta: {e}")
            except ValueError as e:
                print(f"Formato inválido: {e}")


        if pontuacoes:
            vencedor = max(pontuacoes, key=lambda x: x['pontuacao'])
            resultado = {
                'pontuacoes': {p['jogador']: p['pontuacao'] for p in pontuacoes},
                'vencedor': {
                    'jogador': vencedor['jogador'],
                    'pontuacao': vencedor['pontuacao']
                }
            }
            server.send_message(resultado)
            respostas.clear()
            server.chanel.stop_consuming()




server = AmqpServer(queue='response_queue', callback=callback)
adedonha = Adedonha()

try:
    while True:
        while True:
            qnt_pessoas = input('Digite quantas pessoas vão jogar para sortear a letra e iniciar o jogo: ')
            if qnt_pessoas.isnumeric() and int(qnt_pessoas) > 0:
                qnt_pessoas = int(qnt_pessoas)
                break

        sorted_letter = adedonha.sort_letter()
        server.send_message({'letter': f'{sorted_letter}'})
        server.start_consuming()

        novamente = input('Deseja jogar de novo? 1-Sim 2-Não: ')
        if novamente != '1':
            print('Jogo finalizado!')
            break
except KeyboardInterrupt:
    print('Jogo finalizado!')
