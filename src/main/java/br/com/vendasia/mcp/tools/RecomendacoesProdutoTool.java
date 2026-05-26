package br.com.vendasia.mcp.tools;
import br.com.vendasia.service.RecomendacaoProdutoService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import java.util.List;
import java.util.Map;

public class RecomendacoesProdutoTool extends BaseTool {

    private final RecomendacaoProdutoService recomendacaoService;

    public RecomendacoesProdutoTool(RecomendacaoProdutoService recomendacaoService) {
        this.recomendacaoService = recomendacaoService;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("recomendacoes_produto",
                """
                Retorna produtos recomendados com base em um produto base.
                Usa métricas de market basket analysis: suporte, confiança e lift.
                Use quando o usuário perguntar sobre:
                - o que recomendar para clientes que compraram X
                - produtos frequentemente comprados juntos
                - cross-sell e upsell
                - recomendações inteligentes de produtos
                """,
                schema(
                    Map.of("produto_base_id", Map.of(
                        "type", "integer",
                        "description", "ID do produto base para buscar recomendações"
                    )),
                    List.of("produto_base_id")
                )
            ),
            (exchange, request) -> {
                try {
                    int produtoId = getIntArgument(request.arguments(), "produto_base_id", 0);
                    if (produtoId <= 0) throw new IllegalArgumentException("produto_base_id inválido.");
                    var recomendacoes = recomendacaoService.buscarRecomendacoes(produtoId);
                    if (recomendacoes.isEmpty()) {
                        return sucessoResult(List.of(), "Nenhuma recomendação encontrada para este produto.");
                    }
                    List<String> linhas = recomendacoes.stream()
                        .map(r -> String.format(
                            "Produto recomendado ID: %d | Suporte: %d | Confiança: %.2f%% | Lift: %.2f",
                            r.produtoRecomendadoId(),
                            r.suporte(),
                            r.confianca().doubleValue() * 100,
                            r.lift().doubleValue()
                        ))
                        .toList();
                    return sucessoResult(linhas, "Sem recomendações.");
                } catch (Exception e) {
                    return erroResult("recomendacoes_produto", e);
                }
            }
        );
    }
}
