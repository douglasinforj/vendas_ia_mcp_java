package br.com.vendasia.repository;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    public List<Produto> buscarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, sku, preco_custo, preco_venda, estoque_atual, categoria FROM produtos";

        try(Connection conn = ConexaoMySQL.obter();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                produtos.add(mapearProduto(rs));
            }
        }
        return produtos;
    }

    public List<Produto> buscarPorCategoria(String categoria) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, sku, preco_custo, preco_venda, estoque_atual, categoria " +
                     "FROM produtos WHERE categoria = ? ORDER BY preco_venda DESC";

        try (Connection conn = ConexaoMySQL.obter();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) produtos.add(mapearProduto(rs));
            }
        }
        return produtos;
    }


    //----QUERY ANALÍTICA: top produtos mais vendidos------------------
    // Essa query vai virar uma Tool do MCP na Fase 3!
    public List<String> topProdutosMaisVendidos(int limite) throws SQLException {
        List<String> resultado = new ArrayList<>();

        String sql = """
                SELECT p.nome, p.categoria,
                       SUM(ip.quantidade) AS total_vendido,
                       SUM(ip.quantidade * ip.preco_unitario) AS receita_total
                FROM produtos p
                INNER JOIN itens_pedido ip ON ip.produto_id = p.id
                INNER JOIN pedidos ped ON ped.id = ip.pedido_id
                WHERE ped.status IN ('Pago', 'Enviado', 'Entregue')
                GROUP BY p.id, p.nome, p.categoria
                ORDER BY total_vendido DESC
                LIMIT ?
                """;

        try (Connection conn = ConexaoMySQL.obter();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String linha = String.format(
                        "#%s [%s] → %d unidades | R$ %.2f",
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getInt("total_vendido"),
                        rs.getBigDecimal("receita_total")
                    );
                    resultado.add(linha);
                }
            }
        }
        return resultado;
    }

    //--- Estoque Crítico: Produtos abaixo do mínimo-------------
    public List<Produto> buscarEstoqueCritico(int estoqueMinimo) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = """
                SELECT id, nome, sku, preco_custo, preco_venda, estoque_atual, categoria
                FROM produtos
                WHERE estoque_atual <= ?
                ORDER BY estoque_atual ASC
                """;

        try (Connection conn = ConexaoMySQL.obter();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, estoqueMinimo);
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    produtos.add(mapearProduto(rs));
                }
            }
        return produtos;
        }
    }







    // Método Privado: mapeia ResultSet -> Record
    private Produto mapearProduto(ResultSet rs) throws SQLException {
        return new Produto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("sku"),
            rs.getBigDecimal("preco_custo"),
            rs.getBigDecimal("preco_venda"),
            rs.getInt("estoque_atual"),
            rs.getString("categoria")
        );
    }
}
