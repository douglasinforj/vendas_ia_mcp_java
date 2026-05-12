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




