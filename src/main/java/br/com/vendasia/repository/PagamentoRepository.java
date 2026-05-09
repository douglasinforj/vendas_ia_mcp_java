package br.com.vendasia.repository;

import br.com.vendasia.model.Pagamento;
import java.sql.*;

public class PagamentoRepository {

    private final Connection conn;

    public PagamentoRepository(Connection conn) {
        this.conn = conn;
    }

    public Pagamento inserir(Pagamento pagamento) throws SQLException {
        String sql = """
                INSERT INTO pagamentos (pedido_id, forma_pagamento, parcelamento, valor_pago, data_pagamento)
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


}
