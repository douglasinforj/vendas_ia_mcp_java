# Projeto VendasIA
Java + MCP + IA | Sistema de gestão de vendas com integração de Inteligência Artificial

> Projeto de portfólio desenvolvido para dominar a tríade **MCP + IA + Java** com foco em entender
> a base técnica por baixo dos panos antes de evoluir para frameworks como Spring Boot.

- Banco de dados base: [ecommerce-treino1](https://github.com/douglasinforj/ecommerce-treino1)

---

## Objetivo

Desenvolvi este sistema utilizando um banco de dados de e-commerce real para treinar e aplicar
conhecimentos em Java, análise de dados e integração com IA. Com o rápido crescimento da IA,
as empresas buscam profissionais que entendam como conectar sistemas legados a modelos de
linguagem — por isso o foco na tríade MCP + IA + Java, construindo tudo do zero antes de
usar abstrações de framework.

---

## Arquitetura geral

```
Usuário (linguagem natural)
        │
        ▼
  Claude Desktop / Cliente Python (Django)
        │  JSON-RPC via stdio
        ▼
  MCP Server — Java puro
        │  JDBC
        ▼
     MySQL
```

---

## Visão geral das fases

---

### FASE 1 — Java Core + JDBC puro

Conectar no banco sem nenhum framework. Ver exatamente o que o Hibernate esconde.

**O que foi implementado**

| Camada | Classes |
|---|---|
| Infra | `ConexaoMySQL` |
| Model | `Cliente`, `Produto`, `Pedido`, `ItemPedido` |
| Repository | `ClienteRepository`, `ProdutoRepository` |

**Conceitos abordados**

| Conceito | O que é | Por que importa |
|---|---|---|
| `DriverManager` | Gerenciador de drivers JDBC | Ponto de entrada para qualquer conexão |
| `Connection` | Representa a conexão com o banco | Recurso caro — precisa ser fechado |
| `PreparedStatement` | SQL parametrizado | Segurança contra SQL Injection |
| `ResultSet` | Cursor sobre as linhas retornadas | Como se itera os dados |
| `try-with-resources` | Fechamento automático de recursos | Evita memory/connection leak |

> **Por que JDBC antes de JPA?**
> Quando o Hibernate gera um SQL errado em produção, quem sabe JDBC consegue debugar.
> Quem só sabe Hibernate fica perdido.

---

### FASE 2 — Arquitetura em camadas (Model → Repository → Service)

Expansão do domínio com arquitetura profissional, transações atômicas e queries analíticas.

**Models**

| Classe | Descrição |
|---|---|
| `Cliente` | Dados cadastrais PF/PJ |
| `Produto` | SKU, preços, estoque, categoria |
| `Pedido` | Cabeçalho do pedido com status |
| `ItemPedido` | Granularidade do pedido com preço histórico |
| `Pagamento` | Forma, parcelas e valor pago |
| `Entrega` | Rastreio, transportadora, previsão e status |
| `RastreamentoLog` | Log detalhado de movimentação da entrega |
| `RecomendacaoProduto` | Market basket analysis: suporte, confiança e lift |

**Repositories**

`ClienteRepository` · `ProdutoRepository` · `PedidoRepository` · `PagamentoRepository`
`EntregaRepository` · `RastreamentoLogRepository` · `RecomendacaoProdutoRepository` · `RelatorioRepository`

**Services**

`ClienteService` · `ProdutoService` · `PedidoService` · `PagamentoService`
`EntregaService` · `RastreamentoLogService` · `RecomendacaoProdutoService` · `RelatorioService`

**Conceitos abordados**

- Separação de responsabilidades: Repository só persiste, Service orquestra e valida
- Transação atômica manual com `commit` / `rollback` e conexão compartilhada entre repositories
- Queries analíticas com `JOIN`, `GROUP BY`, `SUM`, `AVG`, `COUNT`
- `Optional<T>` para evitar `NullPointerException`
- Java Records para models imutáveis

---

### ✅ FASE 3 — MCP Server em Java

Servidor MCP expondo ferramentas do banco de dados ao Claude via protocolo JSON-RPC 2.0.

**O que é MCP?**

MCP *(Model Context Protocol)* é um protocolo de comunicação que define como um modelo de IA
descobre e chama ferramentas externas. O transporte é JSON-RPC via `stdio` ou HTTP/SSE.

```
Cliente envia:    {"method": "tools/call", "params": {"name": "top_produtos", "arguments": {"limite": 5}}}
Servidor retorna: {"result": {"content": [{"type": "text", "text": "Notebook → 50 un..."}]}}
```

**Tools analíticas**

| Tool | Pergunta que responde |
|---|---|
| `top_produtos` | Quais produtos mais venderam? |
| `receita_por_periodo` | Qual foi o faturamento no período X? |
| `estoque_critico` | Quais produtos precisam de reposição? |
| `pedidos_por_status` | Como está a operação de pedidos? |
| `ticket_medio_por_cliente` | Quem são os melhores clientes? |

**Tools de decisão de negócio**

| Tool | Pergunta que responde |
|---|---|
| `recomendacoes_produto` | O que recomendar para quem comprou X? |
| `historico_rastreamento` | Onde está o pedido da entrega N? |
| `resumo_pagamentos` | Como estão os pagamentos? Qual forma mais usada? |
| `status_entregas` | Quantas entregas estão atrasadas? |
| `perfil_cliente` | Quem é o cliente X? Qual seu histórico? |

**Classes MCP**

| Classe | Responsabilidade |
|---|---|
| `BaseTool` | Classe abstrata base: `especificacao()`, `schema()`, `schemaVazio()`, `sucessoResult()`, `erroResult()`, `getIntArgument()` |
| `VendasMcpServer` | Monta o servidor, registra o transport STDIO e expõe as 10 tools |
| `McpMain` | Entrypoint — inicializa o servidor e mantém o processo vivo |

> **Sobre o schema das tools:** o `inputSchema` é uma String JSON pura passada no construtor
> do `Tool` — sem o objeto `McpSchema.JsonSchema` depreciado na versão 1.1.0.

**Dependências MCP**

```xml
<dependency>
    <groupId>io.modelcontextprotocol.sdk</groupId>
    <artifactId>mcp</artifactId>
    <version>0.14.1</version>
</dependency>
```

Referência: [MCP Java SDK — Quickstart](https://java.sdk.modelcontextprotocol.io/latest/quickstart/)

**Configuração de logging — `logback.xml`**

O protocolo MCP usa `stdout` exclusivamente para JSON-RPC. Qualquer texto não-JSON no `stdout`
quebra a comunicação.

| Configuração | Valor | Motivo |
|---|---|---|
| `ConsoleAppender target` | `System.err` | Logs nunca vão pro `stdout` |
| `root level` | `WARN` | Silencia bibliotecas externas |
| `logger br.com.vendasia` | `INFO` | Sua aplicação loga normalmente |
| `RollingFileAppender` | `logs/vendas-mcp.log` | Histórico persistido em arquivo |
| `NopStatusListener` | — | Suprime mensagens internas do Logback no `stdout` |

**Integrações validadas**

- Claude Desktop (Windows) — 10 tools reconhecidas e chamadas via linguagem natural
- Cliente Python (`mcp_client.py`) — handshake MCP + chamada de tools via `subprocess`

---

### Testes — JUnit 5 + Mockito

Testes organizados em duas categorias com padrões distintos.

**Estratégia adotada**

| Tipo | O que testa | Usa banco? |
|---|---|---|
| `ServiceTest` | Decisões e validações de negócio | ❌ mock |
| `RepositoryTest` | Queries SQL e mapeamento de dados | ✅ banco real |

> **Regra de ouro:** teste o que o service **decide**, não o que ele **delega**.
> Métodos que apenas repassam para o repository são testados no `RepositoryTest`.

**Padrão ServiceTest — Mock + MockedStatic**

```java
@BeforeEach
void setup() {
    conexaoMock = mockStatic(ConexaoMySQL.class);   // intercepta método estático
    conexaoMock.when(ConexaoMySQL::obter).thenReturn(connection);
    clienteService = new ClienteService();
}

@AfterEach
void teardown() {
    conexaoMock.close();                            // sempre fechar o MockedStatic
}
```

**Padrão RepositoryTest — Transação revertida**

```java
@BeforeEach
void setup() throws SQLException {
    connection = ConexaoMySQL.obter();
    connection.setAutoCommit(false);                // inicia transação
}

@AfterEach
void teardown() throws SQLException {
    connection.rollback();                          // desfaz tudo — banco limpo
    connection.close();
}
```

**Suites de teste**

| Classe | Tipo | O que valida |
|---|---|---|
| `ClienteServiceTest` | Service | Validações de nome e e-mail |
| `ClienteRepositoryTest` | Repository | INSERT, SELECT, UNIQUE constraint |
| `ProdutoServiceTest` | Service | Validações de nome, SKU e preço |
| `ProdutoRepositoryTest` | Repository | INSERT, SELECT por categoria, estoque, mapeamento |
| `PedidoServiceTest` | Service | Validações de itens, rollback, commit |
| `PedidoRepositoryTest` | Repository | INSERT pedido e itens, atualizar status, buscar por cliente |

**Conceitos de Mockito aplicados**

| Conceito | Onde foi usado |
|---|---|
| `@Mock` | Dublê da `Connection` |
| `MockedStatic` | Interceptar `ConexaoMySQL.obter()` |
| `doNothing()` | Mockar métodos `void` da `Connection` |
| `verify()` | Confirmar que `commit()` ou `rollback()` foi chamado |
| `verifyNoInteractions()` | Confirmar que o banco não foi tocado quando validação falha |
| `assertThrows()` | Verificar exceção lançada com mensagem correta |
| `assertDoesNotThrow()` | Verificar que validação passou sem lançar exceção |

**Gerar relatório HTML dos testes**

```bash
mvn test
mvn surefire-report:report -DskipTests
```

Relatório gerado em `target/site/surefire-report.html`.

---

### FASE 4 — Spring Boot (próxima etapa)

Migração para Spring Boot com compreensão do que cada abstração substitui.

| O que foi feito na mão | O que o Spring faz automaticamente |
|---|---|
| `ConexaoMySQL` + `DriverManager` | `DataSource` + `HikariCP` |
| `try-with-resources` na conexão | `@Transactional` |
| `conn.setAutoCommit(false)` | `@Transactional` |
| Instanciar repositories no service | Injeção de dependência com `@Autowired` |
| `mvn shade` para o JAR | `spring-boot-maven-plugin` |
| Configurar `.env` manualmente | `application.properties` |

---

## Como rodar localmente

### Pré-requisitos

- Java 21+
- MySQL 8+
- Maven 3.8+

### 1. Configurar o banco

Execute o script do projeto [ecommerce-treino1](https://github.com/douglasinforj/ecommerce-treino1)
para criar as tabelas e popular os dados.

### 2. Configurar variáveis de ambiente

Crie o arquivo `.env` na raiz do projeto:

```env
DB_URL=jdbc:mysql://localhost:3306/vendasia?useSSL=false&serverTimezone=America/Sao_Paulo
DB_USER=seu_usuario
DB_PASS=sua_senha
```

### 3. Compilar e gerar o JAR

```bash
mvn clean package
```

O JAR será gerado em `target/vendasia-mcp.jar`.

### 4. Rodar o servidor MCP

```bash
java --enable-native-access=ALL-UNNAMED -jar target/vendasia-mcp.jar
```

### 5. Rodar os testes

```bash
# Rodar todos os testes
mvn test

# Gerar relatório HTML
mvn surefire-report:report -DskipTests
# Relatório em: target/site/surefire-report.html

# Rodar classe específica
mvn test -Dtest=ClienteServiceTest
```

### 6. Conectar no Claude Desktop

Edite o arquivo:
```
C:\Users\SEU_USUARIO\AppData\Local\Packages\Claude_pzs8sxrjxfjjc\LocalCache\Roaming\Claude\claude_desktop_config.json
```

```json
{
  "mcpServers": {
    "vendas-ia": {
      "command": "java",
      "args": [
        "--enable-native-access=ALL-UNNAMED",
        "-Dslf4j.internal.verbosity=WARN",
        "-Dfile.encoding=UTF-8",
        "-jar",
        "CAMINHO_COMPLETO\\target\\vendasia-mcp.jar"
      ],
      "env": {
        "DB_URL": "jdbc:mysql://localhost:3306/vendasia?useSSL=false&serverTimezone=America/Sao_Paulo",
        "DB_USER": "seu_usuario",
        "DB_PASS": "sua_senha"
      }
    }
  }
}
```

Feche e reabra o Claude Desktop. As 10 tools aparecerão disponíveis.

---

## Estrutura do projeto

```
src/main/java/br/com/vendasia/
├── infra/
│   └── ConexaoMySQL.java
├── model/
│   ├── Cliente.java
│   ├── Produto.java
│   ├── Pedido.java
│   ├── ItemPedido.java
│   ├── Pagamento.java
│   ├── Entrega.java
│   ├── RastreamentoLog.java
│   └── RecomendacaoProduto.java
├── repository/
│   ├── ClienteRepository.java
│   ├── ProdutoRepository.java
│   ├── PedidoRepository.java
│   ├── PagamentoRepository.java
│   ├── EntregaRepository.java
│   ├── RastreamentoLogRepository.java
│   ├── RecomendacaoProdutoRepository.java
│   └── RelatorioRepository.java
├── service/
│   ├── ClienteService.java
│   ├── ProdutoService.java
│   ├── PedidoService.java
│   ├── PagamentoService.java
│   ├── EntregaService.java
│   ├── RastreamentoLogService.java
│   ├── RecomendacaoProdutoService.java
│   └── RelatorioService.java
├── mcp/
│   ├── VendasMcpServer.java
│   ├── McpMain.java
│   └── tools/
│       ├── BaseTool.java
│       ├── TopProdutosTool.java
│       ├── ReceitaPorPeriodoTool.java
│       ├── EstoqueCriticoTool.java
│       ├── PedidosPorStatusTool.java
│       ├── TicketMedioTool.java
│       ├── RecomendacoesProdutoTool.java
│       ├── RastreamentoLogTool.java
│       ├── ResumoPagamentosTool.java
│       ├── StatusEntregasTool.java
│       └── PerfilClienteTool.java
└── Main.java

src/test/java/br/com/vendasia/
├── service/
│   ├── ClienteServiceTest.java
│   ├── ProdutoServiceTest.java
│   └── PedidoServiceTest.java 
└── repository/
    ├── ClienteRepositoryTest.java
    ├── ProdutoRepositoryTest.java
    └── PedidoRepositoryTest.java
```

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| MySQL | 8+ | Banco de dados |
| JDBC | — | Acesso ao banco sem ORM |
| MCP Java SDK | 0.14.1 | Protocolo MCP |
| Logback | 1.5.12 | Logging |
| dotenv-java | 3.0.0 | Variáveis de ambiente |
| JUnit 5 | 5.11.0 | Testes unitários |
| Mockito | 5.12.0 | Mocks nos testes |
| Maven | 3.8+ | Build e dependências |