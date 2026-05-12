package br.com.vendasia.repository;

import br.com.vendasia.model.Entrega;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class EntregaRepository {

    private final Connection conn;

    public EntregaRepository(Connection conn) {
        this.conn = conn;
    }

    // Listar todas as Entregas
    public List<Entrega> buscarTodas() throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = """
                SELECT id, pedido_id, codigo_rastreio, transportadora, data_envio,
                        data_entrega, status, data_previsao_entrega, observacoes,
                        ultima_atualizacao, atualizado_por
                FROM entregas ORDER BY data_envio DESC
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
    }

    // Inserir entregas
    public Entrega inserir(Entrega entrega) throws SQLException {
        String sql = """
                INSERT INTO entregas (pedido_id, codigo_rastreio, transportadora,
                                    data_envio, status, data_previsao_entrega, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1, entrega.pedidoId());
            stmt.setString(2, entrega.codigoRastreio());
            stmt.setString(3, entrega.transportadora());
            stmt.setObject(4,entrega.dataEnvio());
            stmt.setString(5, entrega.status());
            stmt.setObject(6, entrega.dataPrevisaoEntrega());
            stmt.setString(7, entrega.observacoes());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Entrega(keys.getInt(1), entrega.pedidoId(),
                            entrega.codigoRastreio(), entrega.transportadora(),
                            entrega.dataEnvio(), null, entrega.status(),
                            entrega.dataPrevisaoEntrega(), entrega.observacoes(),
                            null, null);
                }
            }
        }
        throw new SQLException("Falha ao inserir entrega");
    }

    // Atualizar Status
    public void atualizarStatus(int entregaId, String novoStatus, String atualizadoPor) throws SQLException {
        String sql = """
                UPDATE entrega SET status = ?, atualizado_por = ?
                WHERE id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setString(2, atualizadoPor);
            stmt.setInt(3, entregaId);
            stmt.executeUpdate();
        }   
    }

    // Buscar Entraga Por Pedido
    public Optional<Entrega> buscarPorPedido(int pedidoId) throws SQLException {
        String sql = """
                SELECT id, pedido_id, codigo_rastreio, transportadora, data_envio,
                        data_entrega, status, data_previsao_entrega, observacoes,
                        ultima_atualizacao, atualizado_por
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        }
        return Optional.empty();
    }

    // Buscar por Status
    public List<Entrega> buscarPorStatus(String status) throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        String sql = """
                SELECT id, pedido_id, codigo_rastreio, transportadora, data_envio,
                        data_entrega, status, data_previsao_entrega, observacoes,
                        ultima_atualizacao, atualizado_por
                FROM entregas WHERE status = ? ORDER BY data_envio DESC
                """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }


    // Mapeamento
    private Entrega mapear(ResultSet rs) throws SQLException {
        return new Entrega(
            rs.getInt("id"),
            rs.getInt("pedido_id"),
            rs.getString("codigo_rastreio"),
            rs.getString("transportadora"),
            rs.getTimestamp("data_envio") != null
                ? rs.getTimestamp("data_envio").toLocalDateTime() : null,
            rs.getTimestamp("data_entrega") != null
                ? rs.getTimestamp("data_entrega").toLocalDateTime() : null,
            rs.getString("status"),
            rs.getDate("data_previsao_entrega") != null
                ? rs.getDate("data_previsao_entrega").toLocalDate() : null,
            rs.getString("observacoes"),
            rs.getTimestamp("ultima_atualizacao") != null
                ? rs.getTimestamp("ultima_atualizacao").toLocalDateTime() : null,
            rs.getString("atualizado_por")
        );
    }
    



}
