package br.com.chatbank.kit.auth;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chatbank.kit.auth")
public class KitAuthProperties {

    private boolean enabled = false;
    private String issuer = "http://localhost:8080";
    private String resource = "http://localhost:8080/mcp";
    private List<String> scopes = new ArrayList<>(List.of("contas:read"));
    private String clientId = "chatbank-mcp-dev";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String resourceMetadataUrl() {
        String base = issuer.endsWith("/") ? issuer.substring(0, issuer.length() - 1) : issuer;
        return base + "/.well-known/oauth-protected-resource";
    }

    public String wwwAuthenticate() {
        return "Bearer resource_metadata=\"" + resourceMetadataUrl() + "\", scope=\""
                + String.join(" ", scopes) + "\"";
    }
}
