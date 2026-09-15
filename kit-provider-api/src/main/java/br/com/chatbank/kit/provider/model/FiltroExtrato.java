package br.com.chatbank.kit.provider.model;

import java.time.LocalDate;

public record FiltroExtrato(
        LocalDate dataInicio,
        LocalDate dataFim,
        TipoLancamento tipo,
        int pagina,
        int tamanho) {

    public FiltroExtrato {
        if (pagina < 1) {
            pagina = 1;
        }
        if (tamanho < 1) {
            tamanho = 10;
        }
        if (tamanho > 50) {
            tamanho = 50;
        }
    }

    public static FiltroExtrato padrao() {
        return new FiltroExtrato(null, null, null, 1, 10);
    }
}
