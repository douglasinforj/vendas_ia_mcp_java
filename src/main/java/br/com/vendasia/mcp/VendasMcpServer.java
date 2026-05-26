package br.com.vendasia.mcp;

import br.com.vendasia.mcp.tools.*;
import br.com.vendasia.service.*;

import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import tools.jackson.databind.json.JsonMapper;

import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendasMcpServer {

    private static final Logger log = LoggerFactory.getLogger(VendasMcpServer.class);

    // ── Services ─────────────────────────────────────────────────────────────
    private final RelatorioService           relatorio;
    private final RecomendacaoProdutoService recomendacao;
    private final RastreamentoLogService     rastreamento;
    private final PagamentoService           pagamento;
    private final EntregaService             entrega;
    private final ClienteService             cliente;
    private final PedidoService              pedido;

    public VendasMcpServer() {
        this.relatorio    = new RelatorioService();
        this.recomendacao = new RecomendacaoProdutoService();
        this.rastreamento = new RastreamentoLogService();
        this.pagamento    = new PagamentoService();
        this.entrega      = new EntregaService();
        this.cliente      = new ClienteService();
        this.pedido       = new PedidoService();
    }

    public McpSyncServer construir() {
        try {
            log.info("Configurando transporte STDIO...");

            // Sem parâmetro — usa o mapper interno do SDK (Jackson 3 nativo)
            JacksonMcpJsonMapper jsonMapper = new JacksonMcpJsonMapper(JsonMapper.builder().build());
            StdioServerTransportProvider transport = new StdioServerTransportProvider(jsonMapper);

            log.info("Inicializando ferramentas de vendas...");

            // ── Tools analíticas ──────────────────────────────────────────────
            TopProdutosTool       topProdutos    = new TopProdutosTool(relatorio);
            ReceitaPorPeriodoTool receitaPeriodo = new ReceitaPorPeriodoTool(relatorio);
            EstoqueCriticoTool    estoqueCritico = new EstoqueCriticoTool(relatorio);
            PedidosPorStatusTool  pedidosStatus  = new PedidosPorStatusTool(relatorio);
            TicketMedioTool       ticketMedio    = new TicketMedioTool(relatorio);

            // ── Tools de decisão de negócio ───────────────────────────────────
            RecomendacoesProdutoTool recomendacoesTool  = new RecomendacoesProdutoTool(recomendacao);
            RastreamentoLogTool      rastreamentoTool   = new RastreamentoLogTool(rastreamento);
            ResumoPagamentosTool     pagamentosTool     = new ResumoPagamentosTool(pagamento);
            StatusEntregasTool       entregasTool       = new StatusEntregasTool(entrega);
            PerfilClienteTool        perfilClienteTool  = new PerfilClienteTool(cliente, pedido);

            log.info("Registrando 10 ferramentas no MCP Server...");

            return McpServer.sync(transport)
                .serverInfo("vendas-ia", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .build())
                .tools(
                    // Analíticas
                    topProdutos.especificacao(),
                    receitaPeriodo.especificacao(),
                    estoqueCritico.especificacao(),
                    pedidosStatus.especificacao(),
                    ticketMedio.especificacao(),

                    // Decisão de negócio
                    recomendacoesTool.especificacao(),
                    rastreamentoTool.especificacao(),
                    pagamentosTool.especificacao(),
                    entregasTool.especificacao(),
                    perfilClienteTool.especificacao()
                )
                .build();

        } catch (Exception e) {
            log.error("Erro crítico ao construir o MCP Server", e);
            throw new RuntimeException("Falha ao iniciar servidor MCP", e);
        }
    }
}