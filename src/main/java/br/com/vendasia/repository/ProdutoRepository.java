package br.com.vendasia.repository;

//import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {

    private final Connection conn;

    public ProdutoRepository(Connection conn) {
        this.conn = conn;
    }

    //Listar todos os produtos

    public List<Produto> buscarTodos() throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, sku, preco_custo, preco_venda, estoque_atual, categoria FROM produtos";

        try(//Connection conn = ConexaoMySQL.obter();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while(rs.next()){
                produtos.add(mapear(rs));
            }
        }
        return produtos;
    }

    // Buscar produto por ID

    public Optional<Produto> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, sku, preco_custo, preco_venda, estoque_atual, categoria FROM produtos WHERE id = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()){
                    return Optional.of(mapear(rs));
                }
            }
        }
        return Optional.empty();
    }



    public List<Produto> buscarPorCategoria(String categoria) throws SQLException {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT id, nome, sku, preco_custo, preco_venda, estoque_atual, categoria " +
                     "FROM produtos WHERE categoria = ? ORDER BY preco_venda DESC";

        try (//Connection conn = ConexaoMySQL.obter();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) produtos.add(mapear(rs));
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

        try (//Connection conn = ConexaoMySQL.obter();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    resultado.add(String.format("%s [%s] -> %d un. | R$ %.2f",
                        rs.getString("nome"),
                        rs.getString("categoria"),
                        rs.getInt("total_vendido"),
                        rs.getBigDecimal("receita_total")));
                }
                
                /*
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
                */
            }
        }
        return resultado;
    }

    // Chamado pelo PedidoService dentro da transação
    public void atualizarEstoque(int produto_id, int novaQuantidade) throws SQLException{
        String sql = "UPDATE produtos SET estoque_atual = ? WHERE id = ? ";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, produto_id);
            int linha = stmt.executeUpdate();
            if (linha == 0) throw new SQLException("Produto id=" + produto_id + "não encontrado");
        }
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

        try (//Connection conn = ConexaoMySQL.obter();
        PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, estoqueMinimo);
            try(ResultSet rs = stmt.executeQuery()){
                while (rs.next()) {
                    produtos.add(mapear(rs));
                }
            }
        return produtos;
        }
    }

    // Método Privado: mapeia ResultSet -> Record
    private Produto mapear(ResultSet rs) throws SQLException {
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
