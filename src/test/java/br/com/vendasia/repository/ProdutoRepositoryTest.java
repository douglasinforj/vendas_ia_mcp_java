package br.com.vendasia.repository;

import br.com.vendasia.model.Produto;

import br.com.vendasia.infra.ConexaoMySQL;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;

import org.junit.jupiter.api.*;



public class ProdutoRepositoryTest {

    private Connection connection;
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setup() throws SQLException {
        connection = ConexaoMySQL.obter();
        connection.setAutoCommit(false);
        produtoRepository = new ProdutoRepository(connection);
    }

    @AfterEach
    void teardown() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
        connection.close();
    }

    //---INSERIR------------

    @Test
    @DisplayName("Deve inserir produto e retornar com ID gerado")
    void deveInserirProduto() throws SQLException {
        Produto novo = new Produto(
            null,
            "Produto test JUnit",
            "SKU-TEST" + System.currentTimeMillis(),
            new BigDecimal("100.00"),
            new BigDecimal("200.00"),
            50,
            "Teste"
        );

        Produto inserido = produtoRepository.inserir(novo);

        assertNotNull(inserido.id());  //verifica se não é nulo
        assertTrue(inserido.id() > 0);  //Verifica se é verdadeiro
        assertEquals("Produto test JUnit", inserido.nome()); //valor esperado x recebido
    }
}
