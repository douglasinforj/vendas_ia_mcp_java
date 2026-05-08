package br.com.vendasia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Pagamento(
    Integer id,
    Integer pedidoId,
    String fromaPagamento,
    Integer parcelas,
    BigDecimal valorPago,
    LocalDateTime dataPagamento
) {}
