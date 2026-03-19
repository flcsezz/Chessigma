package com.chessigma.app.domain.usecase

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Square
import javax.inject.Inject

class GetLegalMovesUseCase @Inject constructor() {
    
    operator fun invoke(fen: String, fromSquareName: String): List<String> {
        val board = Board()
        board.loadFromFen(fen)
        
        val fromSquare = Square.fromValue(fromSquareName.uppercase())
        
        return board.legalMoves()
            .filter { it.from == fromSquare }
            .map { it.to.value().lowercase() }
    }
}
