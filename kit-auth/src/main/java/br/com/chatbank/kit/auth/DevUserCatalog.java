package br.com.chatbank.kit.auth;

import java.util.LinkedHashMap;
import java.util.Map;

final class DevUserCatalog {

    static final Map<String, String> CONTAS = new LinkedHashMap<>();

    static {
        CONTAS.put("ana", "conta-pf-ana");
        CONTAS.put("acme", "conta-pj-acme");
        CONTAS.put("bruno", "conta-pf-bruno");
        CONTAS.put("carla", "conta-pf-carla");
        CONTAS.put("delta", "conta-pj-delta");
    }

    private DevUserCatalog() {
    }

    static String contaId(String username) {
        return CONTAS.getOrDefault(username, username);
    }
}
