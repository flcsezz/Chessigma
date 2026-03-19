package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.EloPoint
import com.chessigma.app.domain.repository.StatsRepository
import javax.inject.Inject

class UpdateEloUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(platform: String, newElo: Int) {
        val eloPoint = EloPoint(
            platform = platform,
            date = System.currentTimeMillis(),
            elo = newElo
        )
        statsRepository.addEloPoint(eloPoint)
    }
}
