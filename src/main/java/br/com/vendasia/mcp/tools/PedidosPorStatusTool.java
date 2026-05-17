package br.com.vendasia.mcp.tools;

import br.com.vendasia.service.RelatorioService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;

public class PedidosPorStatusTool extends BaseTool {

    private final RelatorioService relatorio;

    public PedidosPorStatusTool(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("pedidos_por_status",
                """
                Retorna quantidade e valor total de pedidos agrupados por status.
                Use quando o usuário perguntar sobre:
                - pedidos pendentes, pagos ou cancelados
                - situação geral dos pedidos
                Não requer parâmetros.
                """,
                schemaVazio()
            ),
            (exchange, request) -> {
                try {
                    return sucessoResult(relatorio.pedidosPorStatus(), "Nenhum pedido encontrado.");
                } catch (Exception e) {
                    return erroResult("pedidos_por_status", e);
                }
            }
        );
    }
}