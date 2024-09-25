import pika
from decouple import config


class AmqpChanel:
    def __init__(self) -> None:
        self.__host = config('HOST')
        self.__port = int(config('PORT'))
        self.__virtual_host = config('VIRTUAL_HOST')
        self.__username = config('RABBIT_USERNAME')
        self.__password = config('PASSWORD')
        self.chanel = self.__create_chanel()

    def __create_chanel(self):
        connection_parameters = pika.ConnectionParameters(
            host=self.__host,
            port=self.__port,
            virtual_host=self.__virtual_host,
            credentials=pika.PlainCredentials(
                username=self.__username,
                password=self.__password,
            ),
        )

        return pika.BlockingConnection(connection_parameters).channel()
