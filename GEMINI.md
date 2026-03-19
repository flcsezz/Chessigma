# Chessigma Agent Context

Welcome, Agent. This project is a premium Android chess application with local Stockfish analysis and AI-driven coaching.

## Project Vision
To provide a "Grandmaster in your pocket" experience through on-device analysis and sophisticated AI coaching cascades.

## Documentation Structure
- [Project Roadmap](docs/PLAN.md) - Long-term goals and phases.
- [Progress Tracker](docs/PROGRESS.md) - Historical milestones and current status.
- [Current Active Task](docs/CURRENT_TASK.md) - **Read this first** before starting any work.
- [Architectural Decisions](docs/DECISIONS.md) - Why we chose specific patterns (ADRs).
- [Agent Roles](docs/AGENTS.md) - Definition of specialist roles.
- [Contributing Guide](docs/CONTRIBUTING.md) - Style and workflow rules.

## Core Mandates for Agents
1. **Clean Architecture**: Strictly adhere to the `UI -> Domain -> Data` layer separation. No Android dependencies in `domain`.
2. **Java 17 Toolchain**: Always use Java 17 for Gradle builds to avoid compatibility issues with Kotlin.
3. **Context Preservation**: Update `docs/PROGRESS.md` and `docs/CURRENT_TASK.md` at the end of every task or session.
4. **Testing First**: Domain logic changes must be accompanied by unit tests.

## Tech Stack Highlights
- **Android**: Jetpack Compose, Material 3 (Dark Mode).
- **Architecture**: MVVM + Clean Architecture + Hilt.
- **Database**: Room (Offline first).
- **Engine**: Stockfish 16 (Native C++ via JNI).
- **AI**: Gemini 1.5 Flash, Groq (Llama 3.1), NVIDIA NIM.

---
*This file is foundational for Agent session continuity.*
