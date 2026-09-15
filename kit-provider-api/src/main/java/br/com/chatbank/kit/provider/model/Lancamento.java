package br.com.chatbank.kit.provider.model;

import java.time.LocalDate;
import java.util.Objects;

public record Lancamento(
        String id,
        LocalDate data,
        TipoLancamento tipo,
        String descricao,
        Money valor) {

    public Lancamento {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(tipo, "tipo");
        Objects.requireNonNull(descricao, "descricao");
        Objects.requireNonNull(valor, "valor");
    }
}
