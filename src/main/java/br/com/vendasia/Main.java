package br.com.vendasia;

import br.com.vendasia.service.ClienteService;
import br.com.vendasia.service.ProdutoService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();


        try {
            // Teste: Listar clientes
            System.out.println("\n====Clientes====");
            clienteService.listarTodos()
                .forEach(c -> System.out.println(c.nome() + " | " + c.tipoCliente()));

            // Teste: Estoque crítico
            System.out.println("\n=== Produtos - Estoque Crítico ===");
            produtoService.topMaisVendidos(5).forEach(System.out::println);
            
        
        } catch(SQLException e){
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validação: " + e.getMessage());
        }
    }
}