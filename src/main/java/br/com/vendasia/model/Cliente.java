package br.com.vendasia.model;

import java.time.LocalDateTime;

public record Cliente(
    Integer id,
    String nome,
    String email,
    String cpf,
    LocalDateTime dataCadastro,
    String tipoCliente
) {}
