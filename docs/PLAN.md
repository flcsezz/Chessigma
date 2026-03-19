# Chessigma Project Plan

## Recommended Timeline
- **Phase 1**: Foundation and environment stabilization. Complete before major feature work.
- **Phase 2**: Core board/game state and local play scaffolding. Complete before serious review UX work.
- **Phase 3**: Review-first coaching system. This is the correct phase for Chess.com-style game review, Stockfish analysis, eval bar, move classifications, and AI explanations. ✅ COMPLETE
- **Phase 4**: Bot play and puzzle experiences after the review pipeline is stable enough to reuse engine/review components.
- **Phase 5**: Polish, social features, settings, and product refinement.

## Phase 1: Foundation (In Progress)
- [x] Project scaffolding (Android, Hilt, Room, Compose)
- [x] CI/CD Setup
- [x] Database Schema
- [x] Core Domain Models
- [x] Java 17 Environment Setup (OpenCode - 2026-03-19)
- [x] Stockfish Engine Wrapper (Native Binaries Integrated) <!-- id: 1.1 -->
- [x] AI Cascade Scaffolding (Gemini -> Groq -> NVIDIA rotation + fallback) (Antigravity - 2026-03-19) <!-- id: 1.1 -->
- [x] Android SDK Installation (OpenCode - 2026-03-19)

## Phase 2: Core Chess Logic & UI
- [x] PGN/FEN Parsing Implementation
- [x] Move Validation Engine
- [x] Chessboard UI Component (Drag & Drop) (Gemini CLI - 2026-03-19)
- [x] Professional Move List (Gemini CLI - 2026-03-19)
- [x] Player Cards & Capture Tracking (Gemini CLI - 2026-03-19)
- [x] Game State Management (MVVM)
- [x] Premium Vector Piece Assets (Replace Unicode)
- [x] Game Persistence — `LocalGameRepository` + Room auto-save (Antigravity - 2026-03-19)
- [ ] Tactical Juice (Sound effects & Haptics)

## Phase 3: AI Coaching & Analysis
- [x] Real-time Evaluation Bar (Gemini CLI - 2026-03-19)
- [x] Non-linear Eval Bar scaling (Logarithmic/Sigmoidal)
- [x] Interactive Move Navigation (Tap move to see position)
- [x] Material Advantage Indicator (+X score)
- [x] Review Game Flow (`Review` action starts Stockfish analysis on a completed game) (Codex - 2026-03-19)
- [x] Review Game Flow (`ReviewGameUseCase` walks a completed game via Stockfish) (Antigravity - 2026-03-19)
- [x] Per-move engine review (eval before/after, best uci, classification) (Antigravity - 2026-03-19)
- [x] Review result persistence for engine outputs (Antigravity - 2026-03-19)
- [x] Review UI with board, move navigation, eval sparkline graph, and color-coded move list (Antigravity - 2026-03-19)
- [x] AI Coach summarization layer (review-derived coaching summary, weaknesses/strengths, practice suggestions) (Codex - 2026-03-19)
- [x] Per-game accuracy calculation (Lichess-style formula) (Codex - 2026-03-19)
- [x] Personal Puzzle Generation from mistake/blunder positions (Codex - 2026-03-19)
- [x] Puzzle Solving UI with interactive board (Codex - 2026-03-19)

### Phase 3 Target Sequence
1. Review data pipeline: completed game -> Stockfish move-by-move analysis -> persisted review results.
2. Review UI: board, move list, move navigation, evaluation bar, and best-line display.
3. AI coaching layer: explain mistakes/blunders in plain language from persisted review data; optional provider phrasing remains an enhancement, not a prerequisite.
4. Optimization: progressive loading, caching, and API-budget controls so review feels fast and does not spam providers.

### AI Cascade Focus Gate
Do not prioritize full AI cascade implementation until these are done:
- completed-game Stockfish review pipeline
- persisted per-move review results
- review UI capable of rendering engine output without AI

Once those are stable, the AI cascade becomes a Phase 3 enhancement layer rather than a dependency for core review functionality.

## Phase 4: Play & Puzzles
- [ ] Bot Play Interface
- [x] Puzzle Solving Flow (Codex - 2026-03-19)
- [ ] Lichess Puzzle Database Import Logic

## Phase 5: Polish & Social
- [ ] Accuracy Graphs & ELO Trends
- [ ] Settings & API Key Management
- [ ] Auth & Firebase Integration
