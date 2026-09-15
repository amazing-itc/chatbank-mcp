package br.com.chatbank.kit.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class WwwAuthenticateEntryPoint implements AuthenticationEntryPoint {

    private final KitAuthProperties properties;

    public WwwAuthenticateEntryPoint(KitAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("WWW-Authenticate", properties.wwwAuthenticate());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter()
                .write("{\"error\":\"unauthorized\",\"error_description\":\"Authentication required.\"}");
    }
}
