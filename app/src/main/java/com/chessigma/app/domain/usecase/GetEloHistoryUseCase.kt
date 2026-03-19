package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.EloPoint
import com.chessigma.app.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEloHistoryUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    operator fun invoke(platform: String): Flow<List<EloPoint>> {
        return statsRepository.getEloHistory(platform)
    }
}
