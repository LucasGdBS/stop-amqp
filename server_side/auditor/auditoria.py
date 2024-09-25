import sys
import json
from typing import Callable, Any
from config.amqp_chanel import AmqpChanel
from loguru import logger

# Configurações do logger
logger.remove()
logger.add(sys.stderr,
           level="INFO",
           format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> - <level>{level}</level> - <level>{message}</level>")

class Auditoria(AmqpChanel):
    def __init__(self, queue:str, callback:Callable[..., Any]) -> None:
        super().__init__()

        self.callback = callback
        self.queue = queue

    def set_queue(self):
        self.chanel.queue_declare(queue=self.queue, durable=True, auto_delete=True)

    def set_exchange(self, exchange:str, exchange_type:str):
        self.chanel.exchange_declare(
            exchange=exchange,
            exchange_type=exchange_type
        )

    def bind_queue(self, exchange:str, routing_key:str):
        self.chanel.queue_bind(
            exchange=exchange,
            queue=self.queue,
            routing_key=routing_key
        )

    def set_consume(self, exchange:str, exchange_type:str, routing_key:str):
        self.set_queue()
        self.set_exchange(exchange, exchange_type)
        self.bind_queue(exchange, routing_key)
        self.chanel.basic_consume(
            queue=self.queue,
            on_message_callback=self.callback,
            auto_ack=True
        )

    def start_consuming(self, exchanges: list[str], exchange_types: list[str], routing_keys: list[str]):
        for exchange, exchange_type, routing_key in zip(exchanges, exchange_types, routing_keys):
            self.set_consume(exchange, exchange_type, routing_key)
            logger.info(f'Consumindo da exchange: {exchange}, routing_key: {routing_key}')

        self.chanel.start_consuming()





def callback(ch, method, properties, body:str):
    message = body.decode("utf-8")
    try:
        message_data = json.loads(message)
    except json.JSONDecodeError:
        message_data = {"error": "Invalid JSON", "message": message}

    logger.info(f'Mensagem recebida a partir da exchange: {method.exchange}, routing_key: {method.routing_key}')
    logger.info(f'Conteúdo da mensagem: {json.dumps(message_data, indent=4)}')

logger.info('Iniciando Auditoria')

auditoria = Auditoria('auditoria', callback)

exchanges = ['exchange_resposta', 'exchange_pontuacao']
exchange_types = ['direct', 'fanout']
routing_keys = ['', '']

auditoria.start_consuming(exchanges, exchange_types, routing_keys)
