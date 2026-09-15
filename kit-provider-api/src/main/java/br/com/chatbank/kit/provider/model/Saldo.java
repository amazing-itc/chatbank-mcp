package br.com.chatbank.kit.provider.model;

import java.util.Objects;

public record Saldo(String contaId, String titular, Money disponivel) {

    public Saldo {
        Objects.requireNonNull(contaId, "contaId");
        Objects.requireNonNull(titular, "titular");
        Objects.requireNonNull(disponivel, "disponivel");
    }
}
