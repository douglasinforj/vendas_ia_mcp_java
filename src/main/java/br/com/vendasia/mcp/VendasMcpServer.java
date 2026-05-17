package br.com.vendasia.mcp;

import br.com.vendasia.mcp.tools.*;
import br.com.vendasia.service.RelatorioService;

// REMOVER imports de Jackson e JacksonMcpJsonMapper
//import tools.jackson.databind.ObjectMapper;                          // Jackson 3
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;   // Jackson 3
import tools.jackson.databind.json.JsonMapper;


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

            // Sem parâmetro — usa o mapper interno do SDK (Jackson 3 nativo)
            JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(JsonMapper.builder().build());
            StdioServerTransportProvider transport = new StdioServerTransportProvider(jsonMapper);

            log.info("Inicializando ferramentas de vendas...");

            TopProdutosTool topProdutos         = new TopProdutosTool(relatorio);
            ReceitaPorPeriodoTool receitaPeriodo = new ReceitaPorPeriodoTool(relatorio);
            EstoqueCriticoTool estoqueCritico    = new EstoqueCriticoTool(relatorio);
            PedidosPorStatusTool pedidosStatus   = new PedidosPorStatusTool(relatorio);
            TicketMedioTool ticketMedio          = new TicketMedioTool(relatorio);

            log.info("📋 Registrando 5 ferramentas no MCP Server...");

            return McpServer.sync(transport)
                .serverInfo("vendas-ia", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .build())
                .tools(
                    topProdutos.especificacao(),
                    receitaPeriodo.especificacao(),
                    estoqueCritico.especificacao(),
                    pedidosStatus.especificacao(),
                    ticketMedio.especificacao()
                )
                .build();

        } catch (Exception e) {
            log.error("Erro crítico ao construir o MCP Server", e);
            throw new RuntimeException("Falha ao iniciar servidor MCP", e);
        }
    }
}