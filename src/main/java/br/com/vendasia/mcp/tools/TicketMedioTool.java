package br.com.vendasia.mcp.tools;

import br.com.vendasia.service.RelatorioService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;

public class TicketMedioTool extends BaseTool {

    private final RelatorioService relatorio;

    public TicketMedioTool(RelatorioService relatorio) {
        this.relatorio = relatorio;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("ticket_medio_por_cliente",
                """
                Retorna os top 10 clientes com maior ticket médio de compra.
                Use quando o usuário perguntar sobre:
                - melhores clientes
                - clientes que mais gastam
                - ticket médio por cliente
                - clientes premium
                Não requer parâmetros.
                """,
                schemaVazio()
            ),
            (exchange, request) -> {
                try {
                    return sucessoResult(relatorio.ticketMedioPorCliente(), "Nenhum dado de clientes encontrado.");
                } catch (Exception e) {
                    return erroResult("ticket_medio_por_cliente", e);
                }
            }
        );
    }
}