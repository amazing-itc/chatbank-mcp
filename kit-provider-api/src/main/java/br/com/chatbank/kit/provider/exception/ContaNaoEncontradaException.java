package br.com.chatbank.kit.provider.exception;

public class ContaNaoEncontradaException extends ProviderException {

    public ContaNaoEncontradaException(String contaId) {
        super("Conta não encontrada: " + contaId);
    }
}
