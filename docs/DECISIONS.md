# Architectural Decisions Log

| ADR | Title | Status | Decision | Consequence |
|-----|-------|--------|----------|-------------|
| 1 | Clean Architecture + MVVM | ✅ | UI→Domain→Data layers; no Android deps in Domain | Testable, provider-swappable |
| 2 | AI Cascade Strategy | ✅ | Gemini→Groq→NVIDIA fallback chain | Robust against rate limits / downtime |
| 3 | Java 17 Toolchain | ✅ | `jvmToolchain(17)` in build scripts | Stable builds despite host JDK 25 |
| 4 | Manual Java 17 Install | ✅ | Downloaded Temurin 17 directly (no sudo); `.java-version` file for project pin | Bypasses SDKMAN |
| 5 | Chesslib Dependency Fix | ✅ | JitPack + corrected group `com.github.bhlangonijr:chesslib`; use `getPieceSide()` | Resolves correctly from JitPack |
| 6 | ChessboardState Pattern | ✅ | `@Stable` class encapsulates board/selection/orientation; `pointerInput` for D&D | Centralized, ViewModel-friendly |
| 7 | `org.gradle.java.home` Override | ✅ | Set in `gradle.properties` to local Java 17 path | All agents/local builds use correct JVM |
| 8 | Fire-and-Forget Persistence | ✅ | `saveMove`/`finalizeGame` launched as independent `viewModelScope.launch` after UI update | No UI latency; last move may not persist on kill (acceptable for MVP) |
| 9 | RepositoryModule as Abstract Class | ✅ | `@Binds` requires abstract class; all interface→impl bindings go here | Consistent DI pattern |
| 10 | `ReviewGameUseCase` emits `Flow<ReviewMoveResult>` | ✅ | Emits one result per ply; persists to Room inside the flow | UI shows live progress bar; producer/consumer decoupled |
| 11 | Eval Always White-Relative | ✅ | Stockfish per-move scores stored from White's POV; `cpLoss` for Black = `(evalAfter − evalBefore).coerceAtLeast(0)` | Single consistent axis for graph/classification |
| 12 | Core Material Icons Only | ✅ | Use `KeyboardArrowLeft/Right` instead of `ChevronLeft/Right` | Avoids `material-icons-extended` dependency & sync errors |
| 13 | CLI `JAVA_HOME` Enforcement | ✅ | Prepend specific JDK path to `./gradlew` commands | Bypasses host system Java 25 version mismatch |
| 14 | Deterministic Review Coaching First | ✅ | Generate coaching summaries directly from `ReviewMoveResult` data before optional provider phrasing | Review works immediately, offline-capable, and does not block on API setup |
