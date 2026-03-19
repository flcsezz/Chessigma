# Current Active Task

**Current Agent**: Gemini CLI
**Task**: PlayScreen UI/UX Polish & SAN Implementation

## Objectives
- [x] Implement a custom Chessboard Jetpack Compose component.
- [x] Support board representation from `ChessBoard` domain model.
- [x] Implement smooth drag-and-drop for chess pieces.
- [x] Support board orientation (flipping for black/white).
- [x] Integrate with `ValidateMoveUseCase` for legal move highlighting and execution.
- [x] Integrate `Chessboard` into a basic `PlayScreen`.
- [x] Validate that post-game review is the correct trigger for coaching instead of live per-move API calls.
- [x] Add `ApplyMoveUseCase` and unit coverage for legal move application.
- [x] Implement explicit pawn promotion dialog.
- [x] Implement end-of-game detection (Checkmate, Stalemate, Draw).
- [x] Add move history and undo functionality.
- [x] Implement Professional Move List component with turn grouping.
- [x] Integrate Standard Algebraic Notation (SAN) for move history.
- [x] Add Player Identification cards with captured pieces.
- [x] Add live Evaluation Bar powered by Stockfish 16.

## Context
Phase 1 foundation is complete. The current local play flow is useful scaffolding, but the target product behavior is now clear: users should press `Review` on a finished game, Stockfish should analyze the game move by move, and the AI layer should convert those engine results into human-readable feedback and practice advice.

## Next Steps
1. **Visual Polish**: Replace Unicode chess pieces with high-quality Vector/SVG assets in `Chessboard.kt`.
2. **Move Interactivity**: Implement navigation logic to allow tapping a move in the `MoveList` to view that position.
3. **Engine Refinement**: Implement non-linear scaling for the `EvalBar` to make it more sensitive in the [-2, +2] range.
4. **Material logic**: Add a material advantage score (e.g. +3) to the `PlayerCard` based on captured pieces.
5. **Persistence**: Implement `GameRepository` to persist local games to Room for post-game analysis.
6. **Review Pipeline**: Build a `ReviewGameUseCase` that walks a completed game and records per-move engine outputs.
