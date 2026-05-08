package br.com.vendasia.repository;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    //Buscar todos os clientes
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
