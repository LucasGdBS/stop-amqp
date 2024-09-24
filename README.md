# Stop-amqp
Este projeto implementa um jogo onde os jogadores se conectam a um servidor usando RabbitMQ para enviar respostas e comandos de "Stop". O servidor processa as respostas, calcula as pontuações e transmite o comando de "Stop" para todos os jogadores e para um cliente de auditoria.

## Componentes
### 1. Cliente (Jogadores)
- Cada jogador se conecta ao servidor via RabbitMQ.
- As respostas dos jogadores são enviadas em formato JSON para o servidor.
- O comando de "Stop", enviado por qualquer jogador, encerra a rodada para todos os participantes.
- Mensagens de resposta e comandos de "Stop" são publicadas em filas específicas para o servidor processar.
### 2. Servidor
- Processa as respostas enviadas pelos jogadores.
- Calcula a pontuação de cada resposta e envia de volta ao jogador correspondente.
- Transmite o comando de "Stop" para todos os jogadores através de uma fila de broadcast.
- Envia todas as mensagens trocadas (respostas, pontuações, comandos) para o cliente de auditoria.
### 3. Cliente de Auditoria
Inscrito em todas as filas de mensagens dos jogadores e do servidor.
Registra todas as mensagens (respostas, comandos de "Stop", pontuações) em um log para auditoria.
Implementação com RabbitMQ
##### Exchanges
- Topic Exchange: Usada para cada jogador enviar suas respostas ao servidor.
- Fanout Exchange: Usada para o comando de "Stop" ser enviado para todos os jogadores.
- Topic Exchange: Usada para o servidor enviar a pontuação para cada jogador.

#### Filas
- Uma fila para cada jogador enviar suas respostas ao servidor.
- Uma fila para cada jogador receber suas pontuações.
- Uma fila de broadcast para o comando de "Stop" ser recebido por todos os jogadores.
- Uma fila de auditoria que roteia todas as mensagens para registro.
#### Fluxo de Mensagens
- Cada jogador envia sua resposta através de uma Direct Exchange.
- O servidor processa as respostas, calcula as pontuações e as publica nas filas correspondentes dos jogadores.
- Quando qualquer jogador envia o comando de "Stop", o servidor o distribui para
