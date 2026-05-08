package br.com.vendasia.model;

import java.math.BigDecimal;

public record ItemPedido(
    Integer pedidoId,
    Integer produtoId,
    Integer quantidade,
    BigDecimal precoUnitario

) {}
