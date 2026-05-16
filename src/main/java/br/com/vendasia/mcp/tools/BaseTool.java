package br.com.vendasia.mcp.tools;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

import java.util.List;


public abstract class BaseTool {

    public abstract SyncToolSpecification especicacao();

    /**
     * Schema vazio - tools sem parâmetro
     */
    protected String schemaVazio() {
        return """
                {
                    "type": "object",
                    "properties": {},
                    "required": []
                }
                """;
    }
    /**
     * Formatar erro em resposta legível pro Claude
     */
    protected CallToolResult erroResult(String toolName, Exception e) {
        return McpSchema.CallToolResult.builder()
            .content(List.of(new TextContent(
                "Erro ao executar " + toolName + ": " + e.getMessage()
            )))
            .isError(true)
            .build();
    }

    /**
     * Resposta de sucesso com lista de linhas
     */
    protected CallToolResult sucessoResult(List<String> linhas, String msgVazia) {
        String texto = linhas.isEmpty() ? msgVazia : String.join("\n", linhas);
        return McpSchema.CallToolResult.builder()
            .content(List.of(new TextContent(texto)))
            .build();
    }


}
