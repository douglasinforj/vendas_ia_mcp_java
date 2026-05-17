package br.com.vendasia.mcp.tools;

import br.com.vendasia.service.RelatorioService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;

import java.util.List;
import java.util.Map;

public class TopProdutosTool extends BaseTool {

    private final RelatorioService relatorio;

    public TopProdutosTool(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("top_produtos",
                """
                Retorna os produtos mais vendidos em quantidade.
                Use quando o usuário perguntar sobre:
                - produtos mais vendidos
                - ranking de vendas
                - produtos populares
                - performance de produtos
                """,
                schema(
                    Map.of("limite", Map.of(
                        "type", "integer",
                        "description", "Quantidade máxima de produtos. Padrão: 5"
                    )),
                    List.of()  // limite é opcional
                )
            ),
            (exchange, request) -> {
                try {
                    int limite = getIntArgument(request.arguments(), "limite", 5);
                    if (limite <= 0) throw new IllegalArgumentException("Limite deve ser maior que zero.");
                    return sucessoResult(relatorio.topProdutos(limite), "Nenhum produto vendido encontrado.");
                } catch (Exception e) {
                    return erroResult("top_produtos", e);
                }
            }
        );
    }
}