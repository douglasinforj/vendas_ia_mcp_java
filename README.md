# Projeto Vendas IA
Projeto com Java + MCP + IA (Treinamento) | Utilizando banco de dados do ecommerce já publicado em outro projeto meu para Acesso da IA
- Link do projeto banco de dados ecommerce 
[ecommerce-treino1](https://github.com/douglasinforj/ecommerce-treino1)

## Objetivo
Desenvolvi um banco de dados para treinar e aplicar conhecimentos adquiridos durante minha experiência em e-commerce, focando em novas técnicas de análise de dados para o meu currículo e portfólio. Com o rápido crescimento da IA, as empresas buscam aprendizado contínuo em seus históricos para otimizar atividades; por isso, meu objetivo atual é dominar a tríade MCP + IA + JAVA. Foco em entender a base técnica ('por debaixo dos panos') para evoluir com solidez para ferramentas como Spring.


## Visão Geral do Projeto em 4 fases

## FASE 1 — Fundação Java Core + MySQL via JDBC puro

### Camadas (Model/Repo)
Conectar no banco sem nenhum framework. Ver exatamente o que o Hibernate esconde: 

Infra
 - ConexaoMySQL

Model
 - Cliente
 - Produto
 - Pedido
 - ItemPedido

Repository
 - ClienteRepository   (testado Main)
 - ProdutoRepository   (testado Main)


O que foi abordado:

- DriverManager: Gerenciador de drivers JDBC -> Ponto de entrada pra qualquer conexão
- Connection: Representa a conexão com o banco	-> Recurso caro — precisa ser fechado
- PreparedStatement: SQL parametrizado	-> Segurança contra SQL Injection
- ResultSet: Cursor sobre as linhas retornadas -> Como você itera os dados
- try-with-resources: Fechamento automático de recursos	-> Evita memory/connection leak

- Por que JDBC primeiro e não JPA?
Porque quando o Hibernate gera um SQL errado em produção, quem sabe JDBC consegue debugar. 
Quem só sabe Hibernate fica perdido. 

---

#### FASE 2 — Arquitetura em camadas (Model/Repo/Service)
Expandir o domínio de vendas com MySQL. Model → Repository → Service.

Model
 - Cliente    (fase 1)
 - Produto    (fase 1)
 - Pedido     (fase 1)
 - ItemPedido (fase 1)
 - Pagamento
 - Entrega
 - RastreamentoLog
 - RecomendacaoProduto 

Repository
 - ClienteRepository (Refatorado)
 - ProdutoRepository (Refarorado)
 - PedidoRepository  (ItemPedido)
 - PagamentoRepository
 - EntregaRepository
 - RastreamentoLogRepository
 - RecomendacaoProdutoRepository
 - RelatorioRepository

Service
 - ClienteService  (testado Main)
 - ProdutoService  (testado Main)
 - PedidoService   (testado Main)
 - PagamentoService (testado Main)
 - EntregaService (testado Main)
 - RastreamentoLogService (testado Main)
 - RecomendacaoProdutoService (testado Main)
 - RelatorioService (testado Main)


#### FASE 3 — MCP Server em Java (tools para o Claude)
Construir um servidor MCP que expõe as queries do banco como ferramentas chamáveis pelo Claude. Tratar como o protocolo por dentro: como o JSON-RPC funciona, como as tools são registradas, como o modelo decide o que chamar.

O que o MCP é, de verdade?
MCP é um protocolo de comunicação — um contrato de como um modelo de IA pode descobrir e chamar ferramentas externas. Ele define:
 - Como o servidor anuncia quais ferramentas tem disponíveis
 - Como o modelo solicita a execução de uma ferramenta
 - Como o servidor retorna o resultado pro modelo
O transporte é JSON-RPC — mensagens JSON trafegando via stdio ou HTTP/SSE.

[Referências Doc](https://java.sdk.modelcontextprotocol.io/latest/quickstart/#dependencies) 

Dependências
- pom  (Dependencias e pugins necessários)


## Camada MCP

### `mcp/tools/`

| Classe | Responsabilidade |
|---|---|
| `BaseTool` | Classe abstrata base para todas as tools. Define contratos comuns: `especificacao()`, `schemaVazio()`, `schema()`, `sucessoResult()`, `erroResult()`, `getIntArgument()` |
| `TopProdutosTool` | Retorna os produtos mais vendidos em quantidade |
| `ReceitaPorPeriodoTool` | Retorna receita de vendas agrupada por dia em um período |
| `EstoqueCriticoTool` | Retorna produtos com estoque abaixo do mínimo informado |
| `PedidosPorStatusTool` | Retorna pedidos agrupados por status com totais |
| `TicketMedioTool` | Retorna top 10 clientes com maior ticket médio de compra |

> **Sobre o schema das tools:** o `inputSchema` é uma `String JSON` pura passada diretamente no construtor do `Tool` — mais simples, sem o objeto `McpSchema.JsonSchema` (depreciado na versão 1.1.0), e exatamente o que o protocolo MCP espera por baixo dos panos.

---

### `mcp/`

| Classe | Responsabilidade |
|---|---|
| `VendasMcpServer` | Monta e configura o servidor MCP: registra o transport STDIO, instancia as tools e expõe as capabilities ao Claude Desktop |
| `McpMain` | Entrypoint do servidor MCP. Inicializa o `VendasMcpServer` e mantém o processo vivo aguardando conexões via STDIO |

---

## Resources

### `logback.xml`

Configuração de logging crítica para o funcionamento do MCP.

**Por que isso importa:**
O protocolo MCP usa `stdout` exclusivamente para trafegar mensagens JSON-RPC entre o Claude Desktop e o servidor. Qualquer texto não-JSON no `stdout` — inclusive logs — quebra a comunicação, pois o Claude tenta parsear tudo como JSON e falha.

| Configuração | Valor | Motivo |
|---|---|---|
| `ConsoleAppender target` | `System.err` | Logs nunca vão pro `stdout` |
| `root level` | `WARN` | Silencia bibliotecas externas (Jackson, MCP core) |
| `logger br.com.vendasia` | `INFO` | Sua aplicação loga normalmente |
| `RollingFileAppender` | `logs/vendas-mcp.log` | Histórico persistido em arquivo |
| `NopStatusListener` | — | Suprime mensagens internas do Logback no `stdout` durante inicialização |
   

---

#### FASE 4 — Spring Boot (migração e modernização)
Migrar tudo pra Spring Boot, com o entendimento do que acontece por debaixo dos panos.

---





