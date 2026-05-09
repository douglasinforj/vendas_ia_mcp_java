package br.com.vendasia;

import br.com.vendasia.service.ClienteService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ClienteService clienteService = new ClienteService();


        try {
            // Teste: Listar clientes
            System.out.println("\n====Clientes====");
            clienteService.listarTodos()
                .forEach(c -> System.out.println(c.nome() + " | " + c.tipoCliente()));
        
        } catch(SQLException e){
            System.err.println("Erro SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Validação: " + e.getMessage());
        }
    }
}