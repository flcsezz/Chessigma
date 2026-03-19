package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.GameStatus
import com.github.bhlangonijr.chesslib.Board
import javax.inject.Inject

class GetGameStatusUseCase @Inject constructor() {

    operator fun invoke(fen: String): GameStatus {
        val board = Board()
        board.loadFromFen(fen)

        return when {
            board.isMated -> GameStatus.CHECKMATE
            board.isStaleMate -> GameStatus.STALEMATE
            board.isDraw -> GameStatus.DRAW
            else -> GameStatus.ONGOING
        }
    }
}
