package br.com.chatbank.kit.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuthMetaTest {

    @Test
    void wwwAuthenticateNoMetaMcp() {
        Map<String, Object> meta = AuthMeta.wwwAuthenticate("Bearer resource_metadata=\"http://localhost/x\"");
        assertTrue(meta.containsKey("mcp/www_authenticate"));
        assertTrue(((List<?>) meta.get("mcp/www_authenticate")).getFirst().toString().contains("resource_metadata"));
    }
}
