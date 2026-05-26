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
