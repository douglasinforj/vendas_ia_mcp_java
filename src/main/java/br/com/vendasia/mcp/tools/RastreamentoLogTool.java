package br.com.vendasia.mcp.tools;
import br.com.vendasia.service.RastreamentoLogService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import java.util.List;
import java.util.Map;


public class RastreamentoLogTool extends BaseTool {

    private final RastreamentoLogService rastreamentoService;

    public RastreamentoLogTool(RastreamentoLogService rastreamentoService) {
        this.rastreamentoService = rastreamentoService;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("historico_rastreamento",
                """
                Retorna o histórico completo de rastreamento de uma entrega.
                Use quando o usuário perguntar sobre:
                - onde está determinado pedido ou entrega
                - histórico de movimentação de uma entrega
                - última atualização de rastreio
                - status detalhado de uma entrega específica
                """,
                schema(
                    Map.of("entrega_id", Map.of(
                        "type", "integer",
                        "description", "ID da entrega para consultar o histórico"
                    )),
                    List.of("entrega_id")
                )
            ),
            (exchange, request) -> {
                try {
                    int entregaId = getIntArgument(request.arguments(), "entrega_id", 0);
                    if (entregaId <= 0) throw new IllegalArgumentException("entrega_id inválido.");
                    var logs = rastreamentoService.buscarHistorico(entregaId);
                    List<String> linhas = logs.stream()
                        .map(l -> String.format(
                            "[%s] %s | Local: %s | %s",
                            l.dataHora() != null ? l.dataHora().toString() : "sem data",
                            l.status(),
                            l.localizacao() != null ? l.localizacao() : "não informado",
                            l.descricao() != null ? l.descricao() : ""
                        ))
                        .toList();
                    return sucessoResult(linhas, "Nenhum registro de rastreamento encontrado para esta entrega.");
                } catch (Exception e) {
                    return erroResult("historico_rastreamento", e);
                }
            }
        );
    }
}
