package br.com.vendasia.mcp.tools;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class BaseTool {

    protected static final Logger log = LoggerFactory.getLogger(BaseTool.class);

    public abstract SyncToolSpecification especificacao();

    /**
     * Constrói Tool com os 7 parâmetros obrigatórios da versão 1.1.0.
     * Assinatura real confirmada pelo compilador:
     * Tool(String name, String title, String description,
     *      JsonSchema inputSchema, Map meta, ToolAnnotations annotations, Map extra)
     */
    protected McpSchema.Tool tool(String name, String description, McpSchema.JsonSchema schema) {
        return new McpSchema.Tool(
            name,           // name
            name,           // title (usamos o mesmo nome)
            description,    // description
            schema,         // inputSchema
            null,           // meta
            null,           // annotations
            null            // extra
        );
    }

    /**
     * Schema vazio — tools sem parâmetros.
     */
    protected McpSchema.JsonSchema schemaVazio() {
        return new McpSchema.JsonSchema(
            "object",
            Map.of(),
            List.of(),
            false,
            Map.of(),
            Map.of()
        );
    }

    /**
     * Schema com propriedades.
     */
    protected McpSchema.JsonSchema schema(Map<String, Object> properties, List<String> required) {
        return new McpSchema.JsonSchema(
            "object",
            properties,
            required,
            false,
            Map.of(),
            Map.of()
        );
    }

    protected McpSchema.CallToolResult erroResult(String toolName, Exception e) {
        log.error("Erro na tool {}", toolName, e);
        return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(
                "Erro ao executar " + toolName + ": " + e.getMessage()
            )))
            .isError(true)
            .build();
    }

    protected McpSchema.CallToolResult sucessoResult(List<String> linhas, String msgVazia) {
        String texto = linhas == null || linhas.isEmpty() ? msgVazia : String.join("\n", linhas);
        return McpSchema.CallToolResult.builder()
            .content(List.of(new McpSchema.TextContent(texto)))
            .build();
    }

    protected int getIntArgument(Map<String, Object> arguments, String campo, int valorPadrao) {
        Object valor = arguments.getOrDefault(campo, valorPadrao);
        if (!(valor instanceof Number numero)) {
            throw new IllegalArgumentException("O campo '" + campo + "' deve ser numérico.");
        }
        return numero.intValue();
    }
}