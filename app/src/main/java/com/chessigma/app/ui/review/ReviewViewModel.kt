package com.chessigma.app.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessigma.app.data.repository.AiRepository
import com.chessigma.app.domain.model.AiCascadeState
import com.chessigma.app.domain.model.ReviewMoveResult
import com.chessigma.app.domain.repository.LocalGameRepository
import com.chessigma.app.domain.usecase.CalculateGameAccuracyUseCase
import com.chessigma.app.domain.usecase.ReviewGameUseCase
import com.chessigma.app.ui.puzzles.PuzzleGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── State ─────────────────────────────────────────────────────────────────────

sealed class ReviewState {
    object Idle : ReviewState()
    data class Analysing(val completedPlies: Int, val totalPlies: Int) : ReviewState()
    data class Done(val moves: List<ReviewMoveResult>) : ReviewState()
    data class Error(val message: String) : ReviewState()
}

// ── UiState ───────────────────────────────────────────────────────────────────

data class ReviewUiState(
    val reviewState: ReviewState = ReviewState.Idle,
    val selectedPly: Int = 0,
    val gameId: String? = null,
    val coachState: AiCascadeState = AiCascadeState.Idle,
    val recentGames: List<com.chessigma.app.data.local.GameEntity> = emptyList(),
    val currentGame: com.chessigma.app.data.local.GameEntity? = null
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewGameUseCase: ReviewGameUseCase,
    private val localGameRepository: LocalGameRepository,
    private val aiRepository: AiRepository,
    private val calculateGameAccuracyUseCase: CalculateGameAccuracyUseCase,
    private val puzzleGenerator: PuzzleGenerator
) : ViewModel() {

    private val _reviewState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    private val _selectedPly = MutableStateFlow(0)
    private val _gameId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ReviewUiState> = combine(
        _reviewState,
        _selectedPly,
        _gameId,
        aiRepository.cascadeState
    ) { state, ply, id, coachState ->
        Quadruple(state, ply, id, coachState)
    }.combine(localGameRepository.getRecentGames()) { quad, recent ->
        Five(quad.first, quad.second, quad.third, quad.fourth, recent)
    }.combine(
        _gameId.flatMapLatest { id ->
            if (id != null) localGameRepository.observeGame(id)
            else flowOf(null)
        }
    ) { five, current ->
        ReviewUiState(
            reviewState = five.first,
            selectedPly = five.second,
            gameId = five.third,
            coachState = five.fourth,
            recentGames = five.fifth,
            currentGame = current
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReviewUiState()
    )

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
    private data class Five<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)

    /** Convenience accessor to the reviewed move list (empty while idle/analysing). */
    val reviewedMoves: List<ReviewMoveResult>
        get() = (_reviewState.value as? ReviewState.Done)?.moves ?: emptyList()

    /** Eval history for the sparkline graph — centipawns, White-relative. */
    val evalHistory: List<Float>
        get() = buildList {
            val done = _reviewState.value as? ReviewState.Done ?: return@buildList
            done.moves.forEach { add(it.evalCpBefore / 100f) }
            done.moves.lastOrNull()?.let { add(it.evalCpAfter / 100f) }
        }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Load the most recently played game and start analysing it automatically. */
    fun loadLatestGame() {
        viewModelScope.launch {
            val games = localGameRepository.getRecentGames().firstOrNull()
            val latestId = games?.firstOrNull()?.id ?: return@launch
            startReview(latestId)
        }
    }

    /** Start analysing a specific game by ID. */
    fun startReview(gameId: String) {
        if (_reviewState.value is ReviewState.Analysing) return
        _gameId.value = gameId
        _selectedPly.value = 0

        viewModelScope.launch {
            val totalMoves = localGameRepository.loadGame(gameId)?.second?.size ?: 0
            _reviewState.value = ReviewState.Analysing(0, totalMoves)

            val results = mutableListOf<ReviewMoveResult>()
            reviewGameUseCase(gameId)
                .catch { e ->
                    _reviewState.value = ReviewState.Error(e.message ?: "Analysis failed")
                }
                .collect { result ->
                    results.add(result)
                    _reviewState.value = ReviewState.Analysing(results.size, totalMoves)
                }

            if (_reviewState.value !is ReviewState.Error) {
                _reviewState.value = ReviewState.Done(results.toList())

                val game = localGameRepository.loadGame(gameId)?.first
                val accuracies = calculateGameAccuracyUseCase(results.toList(), game?.userColor)
                localGameRepository.updateAccuracy(gameId, accuracies.first, accuracies.second)

                aiRepository.generateReviewCoachInsight(gameId, results.toList())

                puzzleGenerator.generateFromGame(gameId)
            }
        }
    }

    fun selectPly(ply: Int) {
        _selectedPly.value = ply
    }
}
