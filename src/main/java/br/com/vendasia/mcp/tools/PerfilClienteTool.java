package br.com.vendasia.mcp.tools;

import br.com.vendasia.service.ClienteService;
import br.com.vendasia.service.PedidoService;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerfilClienteTool extends BaseTool {

    private final ClienteService clienteService;
    private final PedidoService  pedidoService;

    public PerfilClienteTool(ClienteService clienteService, PedidoService pedidoService) {
        this.clienteService = clienteService;
        this.pedidoService  = pedidoService;
    }

    @Override
    public SyncToolSpecification especificacao() {
        return new SyncToolSpecification(
            tool("perfil_cliente",
                """
                Retorna dados cadastrais e histórico de pedidos de um cliente.
                Use quando o usuário perguntar sobre:
                - quem é determinado cliente
                - histórico de compras de um cliente
                - pedidos de um cliente específico
                - perfil completo de um cliente
                """,
                schema(
                    Map.of("cliente_id", Map.of(
                        "type", "integer",
                        "description", "ID do cliente para consultar o perfil e histórico de pedidos"
                    )),
                    List.of("cliente_id")
                )
            ),
            (exchange, request) -> {
                try {
                    int clienteId = getIntArgument(request.arguments(), "cliente_id", 0);
                    if (clienteId <= 0) throw new IllegalArgumentException("cliente_id inválido.");

                    var clienteOpt = clienteService.buscarPorId(clienteId);
                    if (clienteOpt.isEmpty()) {
                        return sucessoResult(List.of(), "Cliente não encontrado.");
                    }

                    var cliente = clienteOpt.get();
                    var pedidos = pedidoService.listarPorCliente(clienteId);

                    List<String> linhas = new ArrayList<>();

                    // Dados cadastrais
                    linhas.add("=== DADOS CADASTRAIS ===");
                    linhas.add(String.format("Nome:     %s", cliente.nome()));
                    linhas.add(String.format("Email:    %s", cliente.email()));
                    linhas.add(String.format("Tipo:     %s", cliente.tipoCliente()));
                    linhas.add(String.format("Cadastro: %s",
                        cliente.dataCadastro() != null ? cliente.dataCadastro().toString() : "não informado"
                    ));

                    // Histórico de pedidos
                    linhas.add(String.format("%n=== PEDIDOS (%d) ===", pedidos.size()));
                    if (pedidos.isEmpty()) {
                        linhas.add("Nenhum pedido encontrado.");
                    } else {
                        pedidos.forEach(p -> linhas.add(String.format(
                            "Pedido #%d | %s | R$ %.2f | %s",
                            p.id(),
                            p.status(),
                            p.valorTotal(),
                            p.dataPedido() != null ? p.dataPedido().toLocalDate().toString() : "sem data"
                        )));
                    }

                    return sucessoResult(linhas, "Sem dados.");
                } catch (Exception e) {
                    return erroResult("perfil_cliente", e);
                }
            }
        );
    }
}