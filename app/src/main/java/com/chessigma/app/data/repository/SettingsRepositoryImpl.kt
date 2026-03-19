package com.chessigma.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.chessigma.app.domain.model.UserSettings
import com.chessigma.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val GROQ_API_KEY = stringPreferencesKey("groq_api_key")
        val NVIDIA_API_KEY = stringPreferencesKey("nvidia_api_key")
        val PREFERRED_AI_PROVIDER = stringPreferencesKey("preferred_ai_provider")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val STOCKFISH_DEPTH = intPreferencesKey("stockfish_depth")
    }

    override fun getUserSettings(): Flow<UserSettings> {
        return dataStore.data.map { preferences ->
            UserSettings(
                geminiApiKey = preferences[PreferencesKeys.GEMINI_API_KEY] ?: "",
                groqApiKey = preferences[PreferencesKeys.GROQ_API_KEY] ?: "",
                nvidiaApiKey = preferences[PreferencesKeys.NVIDIA_API_KEY] ?: "",
                preferredAiProvider = preferences[PreferencesKeys.PREFERRED_AI_PROVIDER] ?: "GEMINI",
                isDarkMode = preferences[PreferencesKeys.DARK_MODE] ?: true,
                stockfishDepth = preferences[PreferencesKeys.STOCKFISH_DEPTH] ?: 12
            )
        }
    }

    override suspend fun updateSettings(settings: UserSettings) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_API_KEY] = settings.geminiApiKey
            preferences[PreferencesKeys.GROQ_API_KEY] = settings.groqApiKey
            preferences[PreferencesKeys.NVIDIA_API_KEY] = settings.nvidiaApiKey
            preferences[PreferencesKeys.PREFERRED_AI_PROVIDER] = settings.preferredAiProvider
            preferences[PreferencesKeys.DARK_MODE] = settings.isDarkMode
            preferences[PreferencesKeys.STOCKFISH_DEPTH] = settings.stockfishDepth
        }
    }

    override suspend fun updateApiKey(provider: String, key: String) {
        dataStore.edit { preferences ->
            val prefKey = when (provider.uppercase()) {
                "GEMINI" -> PreferencesKeys.GEMINI_API_KEY
                "GROQ" -> PreferencesKeys.GROQ_API_KEY
                "NVIDIA" -> PreferencesKeys.NVIDIA_API_KEY
                else -> throw IllegalArgumentException("Unknown provider: $provider")
            }
            preferences[prefKey] = key
        }
    }
}
