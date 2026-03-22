package com.chessigma.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessigma.app.domain.model.BoardTheme
import com.chessigma.app.domain.model.PieceSet
import com.chessigma.app.domain.model.UserSettings
import com.chessigma.app.domain.usecase.GetSettingsUseCase
import com.chessigma.app.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    val settings: StateFlow<UserSettings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    fun updateGeminiKey(key: String) {
        viewModelScope.launch {
            updateSettingsUseCase(settings.value.copy(geminiApiKey = key))
        }
    }

    fun updateGroqKey(key: String) {
        viewModelScope.launch {
            updateSettingsUseCase(settings.value.copy(groqApiKey = key))
        }
    }

    fun updateNvidiaKey(key: String) {
        viewModelScope.launch {
            updateSettingsUseCase(settings.value.copy(nvidiaApiKey = key))
        }
    }

    fun setPreferredProvider(provider: String) {
        viewModelScope.launch {
            updateSettingsUseCase(settings.value.copy(preferredAiProvider = provider))
        }
    }

    fun updateBoardTheme(theme: BoardTheme) {
        viewModelScope.launch {
            updateSettingsUseCase(settings.value.copy(boardTheme = theme))
        }
    }

    fun updatePieceSet(pieceSet: PieceSet) {
        viewModelScope.launch {
            updateSettingsUseCase(settings.value.copy(pieceSet = pieceSet))
        }
    }
}
