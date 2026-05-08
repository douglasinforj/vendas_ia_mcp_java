package br.com.vendasia.repository;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    //------------------------
    //Buscar todos os clientes
    //------------------------
    public List<Cliente> buscarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id, nome, email, cpf, data_cadastro, tipo_cliente FROM clientes";

        // try-with-resources: fecha Connection e Statement automaticamente
        // mesmo se lançar exceção. Equivale a finally { conn.close(); }
        try (Connection conn = ConexaoMySQL.obter();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {   //cursor

            // ResultSet é um cursor — começa ANTES da primeira linha
            // rs.next() avança o cursor e retorna false quando acaba
            while (rs.next()) {
                Cliente c = mapearCliente(rs);
                clientes.add(c);
            }
        }
        return clientes;
    }

    //-------------------
    // Buscar po ID
    //-------------------
    public Optional<Cliente> buscarPorId(int id) throws SQLException {
        String sql = "select id, nome, email, cpf, data_cadastro, tipo_cliente from clientes where id = ?";

        // O "?" é o parâmetro. PreparedStatement evita SQL Injection.
        // Nunca faça: "WHERE id = " + id  ← vulnerável!
        try (Connection conn = ConexaoMySQL.obter();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id); // 1 = posição do primeiro "?"
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCliente(rs));  // 'of' cria uma objeto com valor obrigatorio
                }
            }
        }
        return Optional.empty(); // Nunca retorne null — use Optional
    }

    //-------------------
    // Inserir Cliente
    //-------------------
    public Cliente inserir(Cliente cliente) throws SQLException {
        String sql = "insert into clientes (nome, email, cpf, tipo_cliente) value (?,?,?,?)";

        //RETURN_GENERATED_KEYS: recupera o id gerado pelo AUTO_INCREMENT
        try(Connection conn = ConexaoMySQL.obter();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, cliente.nome());
                stmt.setString(2, cliente.email());
                stmt.setString(3, cliente.cpf());
                stmt.setString(4,cliente.tipoCliente());

                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao inserir cliente - nenhuma linha afetada.");
                }
        }
        throw new SQLException("ID não retornado após inserção.");
    }




    // Método Privado: mapeia ResultSet -> Record
    private Cliente mapearCliente(ResultSet rs) throws SQLException{
        return new Cliente(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("email"),
            rs.getString("cpf"),
            rs.getTimestamp("data_Cadastro").toLocalDateTime(),
            rs.getString("tipo_cliente")
        );
    }
}
