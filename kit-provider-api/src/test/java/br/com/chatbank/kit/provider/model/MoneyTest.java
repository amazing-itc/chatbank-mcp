package br.com.chatbank.kit.provider.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void brlUsaMoedaBRL() {
        Money money = Money.brl(152345);
        assertEquals(152345, money.centavos());
        assertEquals("BRL", money.moeda());
    }

    @Test
    void rejeitaMoedaVazia() {
        assertThrows(IllegalArgumentException.class, () -> new Money(1, " "));
    }
}
