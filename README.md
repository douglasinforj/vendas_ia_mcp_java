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

### ✅ FASE 1 — Java Core + JDBC puro

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

> **Por que eu foquei no JDBC antes de JPA?**
> Quando o Hibernate gera um SQL errado em produção, quem sabe JDBC consegue debugar.
> Quem só sabe Hibernate pode ficar perdido, é algo que na minha carreira dou bastante atenção.

---

### ✅ FASE 2 — Arquitetura em camadas (Model → Repository → Service)

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
