# Current Active Task

**Current Agent**: Antigravity
**Task**: Phase 5: Polish & Social (Complete)

## Completed in This Session
- [x] Implement ELO tracking with `EloHistoryEntity` and DAO
- [x] Create `StatsRepository` and `GetEloHistoryUseCase`
- [x] Scaffold `StatsScreen` with Vico ELO trend chart
- [x] Implement User Settings with DataStore-backed repository
- [x] Create `SettingsScreen` for API key management (Gemini, Groq, NVIDIA)
- [x] Scaffold Supabase Auth integration (`AuthRepository`)
- [x] Add Supabase BOM and dependencies to `libs.versions.toml`
- [x] Update `MainActivity` with Stats and Settings tabs
- [x] Resolve build failures (dependency names, missing imports, Hilt package, Vico overloads)
- [x] BUILD SUCCESSFUL
- [x] Implement Bot Play with Stockfish Skill Levels
- [x] Create `GetBotMoveUseCase` for engine move generation
- [x] Update `PlayViewModel` to handle autonomous CPU moves with delay
- [x] Scaffold "New Game" selection UI in `PlayScreen`
- [x] Implement Lichess Daily Puzzle import API integration
- [x] Create `ImportLichessPuzzleUseCase` and DTOs
- [x] Modify `PersonalPuzzleEntity` schema to support external puzzles
- [x] Add "Daily Puzzles" tab and import action in `PuzzleScreen`
- [x] BUILD SUCCESSFUL
- [x] Display accuracy in Review tab (saved metadata + on-the-fly)
- [x] Implement Game History list scaffold in Review tab
- [x] Add saved accuracy chips to Game History items
- [x] Resolve Flow combine overload (6 flows) in ReviewViewModel
- [x] Fix deprecation and OptIn warnings
- [x] Implement AI Cascade (Gemini -> Groq -> NVIDIA NIM -> Deterministic)
- [x] Create LLM Request/Response DTOs
- [x] Update API service interfaces
- [x] Add AI provider rotation and prompt generation in `AiRepository`
- [x] Verify cascade logic with `AiRepositoryTest` (manual mocks)
- [x] Create `CalculateGameAccuracyUseCase` - Lichess-style accuracy formula
- [x] Update `GameDao` and `LocalGameRepository` to persist accuracy
- [x] Update `PersonalPuzzleDao` with solve tracking methods (markSolved, incrementAttempts)
- [x] Create `PuzzlePlayViewModel` for puzzle solving flow
- [x] Update `PuzzleScreen` with functional board and move handling
- [x] Add Puzzle tab to MainActivity navigation (Play/Review/Puzzles)
- [x] Wire puzzle generation to trigger after review completes
- [x] BUILD SUCCESSFUL (assembleDebug)

## Completed in Review Pipeline Session
- [x] Add `updateMoveReview()` to `MoveDao` and `markAnalysed()` to `GameDao`
- [x] Extend `LocalGameRepository` + `LocalGameRepositoryImpl` with review persistence methods
- [x] Create `ReviewMoveResult` domain model
- [x] Implement `ReviewGameUseCase` — Stockfish analysis per move, classification, persistence
- [x] Create `ReviewViewModel` with `Idle/Analysing/Done/Error` states, selectedPly, evalHistory
- [x] Create `ReviewScreen` — board + eval bar + sparkline graph + color-coded move list + nav arrows
- [x] Add Play/Review bottom tab navigation to `MainActivity`
- [x] `ReviewGameUseCaseTest` — 8 unit tests, all green
- [x] BUILD SUCCESSFUL (assembleDebug)
- [x] Implement deterministic review coaching summary from `ReviewMoveResult`
- [x] Replace dummy coach insight payload with persisted review-derived insights
- [x] Render coach summary, weaknesses/strengths, and practice-next suggestions in `ReviewScreen`
- [x] Add explicit `Review Game` action from completed play flow into the Review tab
- [x] `GenerateReviewCoachInsightUseCaseTest` — green
- [x] BUILD SUCCESSFUL (assembleDebug, Java 17 via explicit `JAVA_HOME`)

## Completed in Persistence Session
- [x] Implement `LocalGame` and `MoveHistory` Room entities.
- [x] Create `GameDao` (+ `updateResult`) and `GameRepository`.
- [x] Persist automated saves after every legal move play.
- [x] Update `PlayViewModel` to generate UUID game ID and auto-save.

## Completed in Visual Polish Session
- [x] Integrate Premium Vector Pieces (White & Black).
- [x] Implement Non-linear Evaluation Bar scaling.
- [x] Add Material Advantage (+X) scoring to Player Cards.
- [x] Implement Board History Seeking/Navigation via Move List.
- [x] Resolve GitHub CI "Build Failed" (local Java path fix).

## Completed in Bug Fixes Session
- [x] Add timeout to Stockfish `readLine()` calls
- [x] Add mutex to Stockfish UCI command/response
- [x] Add `fallbackToDestructiveMigration()` to database
- [x] Add `GsonConverterFactory` to ChessCom/Lichess Retrofit
- [x] Wrap ViewModel init in try/catch
- [x] Fix promotion check ordering logic
- [x] Fix eval race condition with Job cancellation
- [x] Fix `rememberChessboardState` keys and flipped board rendering
- [x] Fix double-tap on pieces (deselection)

## Next Objectives
- [ ] Phase 5: Polish & Social Features
- [ ] Accuracy Graphs & ELO Trends
- [ ] Settings & API Key Management

## Context
Phase 3 is complete. Games now have:
1. Per-game accuracy stats computed using a review-based formula
2. Personal puzzle extraction from identified mistake and blunder positions
3. Functional puzzle solving UI in the Puzzles tab

## Implementation Details

### Accuracy Formula
The Lichess-style accuracy is calculated using a mistake score system:
- Brilliant/Best/Excellent: 0 points
- Good: 1 point
- Inaccuracy: 2 points
- Mistake: 4 points
- Blunder: 8 points
- Miss: 10 points

Accuracy = 100 - (average_mistake_score * 10)

### Puzzle Generation
After review completes, puzzles are automatically generated from all moves classified as MISTAKE or BLUNDER. Each puzzle stores:
- The FEN position before the mistake
- The correct move (best move from Stockfish)
- The move that was actually played (the blunder)

### Puzzle Solving Flow
The Puzzles tab provides:
- List of personal puzzles from your games
- Interactive board to solve puzzles
- Move validation against the correct answer
- Progress tracking (solved count, attempts)
