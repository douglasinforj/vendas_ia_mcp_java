package br.com.vendasia;

import br.com.vendasia.repository.ClienteRepository;
import br.com.vendasia.repository.ProdutoRepository;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args){

        ClienteRepository clienteRepo = new ClienteRepository();
        ProdutoRepository produtoRepo = new ProdutoRepository();

        try{

            //CLIENTES
            System.out.println("======CLIENTES CADASTRADOS======");
            clienteRepo.buscarTodos().forEach(c -> System.out.println(c.id() + " | " + c.nome() + " | " + c.email() + " | " + c.cpf() + " | " + c.dataCadastro() + " | " + c.tipoCliente()));

            System.out.println("\n======CLIENTES COM PEDIDO=======");
            clienteRepo.buscarClientesComPedidos().forEach(System.out::println);

            //PRODUTOS
            System.out.println("\n======TOP 5 PRODUTOS MAIS VENDIDOS ======");
            produtoRepo.topProdutosMaisVendidos(5).forEach(System.out::println);

            System.out.println("\n=====ESTOQUE CRÍTICO (< 10 unidade)======");
            produtoRepo.buscarEstoqueCritico(10).forEach(p -> System.out.println(p.nome() + " -> " + p.estoqueAtual() + "un."));

 
        }catch(SQLException e){
            System.out.println("Erro no banco: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
