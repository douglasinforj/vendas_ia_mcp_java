package br.com.vendasia.model;

import java.time.LocalDateTime;

public record RastreamentoLog(
    Integer id,
    Integer entregaId,
    String status,
    String localizacao,
    LocalDateTime dataHora,
    String descricao
) {}
