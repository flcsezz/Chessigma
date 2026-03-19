package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.UserSettings
import com.chessigma.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<UserSettings> {
        return settingsRepository.getUserSettings()
    }
}
