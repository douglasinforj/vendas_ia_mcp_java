package br.com.vendasia.repository;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Cliente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração - conecta no banco real
 * Cada teste abre uma transação, executa, verifica e faz rollback.
 * O banco volta ao estado original aós cada teste.
 */

public class ClienteRepositoryTest {

    private Connection connection;
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() throws SQLException {
        connection = ConexaoMySQL.obter();
        connection.setAutoCommit(false);   //Inicia transação
        clienteRepository = new ClienteRepository(connection);
    }

    @AfterEach
    void teardown() throws SQLException {
        connection.rollback();          // desfaz tudo que o teste fez
        connection.setAutoCommit(false);
        connection.close();
    }

    //---INSERIR
    @Test
    @DisplayName("Deve inserir cliente e retornar com ID gerado")
    void deveInserirCliente() throws SQLException {
        Cliente novo = new Cliente(
            null, 
            "Teste JUnit",
            "junit_" + System.currentTimeMillis() + "@test.com",
            "00000000001",
            null,
            "PF"
        );

        Cliente inserido = clienteRepository.inserir(novo);

        assertNotNull(inserido.id());
        assertTrue(inserido.id() > 0);
        assertEquals("Teste JUnit", inserido.nome());
    }

    @Test
    @DisplayName("Deve inserir cliente PJ com Suscesso")
    void deveInserirClientePJ() throws SQLException {
        Cliente pj = new Cliente(
            null,
            "Empresa teste LTDA",
            "pj_" + System.currentTimeMillis() + "@empresa.com",
            "12345678000199", 
            null, "PJ"
        );

        Cliente inserido = clienteRepository.inserir(pj);

        assertNotNull(inserido.id());
        assertEquals("PJ", inserido.tipoCliente());
    }

    //---BUSCAR POR ID----

    @Test
    @DisplayName("Deve buscar cliente pelo ID após inserção")
    void deveBuscarClientePorId() throws SQLException {
        // Insere primeira para ter um ID real
        Cliente novo = new Cliente(
            null,
            "Busca por ID",
            "buscaid_" + System.currentTimeMillis() + "@test.com",
            "00000000002",
            null,
            "PF"
        );

        Cliente inserido = clienteRepository.inserir(novo);

        //Busca pelo ID gerado
        Optional<Cliente> encontrado = clienteRepository.buscarPorId(inserido.id());

        assertTrue(encontrado.isPresent());
        assertEquals("Busca por ID", encontrado.get().nome());
        assertEquals(inserido.id(), encontrado.get().id());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void deveRetornarVazioIdInexistente() throws SQLException {
        Optional<Cliente> resultado = clienteRepository.buscarPorId(999999);

        assertFalse(resultado.isPresent());

    }

    //---BUSCAR TODOS---------

    @Test
    @DisplayName("Deve retornar lista não nula de Clientes")
    void deveRetornarListaNaoNula() throws SQLException {
        List<Cliente> clientes = clienteRepository.buscarTodos();

        assertNotNull(clientes);

    }

    @Test
    @DisplayName("Deve incluir cliente recém inserido na listagem")
    void deveIncluirClienteInseridoNaListagem() throws SQLException {
        String emailUnico = "lista_" + System.currentTimeMillis() + "@test.com";

        Cliente novo = new Cliente(
            null,
            "Cliente Listagem",
            emailUnico,
            "00000000003",
            null,
            "PF"
        );

        //Inserir para Buscar
        clienteRepository.inserir(novo);

        List<Cliente> clientes = clienteRepository.buscarTodos();

        boolean encontrado = clientes.stream()
            .anyMatch(c -> c.email().equals(emailUnico));
        
            assertTrue(encontrado, "Cliente inserido deve aparecer na listagem.");

    }


    //---MAPEAMENTO DO RESULTESET---------------------------

    @Test
    @DisplayName("Deve mapear todos os campos do cliente corretamente")
    void deveMapperCamposCorretamente() throws SQLException {
        String emailUnico =  "map_" + System.currentTimeMillis() + "@test.com";

        Cliente novo = new Cliente(
            null,
            "Mapeamento Teste",
            emailUnico,
             "00000000004",
             null,
             "PJ"
        );

        Cliente inserido = clienteRepository.inserir(novo);
        Optional<Cliente> encontrado = clienteRepository.buscarPorId(inserido.id());

        assertTrue(encontrado.isPresent());

        Cliente c = encontrado.get();
        assertEquals("Mapeamento Teste", c.nome());
        assertEquals(emailUnico, c.email());
        assertEquals("00000000004", c.cpf());
        assertEquals("PJ", c.tipoCliente());
        assertNotNull(c.dataCadastro());   //preenchido pelo banco via DEFAULT
    }

    //----EMAIL ÚNICO--------------

    @Test
    @DisplayName("Deve lançar exceção ao inserir e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() throws SQLException {
        String emailFixo = "duplicado_" + System.currentTimeMillis() + "@test.com";

        Cliente primeiro = new Cliente(
            null,
            "Primeiro",
            emailFixo,
            "00000000005",
            null, 
            "PF"
        );

        Cliente segundo = new Cliente(
            null,
            "Primeiro",
            emailFixo,
            "00000000006",
            null,
            "PF"
        );

        clienteRepository.inserir(primeiro);

        /**O banco tem UNIQUE no email - deve lançar SQLEXception
         * Inserirndo o segundo deve ser lançado exceção.
         */
        assertThrows(SQLException.class,
            () -> clienteRepository.inserir(segundo)
        );
    }

}
