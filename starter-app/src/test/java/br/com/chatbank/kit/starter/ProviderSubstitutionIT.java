package br.com.chatbank.kit.starter;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.chatbank.kit.mockbank.MockBankProvider;
import br.com.chatbank.kit.provider.BankingProvider;
import br.com.chatbank.kit.provider.model.ContaRef;
import br.com.chatbank.kit.provider.model.Money;
import br.com.chatbank.kit.provider.model.Saldo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootTest
class DefaultProviderIT {

    @Autowired
    private BankingProvider provider;

    @Test
    void semBeanDoBancoUsaMock() {
        assertThat(provider).isInstanceOf(MockBankProvider.class);
        Saldo saldo = provider.consultarSaldo(new ContaRef("conta-pf-ana"));
        assertThat(saldo.disponivel().centavos()).isEqualTo(152_345);
    }
}

@SpringBootTest
@Import(ProviderSubstitutionIT.SubstituteConfig.class)
class ProviderSubstitutionIT {

    @Autowired
    private BankingProvider provider;

    @Test
    void beanDoBancoSubstituiOMock() {
        assertThat(provider).isNotInstanceOf(MockBankProvider.class);
        Saldo saldo = provider.consultarSaldo(new ContaRef("qualquer"));
        assertThat(saldo.titular()).isEqualTo("Banco Real");
    }

    @TestConfiguration
    static class SubstituteConfig {

        @Bean
        @Primary
        BankingProvider bancoReal() {
            return new TestBankingProvider(new Saldo("qualquer", "Banco Real", Money.brl(1)));
        }
    }
}
