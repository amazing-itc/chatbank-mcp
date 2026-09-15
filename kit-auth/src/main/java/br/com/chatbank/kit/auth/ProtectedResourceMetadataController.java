package br.com.chatbank.kit.auth;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(prefix = "chatbank.kit.auth", name = "enabled", havingValue = "true")
public class ProtectedResourceMetadataController {

    private final KitAuthProperties properties;

    public ProtectedResourceMetadataController(KitAuthProperties properties) {
        this.properties = properties;
    }

    @GetMapping(value = "/.well-known/oauth-protected-resource", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> metadata() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("resource", properties.getResource());
        body.put("authorization_servers", List.of(properties.getIssuer()));
        body.put("scopes_supported", properties.getScopes());
        body.put("bearer_methods_supported", List.of("header"));
        return body;
    }
}
