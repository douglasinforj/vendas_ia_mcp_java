package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Cliente;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private Connection connection;

    private MockedStatic<ConexaoMySQL> conexaoMock;
    private ClienteService clienteService;

    @BeforeEach
    void setup() {
        conexaoMock = mockStatic(ConexaoMySQL.class);
        conexaoMock.when(ConexaoMySQL::obter).thenReturn(connection);
        clienteService = new ClienteService();
    }

    @AfterEach
    void teardown() {
        conexaoMock.close();
    }

    // ────────────────────────────────────────────────────────────────────
    // Esses 4 testes cobrem TUDO que o ClienteService decide por conta
    // própria. O resto é delegação — responsabilidade do repository.
    // ────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoNomeNulo() {
        Cliente cliente = new Cliente(null, null, "email@test.com",
            "12345678901", null, "PF");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> clienteService.cadastrar(cliente)
        );

        assertEquals("Nome do cliente é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void deveLancarExcecaoNomeVazio() {
        Cliente cliente = new Cliente(null, "   ", "email@test.com",
            "12345678901", null, "PF");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> clienteService.cadastrar(cliente)
        );

        assertEquals("Nome do cliente é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando e-mail não contiver @")
    void deveLancarExcecaoEmailSemArroba() {
        Cliente cliente = new Cliente(null, "Erick", "emailsemarroba",
            "12345678901", null, "PF");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> clienteService.cadastrar(cliente)
        );

        assertEquals("E-mail inválido", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando e-mail for nulo")
    void deveLancarExcecaoEmailNulo() {
        Cliente cliente = new Cliente(null, "Erick", null,
            "12345678901", null, "PF");

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> clienteService.cadastrar(cliente)
        );

        assertEquals("E-mail inválido", ex.getMessage());
    }
}