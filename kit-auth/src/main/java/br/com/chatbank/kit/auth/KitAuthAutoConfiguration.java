package br.com.chatbank.kit.auth;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@EnableConfigurationProperties(KitAuthProperties.class)
@Import({KitOAuth2Configuration.class, ProtectedResourceMetadataController.class})
public class KitAuthAutoConfiguration {

    @Bean
    @Order(0)
    @ConditionalOnProperty(prefix = "chatbank.kit.auth", name = "enabled", havingValue = "false", matchIfMissing = true)
    SecurityFilterChain kitOpenSecurity(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
