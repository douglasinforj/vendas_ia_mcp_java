package br.com.vendasia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Entrega(
    Integer id,
    Integer pedidoId,
    String codigoRastreio,
    String transportadora,
    LocalDateTime dataEnvio,
    LocalDateTime dataEntrega,
    String status,
    LocalDate dataPrevisaoEntrega,
    String observacoes,
    LocalDateTime ultimaAtualizacao,
    String atualizadoPor
) {}
