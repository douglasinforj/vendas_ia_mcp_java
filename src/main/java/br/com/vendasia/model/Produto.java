package br.com.vendasia.model;

import java.math.BigDecimal;

public record Produto(
    Integer id,
    String nome,
    String sku,
    BigDecimal precoCusto,
    BigDecimal precoVenda,
    Integer estoqueAtual,
    String categoria
) {}
