package br.com.chatbank.kit.mockbank;

import br.com.chatbank.kit.provider.BankingProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockBankConfiguration {

    @Bean
    @ConditionalOnMissingBean(BankingProvider.class)
    BankingProvider mockBankProvider() {
        return new MockBankProvider();
    }
}
