# Current Active Task

**Current Agent**: Codex
**Task**: Phase 3 Accuracy + Practice Extraction (Completed)

## Completed in This Session
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
- [ ] Display accuracy in Review tab for analyzed games
- [ ] Show accuracy on game history list
- [ ] Optional: Add daily puzzles from bundled puzzles

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
