# Chessigma Refactoring Plan

## Phase 1: Rebrand + Feature Removal (COMPLETED)

**Goal:** Transform the app into Chessigma by removing online multiplayer, watch, and blog features while stabilizing local play, puzzles, and learn.

### Summary of Achievements

- **Online Features Removed:** Watch tab, Blog carousel, and all online multiplayer entry points (Tournaments, Lobby, Challenges) are gone.
- **Implementation Purged:** Broadcast, TV, and Streamer implementation paths and models have been deleted.
- **Rebranding:** App renamed to "Chessigma Mobile" globally. All visible Lichess branding, logos, and splash assets replaced.
- **Platform Metadata:** Android `applicationId`, iOS `PRODUCT_BUNDLE_IDENTIFIER`, and package names updated.
- **Stability:** Core local gameplay (Over the Board, vs Computer), Puzzles, and Learn features verified stable and functional.
- **Code Health:** `flutter analyze` passing with zero errors/warnings.

## Phase 2: External Game History + Review Entry

**Goal:** Let the user enter a Chess.com or Lichess username, fetch public game history from that source, show a selectable history list inside Chessigma, and open any selected game in the existing review/analysis flow. PGN import must also continue to open review using the same analysis path.

### Product Rules

- External history is read-only. No login, sync, or account linking is required in Phase 2.
- Review should reuse the current analysis/review experience, not create a parallel reviewer.
- External games should enter review through `AnalysisOptions.pgn` unless there is a strong reason to support a new external archived-game mode.
- PGN import remains a first-class entry point and should feel consistent with external history review.

### Phase 2 Tasks

- [x] `P2-T01` Review-path audit and source contract. Map how imported PGN, archived games, and game-history lists currently reach `AnalysisScreen`. Start with `lib/src/view/more/import_pgn_screen.dart`, `lib/src/view/analysis/pgn_games_list_screen.dart`, `lib/src/model/analysis/analysis_controller.dart`, `lib/src/view/user/game_history_screen.dart`, and `lib/src/model/game/game_history.dart`. → Verify: Phase 2 implementation chooses one review handoff path and documents it before coding.
- [x] `P2-T02` Define external history domain models and provider boundaries. Create a source-agnostic shape for external usernames, history items, paging state, source type, and selected-game payloads so Chess.com and Lichess feed the same UI. → Verify: one normalized model supports both providers and includes enough data to show a list and open review.
- [x] `P2-T03` Implement Lichess public-history fetcher. Use the official Lichess games export API for public user games and convert returned game data into the normalized external-history model. → Verify: entering a Lichess username yields a paginated list of games or a clear empty/error state.
- [ ] `P2-T04` Implement Chess.com public-history fetcher. Use the official Chess.com public API archive endpoints, handle archive discovery plus per-month game loading, and convert results into the normalized external-history model. → Verify: entering a Chess.com username yields a paginated or incrementally loaded list of games or a clear empty/error state.
- [ ] `P2-T05` Build source + username entry UI. Add a screen or entry point where the user picks `Lichess` or `Chess.com`, enters a username, and loads that player’s history. Reuse existing navigation patterns rather than burying the feature in an unrelated screen. → Verify: user can switch source, enter a username, submit, and reach a history result screen.
- [ ] `P2-T06` Build external game-history list UI. Reuse existing game-history presentation patterns where practical, but back it with the new external-history providers instead of the internal account history provider. → Verify: external games render with stable scrolling/loading/error states and can be selected individually.
- [ ] `P2-T07` Wire external game selection into review. When the user taps a fetched external game, open the current review/analysis screen using the game PGN or equivalent imported representation so review behaves like today’s import flow. → Verify: selecting an external game opens `AnalysisScreen` with moves loaded and review tools available.
- [ ] `P2-T08` Consolidate PGN import and external review entry. Ensure imported PGN text/file, multi-game PGN selection, and externally fetched game review all land in the same analysis path and produce consistent move cursor/orientation behavior. → Verify: imported PGN and external-history games both open review without code duplication or divergent UX.
- [ ] `P2-T09` Add caching, rate-limit handling, and failure states. Cache recent lookups, avoid hammering upstream APIs, surface username-not-found and network errors cleanly, and document any per-source limitations. → Verify: repeated lookups behave predictably and 404/429/network failures do not crash the app.
- [ ] `P2-T10` Tests and verification. Add provider/repository tests for both sources, widget tests for source selection/history list/review handoff, and manual checks for Lichess username, Chess.com username, single-game PGN import, and multi-game PGN import. → Verify: `flutter analyze` passes and the relevant tests cover external review flows.

