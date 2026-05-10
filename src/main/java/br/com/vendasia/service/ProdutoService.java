package br.com.vendasia.service;

import br.com.vendasia.infra.ConexaoMySQL;
import br.com.vendasia.repository.ProdutoRepository;
import br.com.vendasia.model.Produto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class ProdutoService {

        // Listar produtos
        public List<Produto> listarTodos() throws SQLException {
            try (Connection conn = ConexaoMySQL.obter()) {
                return new ProdutoRepository(conn).buscarTodos();
            }
        }

        // Buscar produto por ID
        public Optional<Produto> buscarPorId(int id) throws SQLException {
            try (Connection conn = ConexaoMySQL.obter()) {
                return new ProdutoRepository(conn).buscarPorId(id);
            }
        }

        //Buscar Por Categoria
        public List<Produto> buscarPorCategoria(String categoria) throws SQLException {
            if (categoria == null || categoria.isBlank()) {
                throw new IllegalArgumentException("Categoria não pode ser vazia");
            } 
            try (Connection conn = ConexaoMySQL.obter()) {
                return new ProdutoRepository(conn).buscarPorCategoria(categoria);
            }

        // Cadastrar um produto
        public Produto cadastrar(Produto produto) throws SQLException {
            if (produto.nome() == null || produto.nome().isBlank())
                throw new IllegalArgumentException("Nome do produto é obrigatório.");
            if (produto.sku() == null || produto.sku().isBlank())
                throw new IllegalArgumentException("SKU é obrigatório.");
            if (produto.precoVenda() == null || produto.precoVenda().signum() <=0 )
                throw new IllegalArgumentException("Preço de venda de ser maior que zero.");

            try (Connection conn = ConexaoMySQL.obter()) {
                return new ProdutoRepository(conn).inserir(produto);
            }
        }

        // Tops mais vendidos
        public List<String> topMaisVendidos(int limite) throws SQLException {
            if (limite <= 0) throw new IllegalArgumentException("Limite dever ser maior que zero.");
            try (Connection conn = ConexaoMySQL.obter()) {
                return new ProdutoRepository(conn).topProdutosMaisVendidos(limite);
            }
        }

        // Listar produto com estoque minimo
        public List<Produto> estoqueCritico(int minimo) throws SQLException {
            try (Connection conn = ConexaoMySQL.obter()){
                return new ProdutoRepository(conn).buscarEstoqueCritico(minimo);
            }
        }

}
