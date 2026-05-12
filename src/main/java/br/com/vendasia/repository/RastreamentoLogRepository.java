package br.com.vendasia.repository;

import br.com.vendasia.model.RastreamentoLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class RastreamentoLogRepository {

    private final Connection conn;

    public RastreamentoLogRepository(Connection conn) {
        this.conn = conn;
    }

    // Listar Todo os rastreios
    public List<RastreamentoLog> buscarTodos() throws SQLException {
        List<RastreamentoLog> lista = new ArrayList<>();
        String sql = """
                SELECT id, entrega_id, status, localizacao, data_hora, descricao
                FROM rastreamento_log ORDER BY data_hora DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
    }

    // Inserir Rastreamento
    public RastreamentoLog inserir(RastreamentoLog log) throws SQLException {
        String sql = """
                INSERT INTO rastreamento_log (entrega_id, status, localizacao, descricao)
                VALUE (?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, log.entregaId());
            stmt.setString(2, log.status());
            stmt.setString(3, log.localizacao());
            stmt.setString(4, log.descricao());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new RastreamentoLog(keys.getInt(1), log.entregaId(),
                        log.status(), log.localizacao(), null, log.descricao());
                }
            }
        }
        throw new SQLException("Falha ao inserir log de rastreamento.");   
    }

    private RastreamentoLog mapear(ResultSet rs) throws SQLException {
        return new RastreamentoLog(
            rs.getInt("id"),
            rs.getInt("entrega_id"),
            rs.getString("status"),
            rs.getString("localizacao"),
            rs.getTimestamp("data_hora").toLocalDateTime(),
            rs.getString("descricao")
        );
    }

}
