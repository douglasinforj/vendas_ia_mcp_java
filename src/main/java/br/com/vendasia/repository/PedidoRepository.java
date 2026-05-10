package br.com.vendasia.repository;

import br.com.vendasia.model.ItemPedido;
import br.com.vendasia.model.Pedido;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {

    private final Connection conn;

    public PedidoRepository(Connection conn) {
        this.conn = conn;
    }


    // Buscar todos os pedidos //

    public List<Pedido> buscarTodos() throws SQLException {
    List<Pedido> lista = new ArrayList<>();
    String sql = """
            SELECT id, cliente_id, data_pedido, status, valor_total
            FROM pedidos ORDER BY data_pedido DESC
            """;

    try (PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) lista.add(mapear(rs));
    }
    return lista;
}


    // Inserir pedidos //

    public Pedido inserir(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (cliente_id, status, valor_total) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pedido.clienteId());
            stmt.setString(2, pedido.status());
            stmt.setBigDecimal(3, pedido.valorTotal());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Pedido(keys.getInt(1), pedido.clienteId(),
                            null, pedido.status(), pedido.valorTotal());
                }
            }
        }
        throw new SQLException("Falha ao recuperar ID do pedido.");
    }

    // Inserir Item ao pedido

    public void inserirItem(ItemPedido item) throws SQLException {
        String sql = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.pedidoId());
            stmt.setInt(2, item.produtoId());
            stmt.setInt(3, item.quantidade());
            stmt.setBigDecimal(4, item.precoUnitario());
            stmt.executeUpdate();
        }
    }


    // Atualizar Status do Pedido
    public void atualizarStatus(int pedidoId, String novoStatus) throws SQLException {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setInt(2, pedidoId);
            stmt.executeUpdate();
        }
    }


    // Buscar por Status
    public List<Pedido> buscarPorStatus(String status) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
                SELECT id, cliente_id, data_pedido, status, valor_total
                FROM pedidos WHERE status = ? ORDER BY data_pedido DESC
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }


    //busca por Cliente
    public List<Pedido> buscarPorCliente(int clienteId) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
                SELECT id, cliente_id, data_pedido, status, valor_total
                FROM pedidos WHERE cliente_id = ? ORDER BY data_pedido DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Pedido mapear(ResultSet rs) throws SQLException {
        return new Pedido(
            rs.getInt("id"),
            rs.getInt("cliente_id"),
            rs.getTimestamp("data_pedido").toLocalDateTime(),
            rs.getString("status"),
            rs.getBigDecimal("valor_total")
        );
    }
}