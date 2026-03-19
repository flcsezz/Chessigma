# Chessigma Project Plan

## Recommended Timeline
- **Phase 1**: Foundation and environment stabilization. Complete before major feature work.
- **Phase 2**: Core board/game state and local play scaffolding. Complete before serious review UX work.
- **Phase 3**: Review-first coaching system. This is the correct phase for Chess.com-style game review, Stockfish analysis, eval bar, move classifications, and AI explanations.
- **Phase 4**: Bot play and puzzle experiences after the review pipeline is stable enough to reuse engine/review components.
- **Phase 5**: Polish, social features, settings, and product refinement.

## Phase 1: Foundation (In Progress)
- [x] Project scaffolding (Android, Hilt, Room, Compose)
- [x] CI/CD Setup
- [x] Database Schema
- [x] Core Domain Models
- [x] Java 17 Environment Setup (OpenCode - 2026-03-19)
- [x] Stockfish Engine Wrapper (Native Binaries Integrated) <!-- id: 1.1 -->
- [ ] AI Cascade Scaffolding (Stub only; do not prioritize until Phase 3 review pipeline is working)
- [x] Android SDK Installation (OpenCode - 2026-03-19)

## Phase 2: Core Chess Logic & UI
- [x] PGN/FEN Parsing Implementation
- [x] Move Validation Engine
- [x] Chessboard UI Component (Drag & Drop) (Gemini CLI - 2026-03-19)
- [x] Professional Move List (Gemini CLI - 2026-03-19)
- [x] Player Cards & Capture Tracking (Gemini CLI - 2026-03-19)
- [x] Game State Management (MVVM)
- [ ] Premium Vector Piece Assets (Replace Unicode)
- [ ] Tactical Juice (Sound effects & Haptics)

## Phase 3: AI Coaching & Analysis
- [x] Real-time Evaluation Bar (Gemini CLI - 2026-03-19)
- [ ] Non-linear Eval Bar scaling (Logarithmic/Sigmoidal)
- [ ] Interactive Move Navigation (Tap move to see position)
- [ ] Material Advantage Indicator (+X score)
- [ ] Review Game Flow (`Review` action starts Stockfish analysis on a completed game)
- [ ] Per-move engine review (eval before/after, best line, classification)
- [ ] Review result persistence for engine outputs
- [ ] Review UI with board, move navigation, eval bar, and best-line display
- [ ] AI Coach summarization layer (convert reviewed engine output into human coaching language)
- [ ] Practice recommendations from reviewed-game patterns
- [ ] Personal Puzzle Generation UI

### Phase 3 Target Sequence
1. Review data pipeline: completed game -> Stockfish move-by-move analysis -> persisted review results.
2. Review UI: board, move list, move navigation, evaluation bar, and best-line display.
3. AI coaching layer: only after steps 1-2 are working, explain mistakes/blunders in plain language and generate end-of-review practice advice.
4. Optimization: progressive loading, caching, and API-budget controls so review feels fast and does not spam providers.

### AI Cascade Focus Gate
Do not prioritize full AI cascade implementation until these are done:
- completed-game Stockfish review pipeline
- persisted per-move review results
- review UI capable of rendering engine output without AI

Once those are stable, the AI cascade becomes a Phase 3 enhancement layer rather than a dependency for core review functionality.

## Phase 4: Play & Puzzles
- [ ] Bot Play Interface
- [ ] Puzzle Solving Flow
- [ ] Lichess Puzzle Database Import Logic

## Phase 5: Polish & Social
- [ ] Accuracy Graphs & ELO Trends
- [ ] Settings & API Key Management
- [ ] Auth & Firebase Integration
