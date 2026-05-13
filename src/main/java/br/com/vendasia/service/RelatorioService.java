package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.repository.RelatorioRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Queries analisticas - Cada método aqui vira um Tool no MCP
 */

public class RelatorioService {

    // Top Produto
    public List<String> topProdutos(int limite) throws SQLException {
        if (limite <= 0) throw new IllegalArgumentException("Limite deve ser maior que zero.");
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RelatorioRepository(conn).topProdutos(limite);
        }
    }

    // Receita por Periodo
    public List<String> receitaPorPeriodo(String dataInicio, String dataFim) throws SQLException {
        if(dataInicio == null || dataFim == null)
            throw new IllegalArgumentException("Datas de início e fim são obrigatório.");
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RelatorioRepository(conn).receitaPorPeriodo(dataInicio, dataFim);
        }
    }

    // Estoque crítico
    public List<String> estoqueCritico(int minimo) throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()){
            return new RelatorioRepository(conn).estoqueCritico(minimo);
        }
    }

    // Pedido por status
    public List<String> pedidosPorStatus() throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()){
            return new RelatorioRepository(conn).pedidosPorStatus();
        }
    }

    // Ticket médio pro cliente
    public List<String> ticketMedioPorCliente() throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RelatorioRepository(conn).ticketMedioPorCliente();
        }
    }

}
