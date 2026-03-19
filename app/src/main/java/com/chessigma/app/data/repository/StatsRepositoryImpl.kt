package com.chessigma.app.data.repository

import com.chessigma.app.data.local.EloHistoryDao
import com.chessigma.app.data.local.EloHistoryEntity
import com.chessigma.app.domain.model.EloPoint
import com.chessigma.app.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val eloHistoryDao: EloHistoryDao
) : StatsRepository {

    override fun getEloHistory(platform: String): Flow<List<EloPoint>> {
        return eloHistoryDao.getEloHistory().map { entities ->
            entities.filter { it.platform == platform }.map {
                EloPoint(
                    platform = it.platform,
                    date = it.date,
                    elo = it.elo
                )
            }
        }
    }

    override suspend fun addEloPoint(eloPoint: EloPoint) {
        eloHistoryDao.insertElo(
            EloHistoryEntity(
                platform = eloPoint.platform,
                date = eloPoint.date,
                elo = eloPoint.elo
            )
        )
    }
}
