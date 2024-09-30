import pika
from decouple import config


class AmqpChanel:
    def __init__(self) -> None:
        self.__host = config('HOST')
        self.__port = int(config('PORT'))
        self.__virtual_host = config('VIRTUAL_HOST')
        self.__username = config('RABBIT_USERNAME')
        self.__password = config('PASSWORD')
        self.connection = self.__create_connection()  # Mantém a conexão ativa
        self.chanel = self.__create_channel()  # Cria o canal

    def __create_connection(self):
        # Método para criar a conexão
        connection_parameters = pika.ConnectionParameters(
            host=self.__host,
            port=self.__port,
            virtual_host=self.__virtual_host,
            credentials=pika.PlainCredentials(
                username=self.__username,
                password=self.__password,
            ),
        )
        return pika.BlockingConnection(connection_parameters)

    def __create_channel(self):
        # Método para criar o canal a partir da conexão
        return self.connection.channel()

    def reopen_channel(self):
        '''
        Método para reabrir o canal, caso ele tenha sido fechado
        '''
        if self.chanel.is_closed:  # Verifica se o canal está fechado
            print("Reabrindo o canal...")
            self.chanel = self.__create_channel()  # Reabre o canal
