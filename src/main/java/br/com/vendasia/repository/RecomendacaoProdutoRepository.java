package br.com.vendasia.repository;

import br.com.vendasia.model.RecomendacaoProduto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecomendacaoProdutoRepository {

    private final Connection conn;

    public RecomendacaoProdutoRepository(Connection conn) {
        this.conn = conn;
    }

    //Listar Recomendações de Produtos
    public List<RecomendacaoProduto> buscarRecomendacoes(int produtoBaseId) throws SQLException {
        List<RecomendacaoProduto> lista = new ArrayList<>();
        
        String sql = """
                SELECT produto_base_id, produto_recomendado_id, suporte, confianca,
                FROM recomendacoes_produto
                WHERE produto_base_id = ?
                ORDER BY lift DESC
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoBaseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    //Salvar Recomendações
    public void salvar(RecomendacaoProduto rec) throws SQLException {
        // Upsert - atualiza se ja existir ou insere se não existir
        String sql = """
                INSERT INTO recomendacoes_produto
                    (produto_base_id, produto_recomendado_id, suporte, conficanca, lift, data_calculo)
                VALUES (?, ?, ?, ?, ?, NOW())
                ON DUPLICATE KEY UPDATE
                    suporte = VALUES(suporte),
                    confianca = VALUES(confianca),
                    lift = VALUES(lift),
                    data_calculo = NOW()
                """;
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rec.produtoBaseId());
            stmt.setInt(2, rec.produtoRecomendadoId());
            stmt.setInt(3, rec.suporte());
            stmt.setBigDecimal(4, rec.confianca());
            stmt.setBigDecimal(5, rec.lift());
            stmt.executeUpdate();
        }

    }



    //Mapeamento
    private RecomendacaoProduto mapear(ResultSet rs) throws SQLException {
        return new RecomendacaoProduto(
            rs.getInt("paroduto_base_id"),
            rs.getInt("produto_recomendado_id"),
            rs.getInt("suporte"),
            rs.getBigDecimal("conficana"),
            rs.getBigDecimal("lift"),
            rs.getTimestamp("data_calculo").toLocalDateTime()
        );
    }

}




