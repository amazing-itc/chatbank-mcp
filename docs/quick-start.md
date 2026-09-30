# Quick start

## O que você precisa

JDK 21 e Maven 3.9+. Node.js só entra se for abrir o MCP Inspector. Não há Compose neste repositório.

## 1. Build

Na raiz:

```bash
mvn clean install
```

## 2. Servidor

```bash
mvn spring-boot:run -pl starter-app
```

O endpoint é `POST http://localhost:8080/mcp` (Streamable HTTP). Por padrão não há login (`chatbank.kit.auth.enabled=false`). Quem responde é o `mock-bank`.

## 3. Inspector

```bash
npx @modelcontextprotocol/inspector@latest
```

Transporte **Streamable HTTP** → `http://localhost:8080/mcp` → **Connect** → aba **Tools**.

## Parar

Encerre o processo do Maven. Não há volume nem banco para apagar: as cinco contas demo vivem em memória.
