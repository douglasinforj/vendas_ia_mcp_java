package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.model.RecomendacaoProduto;
import br.com.vendasia.repository.RecomendacaoProdutoRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class RecomendacaoProdutoService {

    public List<RecomendacaoProduto> buscarRecomendacoes(int produtoBaseId) throws SQLException {
        try (Connection conn = ConexaoMySQL.obter()) {
            return new RecomendacaoProdutoRepository(conn).buscarRecomendacoes(produtoBaseId);
        }
    }

    public void salvar(RecomendacaoProduto recomendacao) throws SQLException {
        if (recomendacao.produtoBaseId().equals(recomendacao.produtoRecomendadoId()))
            throw new IllegalArgumentException("Produto base e recomendado não podem ser iguais.");

        try (Connection conn = ConexaoMySQL.obter()) {
            new RecomendacaoProdutoRepository(conn).salvar(recomendacao);
        }
    }   
}


