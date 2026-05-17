package br.com.vendasia.mcp.tools;

import br.com.vendasia.service.RelatorioService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;

import java.util.List;
import java.util.Map;

public class EstoqueCriticoTool extends BaseTool {

    private final RelatorioService relatorio;

    public EstoqueCriticoTool(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("estoque_critico",
                """
                Retorna produtos com estoque abaixo do mínimo informado.
                Use quando o usuário perguntar sobre:
                - estoque baixo
                - produtos em falta
                - necessidade de reposição
                - alertas de estoque
                """,
                schema(
                    Map.of("minimoEstoque", Map.of(
                        "type", "integer",
                        "description", "Produtos com estoque abaixo deste valor serão listados. Padrão: 10"
                    )),
                    List.of()  // opcional
                )
            ),
            (exchange, request) -> {
                try {
                    int minimo = getIntArgument(request.arguments(), "minimoEstoque", 10);
                    if (minimo < 0) throw new IllegalArgumentException("minimoEstoque não pode ser negativo.");
                    return sucessoResult(relatorio.estoqueCritico(minimo), "Nenhum produto com estoque crítico.");
                } catch (Exception e) {
                    return erroResult("estoque_critico", e);
                }
            }
        );
    }
}