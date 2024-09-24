from amqp_client import AmqpClient
from typing import Callable, Any


class AmqpServer(AmqpClient):
    def __init__(self, queue:str, callback:Callable[..., Any]) -> None:
        super().__init__()

        self.callback = callback
        self.queue = queue

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
        print("Aguardando...")
        self.set_consume()
        self.channel.start_consuming()


def callback(ch, method, properties, body:str):
    print(f'Recebendo mensagem: {body.decode("utf-8")}')

server = AmqpServer(queue='data2_queue', callback=callback)
server.start_consuming()