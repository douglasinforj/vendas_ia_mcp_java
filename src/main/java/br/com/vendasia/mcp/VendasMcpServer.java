package br.com.vendasia.mcp;

import br.com.vendasia.mcp.tools.*;           // ajuste o pacote se necessário
import br.com.vendasia.service.RelatorioService;

import com.fasterxml.jackson.databind.ObjectMapper;

// Import corrigido:
import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendasMcpServer {
    private static final Logger log = LoggerFactory.getLogger(VendasMcpServer.class);

    private final RelatorioService relatorio;

    public VendasMcpServer() {
        this.relatorio = new RelatorioService();
    }

    public McpSyncServer construir() {
        try {
            log.info("Configurando transporte STDIO...");

            // Correção aqui
            JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(new ObjectMapper());

            StdioServerTransportProvider transport = 
                new StdioServerTransportProvider(jsonMapper);

            log.info("Inicializando tools...");
            TopProdutosTool topProdutos = new TopProdutosTool(relatorio);
            ReceitaPorPeriodoTool receitaPorPeriodo = new ReceitaPorPeriodoTool(relatorio);
            EstoqueCriticoTool estoqueCritico = new EstoqueCriticoTool(relatorio);
            PedidosPorStatusTool pedidosPorStatus = new PedidosPorStatusTool(relatorio);
            TicketMedioTool ticketMedio = new TicketMedioTool(relatorio);

            log.info("Registrando MCP Server...");
            return McpServer.sync(transport)
                .serverInfo("vendas-ia", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .build())
                .tools(
                    topProdutos.especificacao(),
                    receitaPorPeriodo.especificacao(),
                    estoqueCritico.especificacao(),
                    pedidosPorStatus.especificacao(),
                    ticketMedio.especificacao()
                )
                .build();

        } catch (Exception e) {
            log.error("Erro ao construir MCP Server.", e);
            throw new RuntimeException("Falha ao iniciar servidor MCP.", e);
        }
    }
}