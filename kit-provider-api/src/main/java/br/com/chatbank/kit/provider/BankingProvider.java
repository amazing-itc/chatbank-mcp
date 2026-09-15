package br.com.chatbank.kit.provider;

import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.FiltroExtrato;
import br.com.chatbank.kit.provider.model.Lancamento;
import br.com.chatbank.kit.provider.model.Pagina;
import br.com.chatbank.kit.provider.model.Saldo;

/**
 * Contrato que o banco implementa. A camada MCP só fala com esta interface.
 */
public interface BankingProvider {

    Saldo consultarSaldo(ContaRef conta);

    Pagina<Lancamento> consultarExtrato(ContaRef conta, FiltroExtrato filtro);
}
