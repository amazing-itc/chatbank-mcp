package br.com.chatbank.kit.usecases.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Minimiza o structuredContent: só chaves conhecidas da tool e nunca secrets/PII extra.
 */
public final class StructuredContentAllowlist {

    private static final Set<String> BLOQUEADAS = Set.of(
            "password",
            "senha",
            "otp",
            "mfa",
            "token",
            "authorization",
            "secret",
            "pan",
            "cvv",
            "stacktrace",
            "traceid",
            "requestid",
            "sessionid",
            "authorizationheader");

    private static final Set<String> MONEY = Set.of("centavos", "moeda", "formatado");
    private static final Set<String> LANCAMENTO = Set.of("id", "data", "tipo", "descricao", "valor");

    private static final Map<String, Set<String>> POR_TOOL = Map.of(
            "consultar_saldo", Set.of("contaId", "titular", "disponivel"),
            "consultar_extrato", Set.of("contaId", "pagina", "tamanho", "total", "lancamentos"),
            "render_saldo", Set.of("contaId", "titular", "disponivel"),
            "render_extrato", Set.of("contaId", "pagina", "tamanho", "total", "lancamentos"));

    private StructuredContentAllowlist() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> filter(String tool, Map<String, Object> structured) {
        if (structured == null) {
            return Map.of();
        }
        Set<String> raiz = tool == null ? structured.keySet() : POR_TOOL.getOrDefault(tool, structured.keySet());
        return filtrarMapa(structured, raiz, tool);
    }

    private static Map<String, Object> filtrarMapa(Map<String, Object> origem, Set<String> permitidas, String tool) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : origem.entrySet()) {
            if (bloqueada(entry.getKey()) || !permitidas.contains(entry.getKey())) {
                continue;
            }
            out.put(entry.getKey(), filtrarValor(entry.getKey(), entry.getValue(), tool));
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static Object filtrarValor(String chave, Object valor, String tool) {
        if (valor instanceof Map<?, ?> map) {
            Set<String> filhos = filhos(chave);
            return filtrarMapa((Map<String, Object>) map, filhos, tool);
        }
        if (valor instanceof List<?> list) {
            return list.stream().map(item -> filtrarValor(chave, item, tool)).toList();
        }
        return valor;
    }

    private static Set<String> filhos(String chave) {
        return switch (chave) {
            case "disponivel", "valor" -> MONEY;
            case "lancamentos" -> LANCAMENTO;
            default -> Set.of();
        };
    }

    private static boolean bloqueada(String chave) {
        String n = chave.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "");
        return BLOQUEADAS.contains(n);
    }
}
