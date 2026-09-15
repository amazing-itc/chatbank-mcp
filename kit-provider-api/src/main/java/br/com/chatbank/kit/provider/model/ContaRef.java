package br.com.chatbank.kit.provider.model;

import java.util.Objects;

public record ContaRef(String id) {

    public ContaRef {
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id da conta é obrigatório");
        }
    }
}
