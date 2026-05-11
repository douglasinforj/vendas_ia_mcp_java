package br.com.vendasia.repository;

import br.com.vendasia.model.Pagamento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoRepository {

    private final Connection conn;

    public PagamentoRepository(Connection conn) {
        this.conn = conn;
    }

    // Listar Pagamentos
    public List<Pagamento> buscarTodos() throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        String sql = """
                SELECT id, pedido_id, forma_pagamento, parcelas, valor_pago, data_pagamento
                FROM pagamentos ORDER BY data_pagamento DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }


    // Inserir pagamento
    public Pagamento inserir(Pagamento pagamento) throws SQLException {
        String sql = """
                INSERT INTO pagamentos (pedido_id, forma_pagamento, parcelas, valor_pago, data_pagamento)
                VALUES (?,?,?,?, NOW())
                """;
        try(PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pagamento.pedidoId());
            stmt.setString(2, pagamento.formaPagamento());
            stmt.setInt(3, pagamento.parcelas());
            stmt.setBigDecimal(4, pagamento.valorPago());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()){
                    return new Pagamento(
                        keys.getInt(1),
                        pagamento.pedidoId(),
                        pagamento.formaPagamento(),
                        pagamento.parcelas(),
                        pagamento.valorPago(),
                        null
                    );
                }
            }
            throw new SQLException("Falha ao Inserir pagamento.");
        }
    }

    // Buscar pagamento por Pedido
    public List<Pagamento> buscarPorPedido(int pedidoId) throws SQLException {
        List<Pagamento> lista = new ArrayList<>();
        String sql = """
                SELECT id, pedido_id, forma_pagamento, parcelas, valor_pago, data_pagamento
                FROM pagamentos WHERE pedido_id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }



    //Mapeamento
    private Pagamento mapear(ResultSet rs) throws SQLException {
        return new Pagamento(
            rs.getInt("id"),
            rs.getInt("pedido_id"),
            rs.getString("forma_pagamento"),
            rs.getInt("parcelas"),
            rs.getBigDecimal("valor_pago"),
            rs.getTimestamp("data_pagamento") != null
                ? rs.getTimestamp("data_pagamento").toLocalDateTime() : null
        );
    }


}
