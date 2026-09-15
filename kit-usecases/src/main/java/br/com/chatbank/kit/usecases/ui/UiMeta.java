package br.com.chatbank.kit.usecases.ui;

import java.util.List;
import java.util.Map;
import org.springframework.ai.mcp.annotation.context.MetaProvider;

public final class UiMeta {

    public static final String SALDO_URI = "ui://chatbank/saldo-card/v1.html";
    public static final String EXTRATO_URI = "ui://chatbank/extrato-list/v1.html";
    public static final String MIME = "text/html;profile=mcp-app";

    private UiMeta() {
    }

    public static Map<String, Object> resourceUri(String uri) {
        return Map.of("ui", Map.of("resourceUri", uri));
    }

    public static Map<String, Object> resourceDescriptor() {
        return Map.of(
                "ui",
                Map.of(
                        "prefersBorder",
                        true,
                        "csp",
                        Map.of("connectDomains", List.of(), "resourceDomains", List.of())));
    }

    public static final class SaldoToolMeta implements MetaProvider {
        @Override
        public Map<String, Object> getMeta() {
            return resourceUri(SALDO_URI);
        }
    }

    public static final class ExtratoToolMeta implements MetaProvider {
        @Override
        public Map<String, Object> getMeta() {
            return resourceUri(EXTRATO_URI);
        }
    }

    public static final class SaldoResourceMeta implements MetaProvider {
        @Override
        public Map<String, Object> getMeta() {
            return resourceDescriptor();
        }
    }

    public static final class ExtratoResourceMeta implements MetaProvider {
        @Override
        public Map<String, Object> getMeta() {
            return resourceDescriptor();
        }
    }
}
