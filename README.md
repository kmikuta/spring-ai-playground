# Spring AI Playground

Sandbox for exploring [Spring AI](https://spring.io/projects/spring-ai) features: Chats, MCP servers and AI Agents.

Built with Spring Boot 4 and Anthropic Claude.

## Prerequisites

- Java 25
- An [Anthropic API key](https://console.anthropic.com/)

## Getting started

```bash
# 1. Configure shared git hooks (one-time setup)
git config core.hooksPath .githooks

# 2. Start the MCP tool server
./gradlew :spring-ai-mcp:bootRun

# 3. Start the web client (in a separate terminal)
ANTHROPIC_API_KEY=<your-key> ./gradlew :spring-ai-web:bootRun
```
