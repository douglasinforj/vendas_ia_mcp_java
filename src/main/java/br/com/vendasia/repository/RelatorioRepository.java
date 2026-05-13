package br.com.vendasia.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RelatorioRepository {

    private final Connection conn;

    public RelatorioRepository(Connection conn) {
        this.conn = conn;
    }

    public List<String> topProdutos(int limite) throws SQLException {
        String sql = """
                SELECT p.nome, p.categoria,
                       SUM(ip.quantidade) AS total_vendido,
                       SUM(ip.quantidade * ip.preco_unitario) AS receita_total
                FROM produtos p
                JOIN itens_pedido ip ON ip.produto_id = p.id
                JOIN pedidos ped ON ped.id = ip.pedido_id
                WHERE ped.status IN ('Pago', 'Enviado', 'Entregue')
                GROUP BY p.id, p.nome, p.categoria
                ORDER BY total_vendido DESC
                LIMIT ?
                """;
        return executar(sql, 
                        stmt -> stmt.setInt(1, limite), 
                        rs -> String.format("%s [%s] -> %d un. | R$ %.2f",
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getInt("total_vendido"),
                        rs.getBigDecimal("receita_total")));
    }

    // Receita por período
    public List<String> receitaPorPeriodo(String dataInicio, String dataFim) throws SQLException {
        String sql = """
                SELECT DATE(data_pedido) as dia,
                       COUNT(*) AS pedidos,
                       SUM(valor_total) AS receita
                FROM pedidos
                WHERE status IN ('Pago', 'Enviado', 'Entregue')
                  AND data_pedido BETWEEN ? AND ?
                GROUP BY dia
                ORDER BY dia
                """;
        return executar(sql, 
                        stmt -> {stmt.setString(1, dataInicio); stmt.setString(2, dataFim);},
                        rs -> String.format("%s | %d pedidos | R$ %.2f",
                        rs.getString("dia"),
                        rs.getInt("pedidos"),
                        rs.getBigDecimal("receita")));
    }


    // Analise Estoque crítico
    public List<String> estoqueCritico(int minimo) throws SQLException {
        String sql = """
                SELECT nome, sku, estoque_atual, categoria
                FROM produtos
                WHERE estoque_atual <= ?
                ORDER BY estoque_atual ASC
                """;
        return executar(sql,
             stmt -> stmt.setInt(1, minimo),
             rs -> String.format("[%s] %s - %d un. | Cat: %s",
                rs.getString("sku"),
                rs.getString("nome"),
                rs.getInt("estoque_atual"),
                rs.getString("categoria")));
    }

    // pedidosPorStatus
    public List<String> pedidosPorStatus() throws SQLException {
        String sql = """
                SELECT status, COUNT(*) AS total, SUM(valor_total) AS valor
                FROM pedidos
                GROUP BY status
                ORDER BY total DESC
                """;
        return executar(sql, 
            stmt -> {}, 
            rs -> String.format("%-12s | %d pedidos | R$ %.2f", 
                rs.getString("status"),
                rs.getInt("total"),
                rs.getBigDecimal("valor")));
    }

    // Ticket médio por Cliente  Máximo 10
    public List<String> ticketMedioPorCliente() throws SQLException {
        String sql = """
                SELECT c.nome,
                       COUNT(p.id) AS pedidos,
                       AVG(p.valor_total) AS ticket_medio,
                       SUM(p.valor_total) AS total_gasto
                FROM clientes c
                JOIN pedidos p ON p.cliente_id = c.id
                WHERE p.status != 'Cancelado'
                GROUP BY c.id, c.nome
                ORDER BY ticket_medio DESC
                LIMIT 10
                """;

        return executar(sql,
            stmt -> {},
            rs -> String.format("%-30s | %d pedidos | Ticket: R$ %.2f | Total: R$ %.2f",
                rs.getString("nome"),
                rs.getInt("pedidos"),
                rs.getBigDecimal("ticket_medio"),
                rs.getBigDecimal("total_gasto")));
    }

    

    // Helpers funcionais - evitam repetição de try-with-resources
    /**
     * Interfaces para comportamento dinâmico Para o método executar
     * 
     * ParamSetter - Preeche parâmetros SQL
     * executar(...) -> params.set(stmt)  -> stmt.setString(1, categoria)
     * 
     */
    @FunctionalInterface              // Indica que a interface só tem método abstrato
    public interface ParamSetter {
        void set(PreparedStatement stmt) throws SQLException;
    }
    /** 
     * RowMapper - Converter ResultSet "Como trasnformar uma linha do banco"
     * ResultSet -> mapper.map(rs) -> String
     */
    @FunctionalInterface             // Indica que a interface só tem método abstrato
    public interface RowMapper {
        String map(ResultSet rs) throws SQLException;
    }

    private List<String> executar(String sql, ParamSetter params, RowMapper mapper) throws SQLException {
        List<String> resultado = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            params.set(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) resultado.add(mapper.map(rs));
            }
        }
        return resultado;
    }
}