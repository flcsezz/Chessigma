package com.chessigma.app.domain.model

data class GameState(
    val board: ChessBoard,
    val moveHistory: List<ChessMove> = emptyList(),
    val fenHistory: List<String> = emptyList(),
    val status: GameStatus = GameStatus.ONGOING
)
