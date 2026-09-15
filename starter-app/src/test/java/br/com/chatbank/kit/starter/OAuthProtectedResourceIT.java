package br.com.chatbank.kit.starter;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
        properties = {
            "chatbank.kit.auth.enabled=true",
            "chatbank.kit.auth.issuer=http://localhost:8080",
            "chatbank.kit.auth.resource=http://localhost:8080/mcp"
        })
class OAuthProtectedResourceIT {

    @LocalServerPort
    private int port;

    @Test
    void metadataDaResourceE401NoMcpSemToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> meta = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/.well-known/oauth-protected-resource"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(meta.statusCode()).isEqualTo(200);
        assertThat(meta.body()).contains("authorization_servers");
        assertThat(meta.body()).contains("contas:read");

        HttpResponse<String> as = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/.well-known/oauth-authorization-server"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(as.statusCode()).isEqualTo(200);
        assertThat(as.body()).contains("authorization_endpoint");
        assertThat(as.body()).contains("S256");

        String body = """
                {
                  "jsonrpc": "2.0",
                  "id": 1,
                  "method": "initialize",
                  "params": {
                    "protocolVersion": "2025-03-26",
                    "capabilities": {},
                    "clientInfo": { "name": "kit-it", "version": "0.1.0" }
                  }
                }
                """;
        HttpResponse<String> mcp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/mcp"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json, text/event-stream")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(mcp.statusCode()).isEqualTo(401);
        assertThat(mcp.headers().firstValue("WWW-Authenticate").orElse(""))
                .contains("resource_metadata");
    }
}
