package br.com.chatbank.kit.auth;

import java.util.List;
import java.util.Map;

/** _meta de runtime para o host abrir o login (spec MCP). */
public final class AuthMeta {

    private AuthMeta() {
    }

    public static Map<String, Object> wwwAuthenticate(String headerValue) {
        return Map.of("mcp/www_authenticate", List.of(headerValue));
    }
}
