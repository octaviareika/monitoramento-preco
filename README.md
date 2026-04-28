<h1 align="center">
  🛒 Monitoramento de Preço na Amazon
</h1>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white" />
</p>

<p align="center">
  <strong>Uma aplicação automatizada para monitorar os preços de produtos na Amazon e enviar alertas por e-mail quando atingirem o valor desejado.</strong>
</p>

---

## 📌 Sobre o Projeto

Este projeto consiste em um sistema em segundo plano que raspa dados (web scraping) periodicamente de páginas de produtos na Amazon usando a biblioteca **Jsoup**. Os produtos, seus respectivos preços-alvo e os e-mails dos usuários são armazenados em um banco de dados **PostgreSQL**.

A aplicação utiliza o **Spring Scheduling** em conjunto com uma mensageria via **RabbitMQ** para gerenciar a fila de produtos a serem verificados, garantindo robustez e desacoplamento. Caso o preço lido da Amazon seja menor ou igual ao preço-alvo, o sistema envia automaticamente um e-mail de notificação para o usuário.

## 🚀 Tecnologias e Ferramentas

- **Java 21**
- **Spring Boot 3** (Web, Data JPA, AMQP, Mail)
- **RabbitMQ** (Mensageria e Filas)
- **PostgreSQL** (Banco de Dados Relacional)
- **Jsoup** (Web Scraping e Parse HTML)
- **Lombok** (Redução de Boilerplate)
- **Docker** (para rodar infraestrutura como RabbitMQ e PostgreSQL de forma fácil)

## ⚙️ Arquitetura e Fluxo

1. **Scheduler (`PrecoScheduler`)**: Roda a cada 60 segundos, busca todos os produtos cadastrados no banco de dados e envia seus IDs para uma fila no RabbitMQ (`fila_precos`).
2. **Mensageria (`RabbitConfig`)**: A fila do RabbitMQ recebe e enfileira as requisições de checagem. Isso evita sobrecarga ou travamentos simultâneos, escalando o sistema de forma inteligente.
3. **Worker (`PrecoWorker`)**: Escuta a fila ativamente. Para cada ID recebido, ele acessa a página do produto na Amazon através do Jsoup, extrai o preço atual da tag correta, compara com o valor alvo e dispara um e-mail com a API do **Spring Mail** caso o preço esteja bom para compra!

## 🛠️ Como Executar o Projeto

### Pré-requisitos
- [Java 21](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/) (para os serviços de mensageria e banco de dados)

### 1. Clonar o Repositório
Clone o projeto em sua máquina local:
```bash
git clone https://github.com/seu-usuario/monitoramento-preco.git
cd monitoramento-preco
```

### 2. Subir os Serviços (RabbitMQ e PostgreSQL)
Para facilitar, você pode rodar os contêineres do PostgreSQL e RabbitMQ via Docker:

**PostgreSQL:**
```bash
docker run --name pg-financeiro -e POSTGRES_USER=root -e POSTGRES_PASSWORD=sua_senha -e POSTGRES_DB=db_financeiro -p 5432:5432 -d postgres
```

**RabbitMQ:**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 3. Configurar as Variáveis de Ambiente
Verifique e atualize o seu arquivo `src/main/resources/application.properties` com as credenciais do banco de dados, RabbitMQ e as configurações do seu servidor SMTP (para o envio de e-mails, como o Gmail):

```properties
# Configuração do PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/db_financeiro
spring.datasource.username=root
spring.datasource.password=sua_senha

# Configuração do RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# Configurações de E-mail (Exemplo com Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-de-app
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
*(Lembre-se de não commitar a sua senha real do e-mail!)*

### 4. Rodar a Aplicação
Navegue até a pasta que contém o `pom.xml` e inicie o projeto com o Maven:
```bash
mvn spring-boot:run
```

## ⚠️ Observações sobre o Web Scraping
A Amazon possui mecanismos robustos de proteção contra bots e acessos automatizados (como o bloqueio via Captcha). O projeto utiliza headers HTTP (como `User-Agent` e `Accept-Language`) para mimetizar um navegador real. Entretanto, caso a Amazon bloqueie a requisição, o sistema tratará a exceção e emitirá um aviso no console de que o produto está indisponível ou que o Jsoup foi bloqueado.Alguns momentos aparecerá o preço de cada item mas em outros o Amazon bloqueia.

# É isso, bjao 