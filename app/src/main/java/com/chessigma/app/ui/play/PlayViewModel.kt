package com.chessigma.app.ui.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessigma.app.data.repository.AiRepository
import com.chessigma.app.domain.model.*
import com.chessigma.app.domain.repository.LocalGameRepository
import com.chessigma.app.domain.usecase.ApplyMoveUseCase
import com.chessigma.app.domain.usecase.GetLegalMovesUseCase
import com.chessigma.app.domain.usecase.ParseFenUseCase
import com.chessigma.app.engine.StockfishEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

private const val STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

data class PlayUiState(
    val gameState: GameState,
    val gameId: String,
    val selectedSquare: String? = null,
    val legalMoves: List<String> = emptyList(),
    val isPromotionRequired: Boolean = false,
    val promotionMove: ChessMove? = null,
    val cascadeState: AiCascadeState = AiCascadeState.Idle,
    val statusMessage: String? = null,
    val evaluation: Float = 0.0f,
    val whiteCaptures: List<ChessPiece> = emptyList(),
    val blackCaptures: List<ChessPiece> = emptyList(),
    val whiteMaterialAdvantage: Int = 0,
    val blackMaterialAdvantage: Int = 0,
    val historyIndex: Int = -1, // -1 means live position
    val isVsCpu: Boolean = false,
    val cpuColor: PieceColor = PieceColor.BLACK,
    val skillLevel: Int = 5,
    val isExtractingEngine: Boolean = false,
    val engineExtractionProgress: Float = 0f,
    val settings: UserSettings = UserSettings()
)


