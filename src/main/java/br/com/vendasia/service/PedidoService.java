package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.*;
import br.com.vendasia.repository.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PedidoService {

    /**
     * Registra um pedido completo em uma única transação atômica.
     *
     * Ou tudo acontece, ou nada acontece.
     * Se qualquer etapa falhar, o banco volta ao estado anterior.
     *
     * @param clienteId    ID do cliente
     * @param itens        lista de itens do pedido
     * @param formaPgto    forma de pagamento
     * @param parcelas     número de parcelas
     */
    public Pedido registrarPedido(int clienteId,
                                  List<ItemPedido> itens,
                                  String formaPgto,
                                  int parcelas) throws SQLException {

        // Validações antes de abrir conexão
        if (itens == null || itens.isEmpty())
            throw new IllegalArgumentException("Pedido deve ter ao menos um item.");

        Connection conn = ConexaoMySQL.obter();
        try {
            // ── Inicia transação ──────────────────────────
            conn.setAutoCommit(false);

            // Repositorios compartilham a mesma conexão
            ProdutoRepository produtoRepo   = new ProdutoRepository(conn);
            PedidoRepository  pedidoRepo    = new PedidoRepository(conn);
            PagamentoRepository pagtoRepo   = new PagamentoRepository(conn);

            // ── PASSO 1: Calcular total e validar estoque ────
            BigDecimal total = BigDecimal.ZERO;

            for (ItemPedido item : itens) {
                Produto produto = produtoRepo.buscarPorId(item.produtoId())
                    .orElseThrow(() -> new SQLException(
                        "Produto id=" + item.produtoId() + " não encontrado."));

                if (produto.estoqueAtual() < item.quantidade()) {
                    throw new SQLException(String.format(
                        "Estoque insuficiente para '%s'. Disponível: %d, Solicitado: %d",
                        produto.nome(), produto.estoqueAtual(), item.quantidade()));
                }

                total = total.add(produto.precoVenda()
                    .multiply(BigDecimal.valueOf(item.quantidade())));
            }

            // ── PASSO 2: Inserir cabeçalho do pedido ────────
            Pedido pedido = pedidoRepo.inserir(
                new Pedido(null, clienteId, null, "Aguardando", total)
            );

            // ── PASSO 3: Inserir itens + deduzir estoque ────
            for (ItemPedido item : itens) {
                Produto produto = produtoRepo.buscarPorId(item.produtoId()).get();

                // Item com o ID do pedido recém criado
                ItemPedido itemComPedido = new ItemPedido(
                    pedido.id(), item.produtoId(),
                    item.quantidade(), produto.precoVenda()
                );
                pedidoRepo.inserirItem(itemComPedido);

                // Deduzir estoque
                int novoEstoque = produto.estoqueAtual() - item.quantidade();
                produtoRepo.atualizarEstoque(produto.id(), novoEstoque);
            }

            // ── PASSO 4: Registrar pagamento ─────────────────
            pagtoRepo.inserir(new Pagamento(
                null, pedido.id(), formaPgto, parcelas, total, null
            ));

            // ── PASSO 5: Atualizar status do pedido ──────────
            pedidoRepo.atualizarStatus(pedido.id(), "Pago");

            // ── COMMIT: tudo certo, confirma no banco ────────
            conn.commit();
            System.out.println("Pedido #" + pedido.id() + " registrado com sucesso! Total: R$ " + total);
            return pedido;

        } catch (Exception e) {
            // ── ROLLBACK: algo falhou, desfaz tudo ───────────
            System.err.println("Erro ao registrar pedido — rollback executado.");
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Lista todos os pedidos de um cliente específico.
     * Método utilizado pelo PerfilClienteTool para exibir histórico de compras.
     *
     * @param clienteId ID do cliente
     * @return Lista de pedidos do cliente ordenados por data (mais recentes primeiro)
     * @throws SQLException se ocorrer erro no banco de dados
     */
    public List<Pedido> listarPorCliente(int clienteId) throws SQLException {
        if (clienteId <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser maior que zero.");
        }

        try (Connection conn = ConexaoMySQL.obter()) {
            PedidoRepository pedidoRepo = new PedidoRepository(conn);
            return pedidoRepo.buscarPorCliente(clienteId);
        }
    }
}