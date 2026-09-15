package br.com.chatbank.kit.provider.model;

import java.util.List;
import java.util.Objects;

public record Pagina<T>(List<T> itens, int pagina, int tamanho, int total) {

    public Pagina {
        Objects.requireNonNull(itens, "itens");
        itens = List.copyOf(itens);
    }
}
