package br.com.vendasia.service;

import java.sql.Connection;
import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Produto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private Connection connection;

    private MockedStatic<ConexaoMySQL> conexaoMock;

    private ProdutoService produtoService;

    @BeforeEach
    void setup() {
        conexaoMock = mockStatic(ConexaoMySQL.class);
        conexaoMock.when(ConexaoMySQL::obter).thenReturn(connection);
        produtoService = new ProdutoService();
    }

    @AfterEach
    void teardown() {
        conexaoMock.close();
    }

    //----VALIDAÇÃO DO CADASTRAR-----

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoNomenulo() {
        Produto produto = new Produto(
            null,
            null,
            "SKU-001",
            new BigDecimal("2500"),
            new BigDecimal("3500"),
            10,
            "Eletrônicos"
        );

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.cadastrar(produto)
        );

        assertEquals("Nome do produto é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void deveLancarExcecaoNomeVazio() {
        Produto produto = new Produto(
            null, 
            "   ",
            "SKU-001",
            new BigDecimal("2500"),
            new BigDecimal("3500"),
            10,
            "Eletrônicos"
        );

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
             () -> produtoService.cadastrar(produto)
        );

        assertEquals("Nome do produto é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU for vazio")
    void deveLancarExcecaoSkuVazio() {
        Produto produto = new Produto(null, 
            "Notebook", 
            "   ",
            new BigDecimal("2500"), 
            new BigDecimal("3500"), 
            10, 
            "Eletrônicos");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.cadastrar(produto)
        );

        assertEquals("SKU é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço de venda for nulo")
    void deveLancarExcecaoPrecoNulo() {
        Produto produto = new Produto(
            null, 
            "Notebook", 
            "SKU-001",
            new BigDecimal("2500"),
            null, 
            10, 
            "Eletrônicos");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.cadastrar(produto)
        );

        assertEquals("Preço de venda deve ser maior que zero.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço de venda for zero")
    void deveLancarExcecaoPrecoZero() {
        Produto produto = new Produto(
            null, 
            "Notebook",
            "SKU-001",
            new BigDecimal("2500"),
            BigDecimal.ZERO,
            10,
            "Eletrônicos"
        );

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> produtoService.cadastrar(produto)
        );

        assertEquals("Preço de venda deve ser maior que zero.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço de venda for negativo")
    void deveLancarExcecaoPrecoNegativo() {
        Produto produto = new Produto(
            null,
            "Notebook",
            "SKU-001",
            new BigDecimal("2500"),
            new BigDecimal("-1"),
            10,
            "Eletrônico"
        );

        assertThrows(IllegalArgumentException.class,
            () -> produtoService.cadastrar(produto)
        );
    }



}
