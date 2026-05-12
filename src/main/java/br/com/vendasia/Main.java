package br.com.vendasia;

import br.com.vendasia.service.ClienteService;
import br.com.vendasia.service.PagamentoService;
import br.com.vendasia.service.ProdutoService;
import br.com.vendasia.service.PedidoService;
import br.com.vendasia.service.EntregaService;


import br.com.vendasia.model.ItemPedido;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        PedidoService pedidoService = new PedidoService();
        PagamentoService pagamentoService = new PagamentoService();
        EntregaService entregaService = new EntregaService();


        try {
            // ==============Teste Cliente: ===============
            // Listar clientes
            System.out.println("\n====Clientes====");
            clienteService.listarTodos()
                .forEach(c -> System.out.println(c.nome() + " | " + c.tipoCliente()));

            // =============Teste Produtos: =============== 
            // Estoque crítico
            System.out.println("\n=== Produtos - Estoque Crítico ===");
            produtoService.topMaisVendidos(5).forEach(System.out::println);

            // =============Teste Pedidos: ================
            // Registrar pedido com transação
            System.out.println("\n==== Registrando Pedido=====");
            List<ItemPedido> itens = List.of(
                new ItemPedido(null, 1, 2, BigDecimal.ZERO),  // produto 1, qtd 2
                new ItemPedido(null, 2, 1, BigDecimal.ZERO)   // produto 2, qtd 1
            );
            pedidoService.registrarPedido(1, itens, "PIX", 1);


            // ============Teste Pagamento: ===============
            // Listar todos os pagamento
            System.out.println("\n=====Pagamentos Registrados======");
            pagamentoService.listarTodos().forEach(p ->
                System.out.printf("Pedido #%d | %s | %dx | R$ %,2f | %s%n",
                    p.pedidoId(),
                    p.formaPagamento(),
                    p.parcelas(),
                    p.valorPago(),
                    p.dataPagamento() != null ? p.dataPagamento() : "sem data")
            );

            // Listar pagamento de um pedido específico
            System.out.println("\n=== Pagamento do Pedido #1======");
            pagamentoService.listarPorPedido(1).forEach(p ->
                System.out.printf("Forma: %s | Parcelas: %d | Valor: R$ %.2f%n",
                    p.formaPagamento(), p.parcelas(), p.valorPago()
                )
            );


            // ==============Teste Entrega:=============
            // Listar todas as entregas
            System.out.println("\n ====ENTREGAS REGISTRADAS====");
            entregaService.listarTodas().forEach(e ->
                System.out.printf("Pedido #d  | %s | Status: %s | Precisão: %s%n",
                    e.pedidoId(),
                    e.transportadora() != null ? e.transportadora() : "Sem transportadora",
                    e.status(),
                    e.dataPrevisaoEntrega() != null ? e.dataPrevisaoEntrega() : "Sem previsão")
            );

            // Estregas por Status
            System.out.println("\n=== ENTREGAS PENDENTES ===");
            entregaService.listarPorStatus("Pendente").forEach(e ->
                System.out.printf("Pedido #%d | Rastreio: %s | %s%n",
                    e.pedidoId(),
                    e.codigoRastreio() != null ? e.codigoRastreio() : "sem rastreio",
                    e.transportadora())
            );

            
        
        } catch(SQLException e){
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validação: " + e.getMessage());
        }
    }
}