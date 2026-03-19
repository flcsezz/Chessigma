# Chessigma Agent Context

Premium Android chess app with local Stockfish analysis and AI-driven coaching.

## Quick Start
1. Read `docs/CURRENT_TASK.md` for your assignment
2. See `docs/CONTRIBUTING.md` for doc update rules

## Core Mandates
1. **Clean Architecture**: `UI -> Domain -> Data`. No Android deps in `domain`.
2. **Java 17**: Always use Java 17 for Gradle builds.
3. **Docs**: Update `PROGRESS.md`, `CURRENT_TASK.md` after every task (success or failure).
4. **Testing**: Domain logic changes require unit tests.

## Tech Stack
Android Compose + Material 3 | MVVM + Hilt | Room | Stockfish 16 (JNI) | Gemini/Groq/NVIDIA NIM
