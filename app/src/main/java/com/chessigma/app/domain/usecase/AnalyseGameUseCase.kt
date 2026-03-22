package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.MoveClassification
import com.chessigma.app.domain.model.ReviewMoveResult
import com.chessigma.app.domain.repository.LocalGameRepository
import com.chessigma.app.engine.StockfishEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Walks all persisted moves of a completed game and runs Stockfish on
 * each position.  Results are persisted back to Room incrementally.
 *
 * Emits the ply index (0-based) as each move finishes so callers can
 * display real-time progress.
 */
class ReviewGameUseCase @Inject constructor(
    private val localGameRepository: LocalGameRepository,
    private val stockfish: StockfishEngine
) {

    companion object {
        private const val ANALYSIS_DEPTH = 12
    }

    /** Returns a [Flow] that emits the ply index (0-based) each time a move is reviewed. */
    operator fun invoke(gameId: String): Flow<ReviewMoveResult> = flow {
        val (_, moves) = localGameRepository.loadGame(gameId) ?: run {
            Timber.w("ReviewGameUseCase: game $gameId not found")
            return@flow
        }

        if (!stockfish.isReady.value) {
            Timber.d("ReviewGameUseCase: initialising Stockfish")
            stockfish.initialise()
            if (!stockfish.isReady.value) {
                Timber.e("ReviewGameUseCase: Stockfish failed to initialise")
                return@flow
            }
        }

        // We need the FEN *after* each move to compute evalAfter.
        // The moves table stores fenBefore for every ply, so fenAfter for ply N
        // is fenBefore for ply N+1 (or the final board FEN for the last move).
        // We compute evalAfter by evaluating the FEN that comes right after the move.
        for (i in moves.indices) {
            val move = moves[i]
            val fenBefore = move.fenBefore
            // fenAfter: the next move's fenBefore, or evaluate from the current FEN
            // We evaluate both the position before and after the move.
            val evalBefore = runCatching {
                stockfish.evaluate(fenBefore, ANALYSIS_DEPTH)
            }.getOrElse { 0 }

            // Best move at this position
            val bestUci = runCatching {
                stockfish.setPosition(fenBefore)
                stockfish.getBestMove(ANALYSIS_DEPTH)
            }.getOrElse { null }?.takeIf { it.isNotBlank() }

            // Evaluate the resulting position (fenBefore of the next move)
            val fenAfter = moves.getOrNull(i + 1)?.fenBefore
                ?: deriveFenAfter(fenBefore, move.uci)
            val evalAfter = if (fenAfter != null) {
                runCatching { stockfish.evaluate(fenAfter, ANALYSIS_DEPTH) }.getOrElse { evalBefore }
            } else evalBefore

            val classification = classifyMove(
                ply = move.ply,
                evalBefore = evalBefore,
                evalAfter = evalAfter,
                playedUci = move.uci,
                bestUci = bestUci
            )

            // Persist back to Room
            localGameRepository.updateMoveReview(
                gameId = gameId,
                ply = move.ply,
                evalBefore = evalBefore,
                evalAfter = evalAfter,
                classification = classification.javaClass.simpleName,
                bestUci = bestUci,
                bestSan = null // SAN conversion would need board context; omit for now
            )

            emit(
                ReviewMoveResult(
                    ply = move.ply,
                    san = move.san,
                    uci = move.uci,
                    fenBefore = fenBefore,
                    evalCpBefore = evalBefore,
                    evalCpAfter = evalAfter,
                    classification = classification,
                    bestUci = bestUci,
                    bestSan = null
                )
            )

            Timber.d("ReviewGameUseCase: ply=${move.ply} san=${move.san} before=$evalBefore after=$evalAfter class=${classification.displayName}")
        }

        localGameRepository.markGameAnalysed(gameId)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Classify a move using cp-loss thresholds from [MoveClassification].
     * Evaluation is always from White's perspective (positive = White winning).
     */
    internal fun classifyMove(
        ply: Int,
        evalBefore: Int,
        evalAfter: Int,
        playedUci: String,
        bestUci: String?
    ): MoveClassification {
        val whiteToMove = ply % 2 == 0
        // cpLoss = how many centipawns the side-to-move lost
        val cpLoss = if (whiteToMove) {
            (evalBefore - evalAfter).coerceAtLeast(0)
        } else {
            (evalAfter - evalBefore).coerceAtLeast(0)
        }

        // "Best" if the played move matches the engine's top choice
        if (playedUci == bestUci) return MoveClassification.Best

        return when {
            cpLoss <= MoveClassification.Excellent.cpThreshold -> MoveClassification.Excellent
            cpLoss <= MoveClassification.Good.cpThreshold      -> MoveClassification.Good
            cpLoss <= MoveClassification.Inaccuracy.cpThreshold -> MoveClassification.Inaccuracy
            cpLoss <= MoveClassification.Mistake.cpThreshold   -> MoveClassification.Mistake
            else                                                -> MoveClassification.Blunder
        }
    }

    /**
     * Fallback: apply [uci] to [fenBefore] using chesslib to get the resulting FEN.
     * Returns null if the move cannot be applied.
     */
    private fun deriveFenAfter(fenBefore: String, uci: String): String? = runCatching {
        val board = com.github.bhlangonijr.chesslib.Board()
        board.loadFromFen(fenBefore)
        val from = com.github.bhlangonijr.chesslib.Square.fromValue(uci.substring(0, 2).uppercase())
        val to   = com.github.bhlangonijr.chesslib.Square.fromValue(uci.substring(2, 4).uppercase())
        val move = com.github.bhlangonijr.chesslib.move.Move(from, to)
        board.doMove(move)
        board.fen
    }.getOrNull()
}
