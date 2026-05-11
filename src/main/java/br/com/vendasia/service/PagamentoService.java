package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Pagamento;
import br.com.vendasia.repository.PagamentoRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PagamentoService {


    //Registrar pagamentos
    public Pagamento registrar(Pagamento pagamento) throws SQLException {
        if (pagamento.valorPago() ==  null || pagamento.valorPago().signum() <= 0)
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero.");
        if (pagamento.formaPagamento() == null || pagamento.formaPagamento().isBlank())
            throw new IllegalArgumentException("Forma de Pagamento Obrigatória.");

        try (Connection conn = ConexaoMySQL.obter()) {
            return new PagamentoRepository(conn).inserir(pagamento);
        }
    }

    //Listar Pagamentos
    public List<Pagamento> listarTodos() throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new PagamentoRepository(conn).buscarTodos();
        }
    }

    //Listar Pagamento por Pedido
    public List<Pagamento> listarPorPedido(int pedidoId) throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new PagamentoRepository(conn).buscarPorPedido(pedidoId);
        }
    }
}
