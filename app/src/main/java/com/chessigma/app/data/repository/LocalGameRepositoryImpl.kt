package com.chessigma.app.data.repository

import com.chessigma.app.data.local.GameDao
import com.chessigma.app.data.local.GameEntity
import com.chessigma.app.data.local.MoveDao
import com.chessigma.app.data.local.MoveEntity
import com.chessigma.app.domain.model.ChessMove
import com.chessigma.app.domain.repository.LocalGameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalGameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao,
    private val moveDao: MoveDao
) : LocalGameRepository {

    override suspend fun createGame(gameId: String, startedAt: Long) {
        val entity = GameEntity(
            id = gameId,
            platform = "LOCAL",
            pgn = "",
            whiteName = "Player",
            blackName = "Opponent",
            whiteElo = null,
            blackElo = null,
            result = "*", // In-progress marker (PGN convention)
            datePlayed = startedAt,
            openingEco = null,
            openingName = null,
            accuracyWhite = null,
            accuracyBlack = null,
            isAnalysed = false,
            userColor = "WHITE"
        )
        gameDao.insertGame(entity)
    }

    override suspend fun saveMove(
        gameId: String,
        ply: Int,
        move: ChessMove,
        fenBefore: String
    ) {
        val uci = buildUci(move)
        val entity = MoveEntity(
            gameId = gameId,
            ply = ply,
            san = move.san ?: uci,
            uci = uci,
            fenBefore = fenBefore,
            evalCpBefore = null,
            evalCpAfter = null,
            classification = "UNCLASSIFIED",
            bestUci = null,
            bestSan = null
        )
        moveDao.insertMove(entity)
    }

    override suspend fun finalizeGame(gameId: String, result: String) {
        gameDao.updateResult(gameId, result)
    }

    override suspend fun loadGame(gameId: String): Pair<GameEntity, List<MoveEntity>>? {
        val game = gameDao.getGameById(gameId) ?: return null
        val moves = moveDao.getMovesForGameSync(gameId)
        return Pair(game, moves)
    }

    override fun getRecentGames(): Flow<List<GameEntity>> = gameDao.getAllGames()

    override suspend fun updateMoveReview(
        gameId: String,
        ply: Int,
        evalBefore: Int,
        evalAfter: Int,
        classification: String,
        bestUci: String?,
        bestSan: String?
    ) {
        moveDao.updateMoveReview(gameId, ply, evalBefore, evalAfter, classification, bestUci, bestSan)
    }

    override suspend fun markGameAnalysed(gameId: String) {
        gameDao.markAnalysed(gameId)
    }

    override suspend fun updateAccuracy(gameId: String, whiteAccuracy: Float, blackAccuracy: Float) {
        gameDao.updateAccuracy(gameId, whiteAccuracy, blackAccuracy)
    }

    // ── private helpers ────────────────────────────────────────────────────────

    private fun buildUci(move: ChessMove): String {
        val base = move.fromSquare + move.toSquare
        return if (move.promotionPiece != null) {
            base + move.promotionPiece.name.first().lowercaseChar()
        } else {
            base
        }
    }
}