@HiltViewModel
class PlayViewModel @Inject constructor(
    private val parseFenUseCase: ParseFenUseCase,
    private val getLegalMovesUseCase: GetLegalMovesUseCase,
    private val applyMoveUseCase: ApplyMoveUseCase,
    private val getBotMoveUseCase: com.chessigma.app.domain.usecase.GetBotMoveUseCase,
    private val getSettingsUseCase: com.chessigma.app.domain.usecase.GetSettingsUseCase,
    private val aiRepository: AiRepository,
    private val stockfishEngine: StockfishEngine,
    private val localGameRepository: LocalGameRepository
) : ViewModel() {

    /** Stable ID for the current play session — created once per ViewModel lifetime. */
    private val gameId: String = UUID.randomUUID().toString()

    private val initialGameState = GameState(board = parseFenUseCase(STARTING_FEN))
    private val gameState = MutableStateFlow(initialGameState)
    private val selectedSquare = MutableStateFlow<String?>(null)
    private val legalMoves = MutableStateFlow<List<String>>(emptyList())
    private val isPromotionRequired = MutableStateFlow(false)
    private val promotionMove = MutableStateFlow<ChessMove?>(null)
    private val statusMessage = MutableStateFlow<String?>(null)
    private val evaluation = MutableStateFlow(0.0f)
    private val historyIndex = MutableStateFlow(-1)
    private val isVsCpu = MutableStateFlow(false)
    private val cpuColor = MutableStateFlow(PieceColor.BLACK)
    private val skillLevel = MutableStateFlow(5)

    private val gamePlayState = combine(
        gameState,
        selectedSquare,
        legalMoves,
        isPromotionRequired,
        promotionMove
    ) { state, square, moves, promoReq, promoMove ->
        GamePlayState(state, square, moves, promoReq, promoMove)
    }

    private val gameAndAiState = combine(
        gamePlayState,
        aiRepository.cascadeState,
        statusMessage,
        evaluation
    ) { gamePlay, cascade, status, eval ->
        GameAndAiState(gamePlay, cascade, status, eval)
    }

    private val playerConfigState = combine(
        historyIndex,
        isVsCpu,
        cpuColor,
        skillLevel
    ) { idx, vsCpu, cpuCol, skill ->
        PlayerConfigState(idx, vsCpu, cpuCol, skill)
    }

    private val engineAndSettingsState = combine(
        stockfishEngine.isExtracting,
        stockfishEngine.extractionProgress,
        getSettingsUseCase()
    ) { extracting, progress, settings ->
        EngineAndSettingsState(extracting, progress, settings)
    }

    val uiState: StateFlow<PlayUiState> = combine(
        gameAndAiState,
        playerConfigState,
        engineAndSettingsState
    ) { gameAi, playerConfig, engineSettings ->
        val currentGameState = gameAi.gamePlayState.gameState
        val (whiteCaptures, blackCaptures, whiteAdv, blackAdv) = calculateMaterial(currentGameState.board.pieces.values)
        
        PlayUiState(
            gameState = currentGameState,
            gameId = gameId,
            selectedSquare = gameAi.gamePlayState.selectedSquare,
            legalMoves = gameAi.gamePlayState.legalMoves,
            isPromotionRequired = gameAi.gamePlayState.isPromotionRequired,
            promotionMove = gameAi.gamePlayState.promotionMove,
            cascadeState = gameAi.cascadeState,
            statusMessage = gameAi.statusMessage,
            whiteCaptures = whiteCaptures,
            blackCaptures = blackCaptures,
            whiteMaterialAdvantage = whiteAdv,
            blackMaterialAdvantage = blackAdv,
            evaluation = gameAi.evaluation,
            historyIndex = playerConfig.historyIndex,
            isVsCpu = playerConfig.isVsCpu,
            cpuColor = playerConfig.cpuColor,
            skillLevel = playerConfig.skillLevel,
            isExtractingEngine = engineSettings.isExtractingEngine,
            engineExtractionProgress = engineSettings.engineExtractionProgress,
            settings = engineSettings.settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayUiState(gameState = initialGameState, gameId = gameId)
    )

    private data class GamePlayState(
        val gameState: GameState,
        val selectedSquare: String?,
        val legalMoves: List<String>,
        val isPromotionRequired: Boolean,
        val promotionMove: ChessMove?
    )

    private data class GameAndAiState(
        val gamePlayState: GamePlayState,
        val cascadeState: AiCascadeState,
        val statusMessage: String?,
        val evaluation: Float
    )

    private data class PlayerConfigState(
        val historyIndex: Int,
        val isVsCpu: Boolean,
        val cpuColor: PieceColor,
        val skillLevel: Int
    )

    private data class EngineAndSettingsState(
        val isExtractingEngine: Boolean,
        val engineExtractionProgress: Float,
        val settings: UserSettings
    )

    init {
        viewModelScope.launch {
            try {
                localGameRepository.createGame(
                    gameId = gameId,
                    startedAt = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModelScope.launch {
            try {
                if (!stockfishEngine.isReady.value) {
                    stockfishEngine.initialise()
                }

                updateEvaluation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var evalJob: kotlinx.coroutines.Job? = null

    private fun updateEvaluation() {
        evalJob?.cancel()
        evalJob = viewModelScope.launch {
            if (stockfishEngine.isReady.value) {
                val score = stockfishEngine.evaluate(gameState.value.board.fen, depth = 10)

                evaluation.value = score / 100.0f
            }
        }
    }

    private fun calculateMaterial(pieces: Collection<ChessPiece>): MaterialData {
        val whiteOnBoard = pieces.filter { it.color == PieceColor.WHITE }.groupBy { it.type }.mapValues { it.value.size }
        val blackOnBoard = pieces.filter { it.color == PieceColor.BLACK }.groupBy { it.type }.mapValues { it.value.size }

        val whiteCaptures = mutableListOf<ChessPiece>()
        val blackCaptures = mutableListOf<ChessPiece>()

        fun addCaptures(color: PieceColor, type: PieceType, expected: Int, actual: Int, list: MutableList<ChessPiece>) {
            repeat(expected - actual) {
                list.add(ChessPiece(type, color))
            }
        }

        // What White captured (Black pieces)
        addCaptures(PieceColor.BLACK, PieceType.QUEEN, 1, blackOnBoard[PieceType.QUEEN] ?: 0, whiteCaptures)
        addCaptures(PieceColor.BLACK, PieceType.ROOK, 2, blackOnBoard[PieceType.ROOK] ?: 0, whiteCaptures)
        addCaptures(PieceColor.BLACK, PieceType.BISHOP, 2, blackOnBoard[PieceType.BISHOP] ?: 0, whiteCaptures)
        addCaptures(PieceColor.BLACK, PieceType.KNIGHT, 2, blackOnBoard[PieceType.KNIGHT] ?: 0, whiteCaptures)
        addCaptures(PieceColor.BLACK, PieceType.PAWN, 8, blackOnBoard[PieceType.PAWN] ?: 0, whiteCaptures)

        // What Black captured (White pieces)
        addCaptures(PieceColor.WHITE, PieceType.QUEEN, 1, whiteOnBoard[PieceType.QUEEN] ?: 0, blackCaptures)
        addCaptures(PieceColor.WHITE, PieceType.ROOK, 2, whiteOnBoard[PieceType.ROOK] ?: 0, blackCaptures)
        addCaptures(PieceColor.WHITE, PieceType.BISHOP, 2, whiteOnBoard[PieceType.BISHOP] ?: 0, blackCaptures)
        addCaptures(PieceColor.WHITE, PieceType.KNIGHT, 2, whiteOnBoard[PieceType.KNIGHT] ?: 0, blackCaptures)
        addCaptures(PieceColor.WHITE, PieceType.PAWN, 8, whiteOnBoard[PieceType.PAWN] ?: 0, blackCaptures)

        val whiteValue = pieces.filter { it.color == PieceColor.WHITE }.sumOf { getPieceValue(it.type) }
        val blackValue = pieces.filter { it.color == PieceColor.BLACK }.sumOf { getPieceValue(it.type) }
        
        val whiteAdv = (whiteValue - blackValue).coerceAtLeast(0)
        val blackAdv = (blackValue - whiteValue).coerceAtLeast(0)

        return MaterialData(whiteCaptures, blackCaptures, whiteAdv, blackAdv)
    }

    private fun getPieceValue(type: PieceType): Int = when (type) {
        PieceType.PAWN -> 1
        PieceType.KNIGHT -> 3
        PieceType.BISHOP -> 3
        PieceType.ROOK -> 5
        PieceType.QUEEN -> 9
        PieceType.KING -> 0
    }

    data class MaterialData(
        val whiteCaptures: List<ChessPiece>,
        val blackCaptures: List<ChessPiece>,
        val whiteAdvantage: Int,
        val blackAdvantage: Int
    )

    fun onSquareSelected(square: String) {
        if (gameState.value.status != GameStatus.ONGOING) return

        val piece = gameState.value.board.getPiece(square)
        if (piece != null && piece.color == gameState.value.board.sideToMove) {
            if (selectedSquare.value == square) {
                selectedSquare.value = null
                legalMoves.value = emptyList()
            } else {
                selectedSquare.value = square
                legalMoves.value = getLegalMovesUseCase(gameState.value.board.fen, square)
                statusMessage.value = null
            }
            return
        }

        val fromSquare = selectedSquare.value
        if (fromSquare != null && legalMoves.value.contains(square)) {
            val move = ChessMove(fromSquare = fromSquare, toSquare = square)
            onMove(move)
            return
        }

        selectedSquare.value = null
        legalMoves.value = emptyList()
    }

    fun onPromotionSelected(pieceType: PieceType) {
        val move = promotionMove.value?.copy(promotionPiece = pieceType) ?: return
        isPromotionRequired.value = false
        promotionMove.value = null
        onMove(move)
    }

    fun onPromotionCancelled() {
        isPromotionRequired.value = false
        promotionMove.value = null
        selectedSquare.value = null
        legalMoves.value = emptyList()
    }

    private fun checkPromotionRequired(move: ChessMove): Boolean {
        val board = gameState.value.board
        val piece = board.getPiece(move.fromSquare) ?: return false
        if (piece.type != PieceType.PAWN) return false
        val rank = move.toSquare.last()
        return (board.sideToMove == PieceColor.WHITE && rank == '8') ||
               (board.sideToMove == PieceColor.BLACK && rank == '1')
    }

    fun onMove(move: ChessMove) {
        if (checkPromotionRequired(move) && move.promotionPiece == null) {
            promotionMove.value = move
            isPromotionRequired.value = true
            return
        }

        val fenBefore = gameState.value.board.fen
        val nextState = applyMoveUseCase(gameState.value, move)
        if (nextState == null) {
            statusMessage.value = "Illegal move."
            legalMoves.value = emptyList()
            selectedSquare.value = null
            return
        }

        gameState.value = nextState
        selectedSquare.value = null
        legalMoves.value = emptyList()
        historyIndex.value = -1 // Return to live
        val lastMove = nextState.moveHistory.lastOrNull()
        statusMessage.value = "Move played: ${lastMove?.san ?: (move.fromSquare + "-" + move.toSquare)}"

        // Persist move (fire-and-forget — does not block UI)
        val ply = nextState.moveHistory.size - 1
        viewModelScope.launch {
            localGameRepository.saveMove(
                gameId = gameId,
                ply = ply,
                move = lastMove ?: move,
                fenBefore = fenBefore
            )
        }

        updateEvaluation()

        if (nextState.status != GameStatus.ONGOING) {
            val resultString = when (nextState.status) {
                GameStatus.CHECKMATE   -> if (nextState.board.sideToMove == PieceColor.WHITE) "0-1" else "1-0"
                GameStatus.RESIGNATION -> if (nextState.board.sideToMove == PieceColor.WHITE) "0-1" else "1-0"
                GameStatus.TIMEOUT     -> if (nextState.board.sideToMove == PieceColor.WHITE) "0-1" else "1-0"
                GameStatus.STALEMATE   -> "1/2-1/2"
                GameStatus.DRAW        -> "1/2-1/2"
                else                   -> "*"
            }
            statusMessage.value = "Game Over: ${nextState.status}"
            viewModelScope.launch {
                localGameRepository.finalizeGame(gameId, resultString)
            }
        }

        viewModelScope.launch {
            aiRepository.generateCoachInsight()
        }

        if (nextState.status == GameStatus.ONGOING && 
            isVsCpu.value && 
            nextState.board.sideToMove == cpuColor.value) {
            triggerBotMove()
        }
    }

    private fun triggerBotMove() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(800L) // Add thinking delay
            val move = getBotMoveUseCase(
                fen = gameState.value.board.fen,
                level = skillLevel.value
            )
            if (move != null) {
                onMove(move)
            }
        }
    }

    fun startVsCpuGame(cpuCol: PieceColor, level: Int) {
        // Reset game state
        isVsCpu.value = true
        cpuColor.value = cpuCol
        skillLevel.value = level
        gameState.value = initialGameState
        selectedSquare.value = null
        legalMoves.value = emptyList()
        historyIndex.value = -1
        
        if (initialGameState.board.sideToMove == cpuCol) {
            triggerBotMove()
        }
    }

    fun startLocalGame() {
        isVsCpu.value = false
        gameState.value = initialGameState
        selectedSquare.value = null
        legalMoves.value = emptyList()
        historyIndex.value = -1
    }

    fun undo() {
        if (gameState.value.fenHistory.isEmpty()) return
        
        val previousFen = gameState.value.fenHistory.last()
        val prevBoard = parseFenUseCase(previousFen)
        
        val newFenHistory = gameState.value.fenHistory.dropLast(1)
        val newMoveHistory = gameState.value.moveHistory.dropLast(1)
        
        gameState.value = GameState(
            board = prevBoard,
            moveHistory = newMoveHistory,
            fenHistory = newFenHistory,
            status = GameStatus.ONGOING // Assuming we can only undo to an ongoing state for now
        )
        
        selectedSquare.value = null
        legalMoves.value = emptyList()
        historyIndex.value = -1
        statusMessage.value = "Move undone."
        updateEvaluation()
    }

    fun onMoveHistoryClicked(index: Int) {
        if (index == historyIndex.value) return
        
        val fens = listOf(STARTING_FEN) + gameState.value.fenHistory
        if (index < 0 || index >= fens.size) return
        
        val targetFen = fens[index]
        val targetBoard = parseFenUseCase(targetFen)
        
        // Update board view without changing game state history
        // This is a "preview" mode.
        historyIndex.value = index
        gameState.value = gameState.value.copy(board = targetBoard)
        
        selectedSquare.value = null
        legalMoves.value = emptyList()
        updateEvaluation()
    }
}
