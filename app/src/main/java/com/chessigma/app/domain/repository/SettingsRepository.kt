package com.chessigma.app.domain.repository

import com.chessigma.app.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getUserSettings(): Flow<UserSettings>
    suspend fun updateSettings(settings: UserSettings)
    suspend fun updateApiKey(provider: String, key: String)
}
