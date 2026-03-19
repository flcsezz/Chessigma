package com.chessigma.app.data.repository

import com.chessigma.app.domain.repository.EngineRepository
import com.chessigma.app.engine.StockfishEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EngineRepositoryImpl @Inject constructor(
    private val stockfishEngine: StockfishEngine
) : EngineRepository {

    private val _isReady = MutableStateFlow(false)
    override val isReady = _isReady.asStateFlow()

    override suspend fun initialise(): Boolean {
        val success = stockfishEngine.initialise()
        _isReady.value = success
        return success
    }

    override suspend fun stop() {
        stockfishEngine.shutdown()
        _isReady.value = false
    }

    override suspend fun getBestMove(fen: String, depth: Int): String {
        stockfishEngine.setPosition(fen)
        return stockfishEngine.getBestMove(depth)
    }

    override suspend fun evaluatePosition(fen: String, depth: Int): Int {
        return stockfishEngine.evaluate(fen, depth)
    }
}
