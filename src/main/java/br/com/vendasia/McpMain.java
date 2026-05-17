package br.com.vendasia;

import br.com.vendasia.mcp.VendasMcpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpMain {

    private static final Logger log = LoggerFactory.getLogger(McpMain.class);

    public static void main(String[] args) {
        log.info("========================================");
        log.info("Iniciando VendasIA MCP Server v1.0.0");
        log.info("========================================");

        try {
            VendasMcpServer app = new VendasMcpServer();
            McpSyncServer server = app.construir();

            log.info("Servidor MCP configurado com sucesso!");
            log.info("Aguardando conexões via STDIO (Claude Desktop, Cursor, etc.)...");

            // Graceful Shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Sinal de shutdown recebido. Encerrando servidor...");
                try {
                    server.closeGracefully();
                    log.info("Servidor encerrado com sucesso.");
                } catch (Exception e) {
                    log.warn("Erro durante o shutdown", e);
                }
            }));

            // Mantém o processo rodando (essencial para STDIO)
            Thread.currentThread().join();

        } catch (Exception e) {
            log.error("Erro fatal ao iniciar o MCP Server", e);
            System.exit(1);
        }
    }
}
