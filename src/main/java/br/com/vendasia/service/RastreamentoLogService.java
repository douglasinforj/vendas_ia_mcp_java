package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.RastreamentoLog;
import br.com.vendasia.repository.RastreamentoLogRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RastreamentoLogService {

    // Listar todos Reatreamentos
    public List<RastreamentoLog> listarTodos() throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RastreamentoLogRepository(conn).buscarTodos();
        }
    }

    // Registrar rastreamentos com validações
    public RastreamentoLog registrar(RastreamentoLog log) throws SQLException {
        if (log.status() == null || log.status().isBlank())
            throw new IllegalArgumentException("Status do rastreamento é obrigatório.");
        if (log.entregaId() == null)
            throw new IllegalArgumentException("ID da entrega é obrigatório.");
        
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RastreamentoLogRepository(conn).inserir(log);
        }
    }

    //Buscar por Historico da entrega (BuscarPorEntrega())
    public List<RastreamentoLog> buscarHistorico(int entregaId) throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RastreamentoLogRepository(conn).buscarPorEntrega(entregaId);
        }
    }



}
