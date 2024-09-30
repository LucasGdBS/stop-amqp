# Stop-AMQP

**Stop-AMQP** é um jogo baseado no clássico "Stop", onde jogadores se conectam a um servidor usando **RabbitMQ** para enviar respostas e o comando de "Stop" para encerrar a rodada. O servidor processa as respostas, calcula pontuações e envia de volta para os jogadores, com todas as interações registradas por um cliente de auditoria.

## Funcionalidades
- Troca de mensagens em tempo real com RabbitMQ.
- Processamento e cálculo de pontuações.
- Registro completo de todas as mensagens trocadas para auditoria.
  
## Tecnologias
- **Python 3.x**
- **RabbitMQ**
- **pika**: Biblioteca Python para integração com RabbitMQ.

## Instalação
1. **Clone o repositório:**
   ```bash
   git clone https://github.com/LucasGdBS/stop-amqp.git
   ```
2. **Instale as dependências:**
   ```bash
   pip install pika
   ```

3. **Configure e inicie o RabbitMQ:**
   ```bash
   sudo service rabbitmq-server start
   ```

## Execução
### Servidor
Para iniciar o servidor:
```bash
python server.py
```

### Jogadores
Para cada jogador:
```bash
python client.py
```

### Cliente Auditor
Para iniciar o cliente auditor:
```bash
python audit_client.py
```

## Estrutura do Projeto
- **server.py**: Gerencia as respostas dos jogadores, calcula as pontuações e distribui o comando "Stop".
- **client.py**: Cada jogador envia respostas e recebe pontuações.
- **audit_client.py**: Registra todas as interações entre o servidor e os jogadores para auditoria.

## Melhorias Futuras
- **Persistência**: Integração com banco de dados.
- **Interface gráfica** para facilitar a interação dos jogadores.
- **Escalabilidade** para suportar múltiplas rodadas simultâneas.

## Licença
Este projeto está licenciado sob a licença MIT.
