package br.com.vendasia.repository;

//import br.com.vendasia.infra.ConexaoMySQL;
//import br.com.vendasia.infra.ConexaoMySQL;              //Fase 2: Deixa de criar a conexão
import br.com.vendasia.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    //Recebe Connection - não abre mais conexão própria
    //Permite que o Service compartilhe a mesma transação

    private final Connection conn;

    public ClienteRepository(Connection conn) {
        this.conn = conn;
    }

    //------------------------
    //Buscar todos os clientes
    //------------------------
    public List<Cliente> buscarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id, nome, email, cpf, data_cadastro, tipo_cliente FROM clientes";


        try (//Connection conn = ConexaoMySQL.obter();                        //Fase 2: deixa de abrir conexão própria
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {   //cursor

            while (rs.next()) {
                //Cliente c = mapearCliente(rs);                             //Fase 2: deixa de abrir conexão própria         
                //clientes.add(c);                                           //Fase 2: deixa de abrir conexão própria
                clientes.add(mapear(rs));
            }
        }
        return clientes;
    }

    //-------------------
    // Buscar po ID
    //-------------------
    public Optional<Cliente> buscarPorId(int id) throws SQLException {
        String sql = "select id, nome, email, cpf, data_cadastro, tipo_cliente from clientes where id = ?";


        try (//Connection conn = ConexaoMySQL.obter();                      //Fase 2: deixa de abrir conexão própria
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id); // 1 = posição do primeiro "?"
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    //return Optional.of(mapearCliente(rs));                 //Fase 2: deixa de abrir conexão própria
                    return Optional.of(mapear(rs));
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

        try(//Connection conn = ConexaoMySQL.obter();                                                //Fase 2: deixa de abrir conexão própria
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, cliente.nome());
                stmt.setString(2, cliente.email());
                stmt.setString(3, cliente.cpf());
                stmt.setString(4,cliente.tipoCliente());
                stmt.executeUpdate();

                try (ResultSet key = stmt.getGeneratedKeys()){
                    if (key.next()){
                        return new Cliente(
                            key.getInt(1),
                            cliente.nome(),
                            cliente.email(),
                            cliente.cpf(), 
                            null,
                            cliente.tipoCliente());
                    }
                }
                /*
                int linhasAfetadas = stmt.executeUpdate();                     //Fase 2: deixa de abrir conexão própria
                if (linhasAfetadas == 0) {
                    throw new SQLException("Falha ao inserir cliente - nenhuma linha afetada.");
                }*/
        }
        throw new SQLException("ID não retornado após inserção.");
    }


    //---------------------------------------------
    // Buscar Clientes com Pedidos (JOIN)
    //---------------------------------------------
    public List<String> buscarClientesComPedidos() throws SQLException {
        List<String> resultado = new ArrayList<>();

        String sql = """
                SELECT c.nome, COUNT(p.id) AS total_pedidos,
                       SUM(p.valor_total) AS valor_total
                FROM clientes c
                INNER JOIN pedidos p ON p.cliente_id = c.id
                WHERE p.status != 'Cancelado'
                GROUP BY c.id, c.nome
                ORDER BY valor_total DESC
                """;
        try(//Connection conn = ConexaoMySQL.obter();                             //Fase 2: deixa de abrir conexão própria
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){

            while (rs.next()){
                resultado.add(String.format("%-30s | Pedidos: %d | Total: R$ %.2f",
                        rs.getString("nome"),
                        rs.getInt("total_pedidos"),
                        rs.getBigDecimal("valor_total")));
            }
            /* 
            while (rs.next()) {
                String linha = String.format("%-30s | Pedidos: %d | Total: R$ %.2f",     //Fase 2: deixa de abrir conexão própria
                        rs.getString("nome"),
                        rs.getInt("total_pedidos"),
                        rs.getBigDecimal("valor_total"));
                resultado.add(linha);
            } */
        }
        return resultado;
    }



    // Método Privado: mapeia ResultSet -> Record                       
    private Cliente mapear(ResultSet rs) throws SQLException{                //Fase 2: Mapeando direto
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
