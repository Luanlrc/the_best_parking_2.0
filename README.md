# 🅿️ The Best Parking

Sistema de gestão de estacionamento construído com **Spring Boot** e **PostgreSQL**, com API REST e uma interface web simples servida pela própria aplicação.

---

## ✨ Funcionalidades

- **CRUD** de clientes, automóveis, tipos de automóvel e vagas
- **Check-in / check-out** de veículos com cálculo automático do valor cobrado (horas × valor/hora do tipo)
- Mensalistas não pagam por hora
- Controle de status das vagas (LIVRE / OCUPADA)
- **Dashboard** com visão geral do pátio
- **Documentação interativa** da API via Swagger

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.3.4 |
| Persistência | Spring Data JPA (Hibernate) |
| Banco de dados | PostgreSQL |
| Documentação | springdoc-openapi (Swagger UI) |
| Build | Maven |
| Frontend | HTML + CSS + JavaScript (estático) |

---

## 📋 Pré-requisitos

- **JDK 21** instalado ([Eclipse Temurin](https://adoptium.net/))
- **PostgreSQL** rodando localmente
- **Maven** (opcional — o IntelliJ já traz embutido)

---

## 🚀 Como rodar

### 1. Criar o banco de dados

No PostgreSQL, crie o banco (caso ainda não exista):

```sql
CREATE DATABASE estacionamento;
```

> As tabelas e dados de exemplo são criados automaticamente na primeira execução
> (via `schema.sql` e `data.sql`). Não precisa criar nada na mão.

### 2. Conferir as credenciais

As configurações do banco ficam em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/estacionamento
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

Ajuste `username` e `password` conforme o seu PostgreSQL.

### 3. Subir a aplicação

**Pelo IntelliJ:** abra a classe `EstacionamentoApplication.java` e clique no ▶ verde ao lado do `main`.

**Pelo terminal (com Maven instalado):**

```bash
mvn spring-boot:run
```

A aplicação sobe em **http://localhost:8080**.

---

## 🌐 Acessos

| O quê | URL |
|---|---|
| **Interface web (front)** | http://localhost:8080/ |
| **Swagger (documentação da API)** | http://localhost:8080/swagger-ui.html |
| **OpenAPI (JSON)** | http://localhost:8080/v3/api-docs |

---

## 📡 Endpoints da API

### Clientes
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/clientes` | Lista todos |
| GET | `/api/clientes/{id}` | Busca por ID |
| POST | `/api/clientes` | Cria |
| PUT | `/api/clientes/{id}` | Atualiza |
| DELETE | `/api/clientes/{id}` | Remove |

### Tipos de automóvel
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/tipos-automovel` | Lista todos |
| POST | `/api/tipos-automovel` | Cria |
| PUT | `/api/tipos-automovel/{id}` | Atualiza |
| DELETE | `/api/tipos-automovel/{id}` | Remove |

### Automóveis
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/automoveis` | Lista todos |
| POST | `/api/automoveis` | Cria |
| PUT | `/api/automoveis/{id}` | Atualiza |
| DELETE | `/api/automoveis/{id}` | Remove |

### Vagas
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/vagas` | Lista todas |
| GET | `/api/vagas/livres` | Lista só as livres |
| POST | `/api/vagas` | Cria |
| PUT | `/api/vagas/{id}` | Atualiza |
| DELETE | `/api/vagas/{id}` | Remove |

### Movimentações (entrada/saída)
| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/movimentacoes` | Lista todas |
| GET | `/api/movimentacoes/abertas` | Lista veículos no pátio (sem saída) |
| POST | `/api/movimentacoes/checkin` | Registra entrada — `{ "placa": "ABC1D23", "idVaga": 1 }` |
| POST | `/api/movimentacoes/checkout` | Registra saída e calcula valor — `{ "placa": "ABC1D23" }` |

---

## 📂 Estrutura do projeto

```
the_best_parking_2.0/
├── pom.xml                        # Dependências e build (Maven)
├── README.md                      # Este arquivo
└── src/main/
    ├── java/br/com/estacionamento/
    │   ├── EstacionamentoApplication.java   # Ponto de entrada
    │   ├── controller/   # Camada HTTP (endpoints REST)
    │   ├── service/      # Regras de negócio
    │   ├── repository/   # Acesso ao banco (Spring Data JPA)
    │   ├── entity/       # Entidades JPA (mapeamento das tabelas)
    │   ├── dto/          # Objetos de transferência (entrada/saída da API)
    │   └── exception/    # Tratamento global de erros
    └── resources/
        ├── application.properties   # Configurações
        ├── schema.sql               # Criação das tabelas
        ├── data.sql                 # Dados de exemplo
        └── static/                  # Frontend (HTML/CSS/JS)
            ├── index.html
            ├── css/styles.css
            └── js/{api.js, app.js}
```

---

## 🧪 Fluxo de teste rápido

1. Acesse http://localhost:8080/
2. Vá em **Automóveis → + Novo automóvel** e cadastre um carro (já existem tipos e clientes de exemplo)
3. Vá em **Pátio** e registre a **entrada** (check-in) escolhendo placa e vaga
4. Registre a **saída** (check-out) — o valor cobrado é calculado automaticamente
5. Acompanhe tudo no **Dashboard**

---

## 📝 Observações

- O `ddl-auto` está em `validate`: o Hibernate **valida** que as entidades batem com o banco, mas **não altera** o schema.
- As senhas estão em texto puro no `application.properties` por ser um projeto de estudo. Em produção, use variáveis de ambiente.
