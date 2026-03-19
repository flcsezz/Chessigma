# Architectural Decisions Log (ADR)

## ADR 1: Clean Architecture + MVVM
- **Status**: Accepted
- **Context**: Need a scalable, testable Android app.
- **Decision**: Use UI -> Domain -> Data layers. No Android dependencies in the Domain layer.
- **Consequence**: More boilerplate initially, but easier to swap Stockfish versions or AI providers later.

## ADR 2: AI Cascade Strategy
- **Status**: Accepted
- **Context**: Reliability and rate limits of free AI APIs.
- **Decision**: Implement a cascade (Gemini -> Groq -> NVIDIA).
- **Consequence**: Robust feedback loops even if one provider is down or rate-limited.

## ADR 3: Java 17 Toolchain
- **Status**: Accepted
- **Context**: JDK 25 on host machine causes Gradle/Kotlin parse errors.
- **Decision**: Force Gradle to use Java 17 via `jvmToolchain(17)`.
- **Consequence**: Stable builds across environments.

## ADR 4: Manual Java 17 Installation
- **Status**: Accepted
- **Context**: SDKMAN installation failed due to missing `zip` package (no sudo access).
- **Decision**: Download Java 17 directly from Adoptium (Temurin) and set up manually.
- **Consequence**: Bypasses SDKMAN dependency. Java 17 is installed at `~/java-installs/jdk-17.0.10+7` with `.java-version` file for project configuration.
