package br.com.vendasia.mcp.tools;
import br.com.vendasia.service.PagamentoService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import java.util.List;
import java.util.Map;

public class ResumoPagamentosTool extends BaseTool {

    private final PagamentoService pagamentoService;

    public ResumoPagamentosTool(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("resumo_pagamentos",
                """
                Retorna todos os pagamentos registrados com forma, parcelas e valor.
                Use quando o usuário perguntar sobre:
                - pagamentos realizados
                - formas de pagamento mais usadas
                - pagamentos de um pedido específico
                - volume financeiro de pagamentos
                """,
                schema(
                    Map.of("pedido_id", Map.of(
                        "type", "integer",
                        "description", "ID do pedido para filtrar pagamentos (opcional — omita para listar todos)"
                    )),
                    List.of() // pedido_id é opcional
                )
            ),
            (exchange, request) -> {
                try {
                    var args = request.arguments();
                    List<String> linhas;
                    if (args.containsKey("pedido_id")) {
                        int pedidoId = getIntArgument(args, "pedido_id", 0);
                        if (pedidoId <= 0) throw new IllegalArgumentException("pedido_id inválido.");
                        linhas = pagamentoService.listarPorPedido(pedidoId).stream()
                            .map(p -> String.format(
                                "Pedido #%d | %s | %dx | R$ %.2f | %s",
                                p.pedidoId(),
                                p.formaPagamento(),
                                p.parcelas(),
                                p.valorPago(),
                                p.dataPagamento() != null ? p.dataPagamento().toString() : "sem data"
                            ))
                            .toList();
                    } else {
                        linhas = pagamentoService.listarTodos().stream()
                            .map(p -> String.format(
                                "Pedido #%d | %s | %dx | R$ %.2f | %s",
                                p.pedidoId(),
                                p.formaPagamento(),
                                p.parcelas(),
                                p.valorPago(),
                                p.dataPagamento() != null ? p.dataPagamento().toString() : "sem data"
                            ))
                            .toList();
                    }
                    return sucessoResult(linhas, "Nenhum pagamento encontrado.");
                } catch (Exception e) {
                    return erroResult("resumo_pagamentos", e);
                }
            }
        );
    }
}
