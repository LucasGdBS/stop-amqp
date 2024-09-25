from config.amqp_chanel import AmqpChanel
from typing import Callable, Any, Dict
import json
from adedonha import Adedonha


class AmqpServer(AmqpChanel):
    def __init__(self, queue:str, callback:Callable[..., Any]) -> None:
        super().__init__()

        self.callback = callback
        self.queue = queue
        self.exchange = 'exchange_pontuacao' # Nome da exchange que será utilizada para enviar as mensagens

    def set_queue(self):
        self.chanel.queue_declare(queue=self.queue, durable=True, auto_delete=True)

    def set_consume(self):
        self.set_queue()
        self.chanel.basic_consume(
            queue=self.queue,
            on_message_callback=self.callback,
            auto_ack=True
        )

    def start_consuming(self):
        print("Jogo iniciado!\nAguardando jogadores...")
        self.set_consume()
        self.chanel.start_consuming()

    def send_message(self, message:Dict):
        self.chanel.exchange_declare(exchange=self.exchange, exchange_type='fanout')

        self.chanel.basic_publish(
            exchange=self.exchange,
            routing_key='',
            body=json.dumps(message)
        )
        print(f'Mensagem enviada: {message}')

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
            print(f'Resultado enviado: {resultado}')




server = AmqpServer(queue='response_queue', callback=callback)
adedonha = Adedonha()

qnt_pessoas = 0
while True:
    qnt_pessoas = input('Digite quantas pessoas vão jogar para sortear a letra e iniciar o jogo: ')
    if qnt_pessoas.isnumeric():
        qnt_pessoas = int(qnt_pessoas)
        break

sorted_letter = adedonha.sort_letter()
server.send_message({'letter': f'{sorted_letter}'})
server.start_consuming()

