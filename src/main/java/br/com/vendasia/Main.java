package br.com.vendasia;

import br.com.vendasia.repository.ClienteRepository;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args){

        ClienteRepository clienteRepo = new ClienteRepository();

        try{
            System.out.println("======CLIENTES CADASTRADOS======");
            clienteRepo.buscarTodos().forEach(c -> System.out.println(c.id() + " | " + c.nome() + " | " + c.email() + " | " + c.cpf() + " | " + c.dataCadastro() + " | " + c.tipoCliente()));

        }catch(SQLException e){
            System.out.println("Erro no banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
