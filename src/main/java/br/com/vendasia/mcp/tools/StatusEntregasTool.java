package br.com.vendasia.mcp.tools;
import br.com.vendasia.service.EntregaService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import java.util.List;
import java.util.Map;

public class StatusEntregasTool extends BaseTool {

    private final EntregaService entregaService;

    public StatusEntregasTool(EntregaService entregaService) {
        this.entregaService = entregaService;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("status_entregas",
                """
                Retorna entregas filtradas por status ou lista todas as entregas.
                Use quando o usuário perguntar sobre:
                - entregas pendentes, em trânsito ou entregues
                - quantas entregas estão atrasadas ou extraviadas
                - visão geral do status de entregas
                - entregas de um pedido específico
                Status disponíveis: Pendente, Em_separacao, Enviado, Em_transito,
                Entregue, Devolvido, Extraviado
                """,
                schema(
                    Map.of("status", Map.of(
                        "type", "string",
                        "description", "Status para filtrar: Pendente, Em_separacao, Enviado, Em_transito, Entregue, Devolvido, Extraviado. Omita para listar todas."
                    )),
                    List.of() // status é opcional
                )
            ),
            (exchange, request) -> {
                try {
                    var args = request.arguments();
                    List<String> linhas;
                    if (args.containsKey("status")) {
                        String status = (String) args.get("status");
                        linhas = entregaService.listarPorStatus(status).stream()
                            .map(e -> String.format(
                                "Pedido #%d | %s | Rastreio: %s | Previsão: %s",
                                e.pedidoId(),
                                e.status(),
                                e.codigoRastreio() != null ? e.codigoRastreio() : "sem rastreio",
                                e.dataPrevisaoEntrega() != null ? e.dataPrevisaoEntrega().toString() : "sem previsão"
                            ))
                            .toList();
                    } else {
                        linhas = entregaService.listarTodas().stream()
                            .map(e -> String.format(
                                "Pedido #%d | %s | %s | Previsão: %s",
                                e.pedidoId(),
                                e.status(),
                                e.transportadora() != null ? e.transportadora() : "sem transportadora",
                                e.dataPrevisaoEntrega() != null ? e.dataPrevisaoEntrega().toString() : "sem previsão"
                            ))
                            .toList();
                    }
                    return sucessoResult(linhas, "Nenhuma entrega encontrada.");
                } catch (Exception e) {
                    return erroResult("status_entregas", e);
                }
            }
        );
    }
}
