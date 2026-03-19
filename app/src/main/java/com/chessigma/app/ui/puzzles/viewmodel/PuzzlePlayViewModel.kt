package com.chessigma.app.ui.puzzles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessigma.app.data.local.PersonalPuzzleDao
import com.chessigma.app.data.local.PersonalPuzzleEntity
import com.chessigma.app.domain.model.ChessBoard
import com.chessigma.app.domain.model.ChessMove
import com.chessigma.app.domain.model.GameState
import com.chessigma.app.domain.usecase.ApplyMoveUseCase
import com.chessigma.app.domain.usecase.GetLegalMovesUseCase
import com.chessigma.app.domain.usecase.ParseFenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PuzzleUiState(
    val currentPuzzle: PersonalPuzzleEntity? = null,
    val board: ChessBoard? = null,
    val legalMoves: List<String> = emptyList(),
    val selectedSquare: String? = null,
    val lastMove: ChessMove? = null,
    val isCorrect: Boolean? = null,
    val showResult: Boolean = false,
    val solvedCount: Int = 0,
    val attemptCount: Int = 0,
    val puzzles: List<PersonalPuzzleEntity> = emptyList(),
    val currentPuzzleIndex: Int = 0
)

@HiltViewModel
class PuzzlePlayViewModel @Inject constructor(
    private val personalPuzzleDao: PersonalPuzzleDao,
    private val parseFenUseCase: ParseFenUseCase,
    private val getLegalMovesUseCase: GetLegalMovesUseCase,
    private val applyMoveUseCase: ApplyMoveUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var gameState = MutableStateFlow<GameState?>(null)

    init {
        loadPuzzles()
    }

    private fun loadPuzzles() {
        viewModelScope.launch {
            personalPuzzleDao.getAllPersonalPuzzles().collect { puzzles ->
                _uiState.value = _uiState.value.copy(
                    puzzles = puzzles,
                    solvedCount = puzzles.count { it.solved }
                )
                if (puzzles.isNotEmpty() && _uiState.value.currentPuzzle == null) {
                    loadPuzzle(0)
                }
            }
        }
    }

    fun loadPuzzle(index: Int) {
        val puzzles = _uiState.value.puzzles
        if (index < 0 || index >= puzzles.size) return

        val puzzle = puzzles[index]
        val board = parseFenUseCase(puzzle.fenPosition)
        gameState.value = board?.let {
            GameState(board = it, moveHistory = emptyList(), fenHistory = emptyList())
        }

        _uiState.value = _uiState.value.copy(
            currentPuzzle = puzzle,
            board = board,
            selectedSquare = null,
            lastMove = null,
            isCorrect = null,
            showResult = false,
            currentPuzzleIndex = index,
            attemptCount = puzzle.attemptCount
        )

        updateLegalMoves()
    }

    private fun updateLegalMoves() {
        val board = _uiState.value.board ?: return
        _uiState.value = _uiState.value.copy(legalMoves = emptyList())
    }

    fun onSquareClick(square: String) {
        val currentState = _uiState.value
        val board = currentState.board ?: return
        val currentGameState = gameState.value ?: return

        val selectedSquare = currentState.selectedSquare

        if (selectedSquare == null) {
            val piece = board.getPiece(square)
            if (piece != null && piece.color == board.sideToMove) {
                val moves = getLegalMovesUseCase(board.fen, square)
                _uiState.value = currentState.copy(
                    selectedSquare = square,
                    legalMoves = moves
                )
            }
        } else {
            val from = selectedSquare
            val to = square

            val isLegalMove = currentState.legalMoves.contains(to)

            if (isLegalMove) {
                handleMove(from, to)
            } else {
                val piece = board.getPiece(square)
                if (piece != null && piece.color == board.sideToMove) {
                    val moves = getLegalMovesUseCase(board.fen, square)
                    _uiState.value = currentState.copy(
                        selectedSquare = square,
                        legalMoves = moves
                    )
                } else {
                    _uiState.value = currentState.copy(
                        selectedSquare = null,
                        legalMoves = emptyList()
                    )
                }
            }
        }
    }

    private fun handleMove(from: String, to: String) {
        val currentState = _uiState.value
        val puzzle = currentState.currentPuzzle ?: return
        val currentGameState = gameState.value ?: return

        viewModelScope.launch {
            personalPuzzleDao.incrementAttempts(puzzle.id)
        }

        val move = ChessMove(fromSquare = from, toSquare = to)
        val newGameState = applyMoveUseCase(currentGameState, move)

        if (newGameState == null) {
            _uiState.value = currentState.copy(
                selectedSquare = null,
                legalMoves = emptyList()
            )
            return
        }

        gameState.value = newGameState

        val playedUci = move.toUci()
        val isCorrect = playedUci == puzzle.correctUci

        _uiState.value = currentState.copy(
            board = newGameState.board,
            selectedSquare = null,
            legalMoves = emptyList(),
            lastMove = move,
            isCorrect = isCorrect,
            showResult = true,
            attemptCount = currentState.attemptCount + 1
        )

        if (isCorrect) {
            viewModelScope.launch {
                personalPuzzleDao.markSolved(puzzle.id)
                val updatedPuzzle = personalPuzzleDao.getPuzzleById(puzzle.id)
                _uiState.value = _uiState.value.copy(
                    solvedCount = _uiState.value.solvedCount + 1,
                    currentPuzzle = updatedPuzzle
                )
            }
        }
    }

    fun nextPuzzle() {
        val currentIndex = _uiState.value.currentPuzzleIndex
        val puzzles = _uiState.value.puzzles
        if (currentIndex < puzzles.size - 1) {
            loadPuzzle(currentIndex + 1)
        }
    }

    fun previousPuzzle() {
        val currentIndex = _uiState.value.currentPuzzleIndex
        if (currentIndex > 0) {
            loadPuzzle(currentIndex - 1)
        }
    }

    fun dismissResult() {
        _uiState.value = _uiState.value.copy(showResult = false)
    }
}
