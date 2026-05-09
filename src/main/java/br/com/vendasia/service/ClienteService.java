package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Cliente;
import br.com.vendasia.repository.ClienteRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClienteService {

    //Service abre a conexão e passa para o repository
    // Operação simples não precisam de transação explícita


    // Listar Clientes
    public List<Cliente> listarTodos() throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {                //Abre a conexão
            return new ClienteRepository(conn).buscarTodos();         // Enjeta conexão no construtor de ClienteRepository
        }                                                             // Retornando buscaTodos com os dados de Model
    }

    // Buscar por ID
    public Optional<Cliente> buscarPorId(int id) throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new ClienteRepository(conn).buscarPorId(id);
        }
    }
}