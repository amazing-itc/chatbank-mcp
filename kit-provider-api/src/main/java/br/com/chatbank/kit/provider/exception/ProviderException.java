package br.com.chatbank.kit.provider.exception;

/**
 * Erro de negócio do adapter. A mensagem é segura para o modelo/usuário.
 */
public class ProviderException extends RuntimeException {

    public ProviderException(String message) {
        super(message);
    }
}
