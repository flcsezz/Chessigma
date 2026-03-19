# Current Active Task

**Current Agent**: Gemini CLI
- [x] PlayScreen UI/UX Polish & SAN Implementation
- [x] Visual Polish: Replace Unicode chess pieces with high-quality Vector/SVG assets.
- [x] Move Interactivity: Implement navigation logic to allow tapping a move in the `MoveList` to view that position.
- [x] Engine Refinement: Implement non-linear scaling for the `EvalBar`.
- [x] Material logic: Add a material advantage score (e.g. +3) to the `PlayerCard`.
- [ ] Persistence: Implement `GameRepository` to persist local games to Room for post-game analysis.
- [ ] Review Pipeline: Build a `ReviewGameUseCase` that walks a completed game and records per-move engine outputs.

## Context
Phase 1 foundation is complete. Visual polish has significantly elevated the `PlayScreen`. The remaining major hurdles for MVP are game persistence and the full post-game analysis/review pipeline.

## Next Steps
1. **Persistence**: Implement `GameRepository` and Room entities for `LocalGame` and `GameHistory`.
2. **Review Pipeline**: Build a `ReviewGameUseCase` that walks a completed game and records per-move engine outputs.
3. **AI Coaching Integration**: Replace dummy cascade data with actual review-based insights.
