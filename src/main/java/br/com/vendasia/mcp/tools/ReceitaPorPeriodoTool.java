package br.com.vendasia.mcp.tools;

import br.com.vendasia.service.RelatorioService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;

import java.util.List;
import java.util.Map;

public class ReceitaPorPeriodoTool extends BaseTool {

    private final RelatorioService relatorio;

    public ReceitaPorPeriodoTool(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("receita_por_periodo",
                """
                Retorna a receita de vendas agrupada por dia em um período.
                Use quando o usuário perguntar sobre:
                - faturamento por período
                - receita mensal ou diária
                - desempenho financeiro
                As datas devem estar no formato YYYY-MM-DD.
                """,
                schema(
                    Map.of(
                        "dataInicio", Map.of(
                            "type", "string",
                            "description", "Data inicial no formato YYYY-MM-DD"
                        ),
                        "dataFim", Map.of(
                            "type", "string",
                            "description", "Data final no formato YYYY-MM-DD"
                        )
                    ),
                    List.of("dataInicio", "dataFim")
                )
            ),
            (exchange, request) -> {
                try {
                    String inicio = (String) request.arguments().get("dataInicio");
                    String fim    = (String) request.arguments().get("dataFim");
                    if (inicio == null || inicio.isBlank()) throw new IllegalArgumentException("dataInicio é obrigatória.");
                    if (fim == null || fim.isBlank())       throw new IllegalArgumentException("dataFim é obrigatória.");
                    return sucessoResult(relatorio.receitaPorPeriodo(inicio, fim), "Nenhuma receita encontrada no período.");
                } catch (Exception e) {
                    return erroResult("receita_por_periodo", e);
                }
            }
        );
    }
}