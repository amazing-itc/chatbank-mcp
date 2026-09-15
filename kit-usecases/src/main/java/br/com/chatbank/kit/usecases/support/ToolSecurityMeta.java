package br.com.chatbank.kit.usecases.support;

import java.util.List;
import java.util.Map;
import org.springframework.ai.mcp.annotation.context.MetaProvider;

/** securitySchemes no _meta do descriptor: noauth (demo) + oauth2 (quando kit-auth está ligado). */
public abstract class ToolSecurityMeta implements MetaProvider {

    private final List<String> scopes;

    protected ToolSecurityMeta(List<String> scopes) {
        this.scopes = List.copyOf(scopes);
    }

    @Override
    public Map<String, Object> getMeta() {
        return Map.of(
                "securitySchemes",
                List.of(
                        Map.of("type", "noauth"),
                        Map.of("type", "oauth2", "scopes", scopes)));
    }

    public static final class ContasRead extends ToolSecurityMeta {
        public ContasRead() {
            super(List.of("contas:read"));
        }
    }
}
