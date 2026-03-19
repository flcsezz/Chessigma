package com.chessigma.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessigma.app.domain.model.EloPoint
import com.chessigma.app.domain.usecase.GetEloHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    getEloHistoryUseCase: GetEloHistoryUseCase
) : ViewModel() {

    val eloHistory: StateFlow<List<EloPoint>> = getEloHistoryUseCase("LOCAL")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
