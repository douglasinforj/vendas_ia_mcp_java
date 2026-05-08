package br.com.vendasia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record Pedido(
    Integer id,
    Integer clienteId,
    LocalDateTime dataPedido,
    String status,
    BigDecimal valorTotal
) {}
