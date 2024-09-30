# Stop-AMQP

## 1. Visão Geral
Stop-AMQP é uma implementação distribuída do clássico jogo "Stop", usando RabbitMQ para a comunicação entre os componentes do sistema: um servidor central, jogadores (clientes) e um auditor. O jogo explora conceitos de mensageria assíncrona e processamento distribuído, onde os jogadores interagem com o servidor enviando respostas, que são processadas para determinar o vencedor ao final de cada rodada.  
Este projeto utiliza Python para implementar o servidor e o auditor e Java para os clientes, e RabbitMQ como middleware de mensagens para garantir a entrega confiável e em tempo real de comandos e respostas entre os jogadores e o servidor.

### Objetivos do Projeto
- Criar um jogo distribuído que possa ser executado em ambientes descentralizados.
- Demonstrar como sistemas de mensageria (AMQP) podem ser utilizados para coordenação de eventos em tempo real.
- Fornecer um exemplo prático de como usar RabbitMQ para comunicação cliente-servidor.

## 2. Arquitetura do Sistema
O projeto segue um modelo cliente-servidor distribuído, onde há três componentes principais: Cliente (Jogadores), Servidor e Cliente Auditor.

### 2.1 Componentes
Ambos os componentes, cliente e servidor, atuam tanto como consumidores quanto como produtores.

1. **Cliente (Jogadores)**
   - Cada jogador se conecta ao servidor via RabbitMQ criando uma fila com o nome do jogador.
   - As respostas são enviadas no formato JSON para o servidor através de uma exchange topic com a routing key específica do servidor.
   - Após o processamento, o jogador recebe a pontuação de volta do servidor.

2. **Servidor**
   - Gera um código único que será utilizado para nomear a fila e compor a routing key associada.
   - Seleciona uma letra aleatoriamente.
   - Envia ambos os dados para os clientes no formato JSON.
   - Processa todas as respostas enviadas pelos jogadores.
   - Calcula a pontuação de cada jogador e envia os resultados para os jogadores.
   - Todos os dados do jogo, incluindo respostas e pontuações, são enviados para o cliente auditor.
   - Ao final, o servidor pergunta se deseja iniciar uma nova partida.

3. **Cliente Auditor**
   - Escuta todas as mensagens trocadas entre o servidor e os jogadores.
   - Registra cada resposta e pontuação em um log, incluindo a exchange de origem da mensagem e a routing key (se aplicável), para fins de auditoria e análise futura.
   - Atua como uma ferramenta de monitoramento, garantindo a integridade e rastreabilidade das mensagens do sistema.

### 2.2 RabbitMQ e Fluxo de Mensagens
O sistema utiliza RabbitMQ para gerenciar a troca de mensagens entre os componentes. Dois tipos de exchanges e diversas filas são utilizadas para coordenar a comunicação.

#### Exchanges
- **Topic Exchange (Respostas dos Jogadores)**: Cada jogador envia suas respostas ao servidor por meio desta exchange. As mensagens são roteadas para a fila do servidor utilizando a routing key única `resposta.send.<código>`, enquanto as mensagens destinadas ao auditor são roteadas usando qualquer routing key que comece com `resposta.#`.
- **Fanout Exchange (Sorteio da Letra e Pontuações)**: Utilizada para distribuir a letra sorteada a todos os jogadores conectados e enviar as pontuações de volta. O servidor calcula a pontuação de cada jogador e gera um ranking geral, informando quem foi o vencedor da rodada.

#### Filas
- **Fila de Respostas**: Fila responsável por receber as respostas de todos os jogadores e encaminhá-las ao servidor. Cada instância de servidor cria uma fila exclusiva com um código único, e o servidor escuta essa fila para processar as respostas enviadas.
- **Fila do Jogador**: Cada jogador tem uma fila exclusiva que recebe a letra sorteada e as pontuações.
- **Fila de Auditoria**: Todas as mensagens trocadas, incluindo respostas e pontuações, são roteadas para essa fila para fins de auditoria.

### 2.3 Diagrama do Fluxo
_(Insira aqui um diagrama que ilustre o fluxo de mensagens, se necessário)_

## 3. Fluxo de Comunicação e Operação

### 3.1 Início do Jogo
- Cada jogador insere seu nome e é informado ao servidor quantas pessoas irão jogar.
- O servidor inicia uma rodada do jogo e envia uma mensagem de início contendo a letra sorteada para todos os jogadores conectados.
- Os jogadores respondem enviando suas respostas, que são processadas pelo servidor em tempo real.

### 3.2 Encerramento da Rodada
- O servidor, ao receber todas as respostas, calcula as pontuações dos jogadores com base nas respostas enviadas.
- O servidor envia uma notificação com os resultados finais e a pontuação acumulada para todos os jogadores.

### 3.3 Auditoria
- O cliente auditor, rodando paralelamente, registra todas as interações entre os jogadores e o servidor, permitindo auditorias detalhadas de cada rodada de jogo.

## 4. Implementação

### 4.1 Tecnologias Utilizadas
- **Python 3.x**: Linguagem de programação usada para desenvolver o servidor e o cliente de auditoria.
- **Java 22**: Linguagem de programação usada para desenvolver os clientes.
- **RabbitMQ**: Sistema de mensageria que implementa o protocolo AMQP para garantir comunicação robusta entre os componentes do sistema.
- **pika**: Biblioteca Python utilizada para comunicação com o RabbitMQ, lidando com a criação de filas, exchanges, e publicação/consumo de mensagens.
- **com.rabbitmq**: Biblioteca do Java utilizada para comunicação com o RabbitMQ.

## 5. Instalação e Configuração

### 5.1 Requisitos
- Python 3.x
- JDK: Certifique-se de que o JDK está instalado. Recomendamos o uso do IntelliJ IDEA como IDE.
- RabbitMQ: O RabbitMQ deve estar instalado e em execução localmente ou em um servidor remoto.
- **Dependências do Python**: Instale as dependências com `pip install -r requirements.txt`.
- **Dependências do Maven**: Certifique-se de que o projeto está configurado como um projeto Maven no IntelliJ e que todas as dependências do `pom.xml` sejam instaladas.

### 5.2 Configuração do RabbitMQ
1. **Criar uma Conta**: Acesse CloudAMQP, crie uma conta e configure uma nova instância do RabbitMQ.
2. **Obter os Dados da Instância**: Após criar a instância, clique no nome da instância para acessar as configurações e copie os detalhes de conexão.
3. **Configurar o Arquivo .env**: Crie um arquivo `.env` com base no `.env.example`, preenchendo os campos com os dados obtidos da sua instância do RabbitMQ.

### 5.3 Execução do Projeto
1. **Iniciar o Servidor**: Para iniciar o servidor de jogo, execute o `amqp_server.py`.
2. **Iniciar os Clientes**: Cada jogador deve executar o `ClienteSideMain` no IntelliJ (projeto Java).
3. **Iniciar o Cliente Auditor**: Para iniciar o cliente auditor e registrar as mensagens trocadas, execute o `auditoria.py`.
