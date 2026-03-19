package com.chessigma.app.domain.usecase

import com.chessigma.app.domain.model.UserSettings
import com.chessigma.app.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: UserSettings) {
        settingsRepository.updateSettings(settings)
    }
}
