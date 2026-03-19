package com.chessigma.app.domain.repository

import com.chessigma.app.domain.model.EloPoint
import kotlinx.coroutines.flow.Flow

interface StatsRepository {
    fun getEloHistory(platform: String): Flow<List<EloPoint>>
    suspend fun addEloPoint(eloPoint: EloPoint)
}
