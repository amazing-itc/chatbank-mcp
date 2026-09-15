package br.com.chatbank.kit.provider.model;

import java.util.Objects;

/**
 * Valor monetário em unidades menores (centavos) para evitar ponto flutuante.
 */
public record Money(long centavos, String moeda) {

    public Money {
        Objects.requireNonNull(moeda, "moeda");
        if (moeda.isBlank()) {
            throw new IllegalArgumentException("moeda é obrigatória");
        }
    }

    public static Money brl(long centavos) {
        return new Money(centavos, "BRL");
    }
}