### Phase 2 Dependencies

- `P2-T01` completed before implementation work.
- `P2-T02` before `P2-T03` and `P2-T04`.
- `P2-T03` and `P2-T04` can run in parallel after the shared model contract is settled.
- `P2-T05` and `P2-T06` can start once the provider contracts are stable.
- `P2-T07` after at least one source is returning real normalized game data.
- `P2-T08` after `P2-T07`.
- `P2-T09` and `P2-T10` last.

### P2-T01 Audit Notes

#### Current Review Entry Paths

- `lib/src/view/more/import_pgn_screen.dart`
  - `ImportPgnScreen.handlePgnText` parses clipboard/file text with `PgnGame.parseMultiGamePgn`.
  - Single-game import opens `AnalysisScreen.buildRoute(... AnalysisOptions.pgn(...))`.
  - Multi-game import routes to `PgnGamesListScreen`.
- `lib/src/view/analysis/pgn_games_list_screen.dart`
  - Each selected PGN game opens `AnalysisScreen.buildRoute(... AnalysisOptions.pgn(...))`.
  - Current behavior hardcodes `orientation: Side.white`, `isComputerAnalysisAllowed: true`, and starts at move 1 when moves exist.
- `lib/src/view/user/game_history_screen.dart`
  - Internal Chessigma/Lichess-backed history opens review through `AnalysisOptions.archivedGame`.
  - This path depends on a server-resolvable `gameId`, not self-contained PGN.
- `lib/src/model/game/game_history.dart`
  - Internal history is tightly coupled to `GameRepository.getUserGames(...)` and local `gameStorageProvider`.
  - It is not a good direct reuse point for external source integration without introducing a source-agnostic layer.
- `lib/src/model/analysis/analysis_controller.dart`
  - `AnalysisOptions.pgn` already supports self-contained review for any PGN string plus variant/orientation/computer-analysis flag.
  - `AnalysisOptions.archivedGame` is server-backed and loads by `gameId` through `archivedGameProvider`.

#### Source Contract Decision

- Phase 2 external history must hand selected games into review through `AnalysisOptions.pgn`.
- Do not add a new external `archivedGame` mode in Phase 2.
- Rationale:
  - external games from Chess.com/Lichess public history are naturally fetchable as PGN or PGN-equivalent records
  - `AnalysisOptions.pgn` already powers imported-game review successfully
  - this avoids coupling external review to Chessigma server IDs or internal repository assumptions
  - this keeps PGN import and external-history review on one code path

#### Design Consequences For Phase 2

- `P2-T02` should create normalized external-history items that include:
  - source (`lichess` or `chesscom`)
  - username
  - stable external game identifier or URL
  - display metadata for list rows
  - full PGN string for review handoff, or enough fetch metadata to obtain it before opening review
  - preferred orientation if derivable from the username and PGN headers
- `P2-T06` should reuse UI ideas from `GameHistoryScreen`, but not `userGameHistoryProvider` directly.
- `P2-T07` should centralize a single helper that converts an external-history item into `AnalysisOptions.pgn`.
- `P2-T08` should align imported-PGN and external-history handoff behavior:
  - same move-cursor rule
  - same computer-analysis allowance rule
  - same orientation derivation rule where possible

### Suggested Agent Ownership

- Agent A: shared domain model + provider boundaries.
- Agent B: Lichess history integration.
- Agent C: Chess.com history integration.
- Agent D: entry UI, history list UI, and review handoff.
- Agent E: tests, caching, and failure-state cleanup.

### Done When

- [ ] User can choose `Lichess` or `Chess.com`.
- [ ] User can enter a username and load public game history.
- [ ] User can pick any loaded game and open review in the existing analysis flow.
- [ ] PGN import still works for single-game and multi-game review.
- [ ] Error states for missing usernames, network failures, and upstream rate limits are handled cleanly.
- [ ] Validation passes or blockers are recorded in `CURRENT_TASK.md`.
