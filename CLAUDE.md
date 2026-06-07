# Spring AI Playground

A multimodule Gradle project for experimenting with Spring AI. Two modules: an MCP tool server and a web chat client.

## Modules

### spring-ai-mcp (port 8081)
MCP server that exposes tools via the Model Context Protocol over HTTP (streamable). Currently exposes one tool: `generateReport(date)`.

Tool annotations: `@McpTool`, `@McpToolParam`.

### spring-ai-web (port 8080)
Web chat client backed by Anthropic Claude. Orchestrates local tools, conversation memory, and remote MCP tools.

Tool annotations: `@Tool`, `@ToolParam`.

Chat endpoints under `/api/chat`:
- `GET /simple` — basic chat, no tools
- `GET /memory/{conversationId}` — chat with conversation memory
- `GET /tools` — chat with local tools (DateTimeTools, WeatherTools)
- `GET /mcp` — chat using remote tools from the MCP server

## Build & Run

```bash
# Build everything
./gradlew build

# Run MCP server (start this first)
./gradlew :spring-ai-mcp:bootRun

# Run web client (requires MCP server running)
ANTHROPIC_API_KEY=<your-key> ./gradlew :spring-ai-web:bootRun
```

The web module requires `ANTHROPIC_API_KEY` set in the environment. Model: `claude-haiku-4-5`.

## Code Style

Spotless enforces Google Java Format. It runs automatically via a git pre-commit hook — no manual step needed. To apply manually:

```bash
./gradlew spotlessApply
```

CI will fail if formatting is off (`./gradlew spotlessCheck`).

## Tech Stack

- Java 25, Spring Boot 4.0.6, Spring AI 2.0.0-M8
- Gradle 9.5.1
- Anthropic Claude (via `spring-ai-starter-model-anthropic`)
- MCP protocol (via `spring-ai-starter-mcp-server-webmvc` / `spring-ai-starter-mcp-client`)
