package br.com.vendasia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Pagamento(
    Integer id,
    Integer pedidoId,
    String formaPagamento,
    Integer parcelas,
    BigDecimal valorPago,
    LocalDateTime dataPagamento
) {}
