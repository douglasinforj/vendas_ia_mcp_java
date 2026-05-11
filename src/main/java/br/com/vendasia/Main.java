package br.com.vendasia;

import br.com.vendasia.service.ClienteService;
import br.com.vendasia.service.ProdutoService;
import br.com.vendasia.service.PedidoService;

import br.com.vendasia.model.ItemPedido;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        PedidoService pedidoService = new PedidoService();


        try {
            // Teste: Listar clientes
            System.out.println("\n====Clientes====");
            clienteService.listarTodos()
                .forEach(c -> System.out.println(c.nome() + " | " + c.tipoCliente()));

            // Teste: Estoque crítico
            System.out.println("\n=== Produtos - Estoque Crítico ===");
            produtoService.topMaisVendidos(5).forEach(System.out::println);

            // Teste: registrar pedido com transação
            System.out.println("\n==== Registrando Pedido=====");
            List<ItemPedido> itens = List.of(
                new ItemPedido(null, 1, 2, BigDecimal.ZERO),  // produto 1, qtd 2
                new ItemPedido(null, 2, 1, BigDecimal.ZERO)   // produto 2, qtd 1
            );
            pedidoService.registrarPedido(1, itens, "PIX", 1);
            
            
        
        } catch(SQLException e){
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validação: " + e.getMessage());
        }
    }
}