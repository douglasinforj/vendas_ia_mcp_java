package br.com.vendasia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecomendacaoProduto(
    Integer produtoBaseId,
    Integer produtoRecomendadoId,
    Integer suporte,
    BigDecimal confianca,
    BigDecimal lift,
    LocalDateTime dataCalculo
) {}
