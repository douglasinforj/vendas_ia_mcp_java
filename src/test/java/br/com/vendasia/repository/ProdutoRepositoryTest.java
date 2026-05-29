package br.com.vendasia.repository;

import br.com.vendasia.model.Produto;

import br.com.vendasia.infra.ConexaoMySQL;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("Deve inserir produto sem preço de custo")
    void deveInserirProdutoSemPrecoCusto() throws SQLException {
        Produto novo = new Produto(
            null,
            "Produto Sem Custo",
            "SKU-SC" + System.currentTimeMillis(),
            null,
            new BigDecimal("150.00"),
            10,
            "Teste");

        Produto inserido = produtoRepository.inserir(novo);

        assertNotNull(inserido.id());
        assertNull(inserido.precoCusto());
    }

    @Test
    @DisplayName("Deve lançar exceção ao inserir SKU duplicado")
    void deveLancarExcecaoSkuDuplicado() throws SQLException {

        String skuFixo = "SKU-DUP-" + System.currentTimeMillis();

        Produto primeiro = new Produto(
            null, 
            "Primeiro Produto",
            skuFixo,
            new BigDecimal("150"),
            new BigDecimal("250"),
            8,
            "Teste"
        );

        Produto segundo = new Produto(
            null,
            "Segundo Produto",
            skuFixo, 
            new BigDecimal("150"), 
            new BigDecimal("250"),
            8,
            "Teste"
        );

        produtoRepository.inserir(primeiro);

        assertThrows(SQLException.class,
            () -> produtoRepository.inserir(segundo)
        );
    }


    // --- BUSCAR POR ID---------

    @Test
    @DisplayName("Deve buscar produto pelo ID após inserção")
    void deveBuscarProdutoPorId() throws SQLException {
        Produto novo = new Produto(
            null,
            "Buscar ID",
            "SKU-BID-" + System.currentTimeMillis(),
            new BigDecimal("80"),
            new BigDecimal("160"),
            20, "Teste"
        );

        Produto inserido = produtoRepository.inserir(novo);
        Optional<Produto> encontrado = produtoRepository.buscarPorId(inserido.id());

        assertTrue(encontrado.isPresent());
        assertEquals("Buscar ID", encontrado.get().nome());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void deveRetornarVazioInexistente() throws SQLException {
        Optional<Produto> resultado = produtoRepository.buscarPorId(999999);

        assertFalse(resultado.isPresent());
    }

    //---BUSCAR POR CATEGORIA----------

    @Test
    @DisplayName("Deve retornar produtos da categoria informada")
    void deveRetornarProdutosPorCategoria() throws SQLException {

        String categoriaUnica = "CatTeste-" + System.currentTimeMillis();

        Produto novo = new Produto(
            null,
            "Produto Categoria",
            "SKU-CAT-" + System.currentTimeMillis(),
            new BigDecimal("50"),
            new BigDecimal("100"),
            5,
            categoriaUnica
        );

        produtoRepository.inserir(novo);

        List<Produto> resultado = produtoRepository.buscarPorCategoria(categoriaUnica);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream()
            .allMatch(p -> p.categoria().equals(categoriaUnica)));

    }

    @Test
    @DisplayName("Deve retornar lista vazia para categoria inexistente")
    void deveRetornarVazioCategoriaNaoExistente() throws SQLException {
        List<Produto> resultado = produtoRepository.buscarPorCategoria("CategoriaQueNaoExiste999");

        assertTrue(resultado.isEmpty());
    }

    //---ATUALIZAR ESTOQUE

    @Test
    @DisplayName("Deve atualizar estoque do produto corretamente")
    void deveAtualizarEstoque() throws SQLException {
        Produto novo = new Produto(
            null,
            "Produto Estoque",
            "SKU-EST-" + System.currentTimeMillis(),
            new BigDecimal("50"),
            new BigDecimal("100"),
            10,
            "Teste"
        );

        Produto inserido = produtoRepository.inserir(novo);

        produtoRepository.atualizarEstoque(inserido.id(), 25);

        Optional<Produto> atualizado = produtoRepository.buscarPorId(inserido.id());

        assertTrue(atualizado.isPresent());
        assertEquals(25, atualizado.get().estoqueAtual());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estoque de ID inexistente")
    void devoLancarExcecaoEstoqueIdInexistente() {
        assertThrows(SQLException.class,
            () -> produtoRepository.atualizarEstoque(999999, 10)
        );
    }


    //---ESTOQUE CRÍTICO----------------

    @Test
    @DisplayName("Deve incluir produto com estoque abaixo do mínimo.")
    void deveIncluirProdutoComEstoqueCritico() throws SQLException {
        Produto critico = new Produto(
            null,
            "Produto Crítico",
            "SKU-CRT-" + System.currentTimeMillis(),
            new BigDecimal("50"),
            new BigDecimal("100"),
            2,
            "Teste"
        );

        produtoRepository.inserir(critico);

        List<Produto> resultado = produtoRepository.buscarEstoqueCritico(5);

        boolean encontrado = resultado.stream()
            .anyMatch(p -> p.estoqueAtual() <= 5);
        
            assertTrue(encontrado);
    }
    



}
