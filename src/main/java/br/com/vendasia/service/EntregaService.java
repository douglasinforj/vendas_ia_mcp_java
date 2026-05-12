package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Entrega;
import br.com.vendasia.repository.EntregaRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;



public class EntregaService {


    // Listar Todas as Entregas
    public List<Entrega> listarTodas() throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new EntregaRepository(conn).buscarTodas();
        }
    }

    // Registrar uma entrega
    public Entrega registrar(Entrega entrega) throws SQLException {
        if (entrega.pedidoId() == null)
            throw new IllegalArgumentException("Pedido é obrigatorio para criar entrega.");
        if (entrega.transportadora() == null)
            throw new IllegalArgumentException("Transportadora é obrigatória.");

        try (Connection conn = ConexaoMySQL.obter()) {
            return new EntregaRepository(conn).inserir(entrega);
        }
    }

    // Listar Por Status
    public List<Entrega> listarPorStatus (String status) throws SQLException {
        if (status == null || status.isBlank())
            throw new IllegalArgumentException("Status não pode ser vazio.");
        try( Connection conn = ConexaoMySQL.obter()){
            return new EntregaRepository(conn).buscarPorStatus(status);
        }
    }

    // Buscar entrega por pedido
    public Optional<Entrega> buscarPorPedido (int pedidoId) throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new EntregaService().buscarPorPedido(pedidoId);
        }
    }

    // Atualizar Status
    public void atualizarStatus (int entregaId, String novoStatus, String atualizadoPor) throws SQLException {
        if (novoStatus == null || novoStatus.isBlank()) 
            throw new IllegalArgumentException("Novo status é obrigatório.");
        try (Connection conn = ConexaoMySQL.obter()) {
            new EntregaRepository(conn).atualizarStatus(entregaId, novoStatus, atualizadoPor);
        }
    }
}
