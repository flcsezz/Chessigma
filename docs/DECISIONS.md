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

## ADR 5: Chesslib Dependency Fix
- **Status**: Accepted
- **Context**: Build failed with `com.github.bhlangon:chesslib:1.3.3` not found in Maven repositories.
- **Decision**: Add JitPack repository and correct dependency group to `com.github.bhlangonijr:chesslib`.
- **Consequence**: Chesslib resolves correctly from JitPack. Code updated to use `getPieceSide()` instead of private `side` field.

## ADR 6: Custom Chessboard UI State Management
- **Status**: Accepted
- **Context**: Need a responsive, interactive chessboard with drag-and-drop and legal move highlighting.
- **Decision**: Implement a `@Stable` `ChessboardState` class to encapsulate board data, orientation, and selection state. Use Jetpack Compose `pointerInput` with `detectDragGestures` for interaction.
- **Consequence**: Centralized state makes it easy to control the board from ViewModels and supports complex animations/interactions without polluting the main Composable function.

