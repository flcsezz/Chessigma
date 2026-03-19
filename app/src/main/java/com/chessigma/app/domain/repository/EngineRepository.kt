package com.chessigma.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface EngineRepository {
    val isReady: Flow<Boolean>
    
    suspend fun initialise(): Boolean
    suspend fun stop()
    
    suspend fun getBestMove(fen: String, depth: Int): String
    suspend fun evaluatePosition(fen: String, depth: Int): Int
}
