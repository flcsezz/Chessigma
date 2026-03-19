package com.chessigma.app.ui.puzzles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chessigma.app.data.local.PersonalPuzzleDao
import com.chessigma.app.data.local.PersonalPuzzleWithGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PuzzleViewModel @Inject constructor(
    personalPuzzleDao: PersonalPuzzleDao
) : ViewModel() {

    val personalPuzzles: StateFlow<List<PersonalPuzzleWithGame>> = personalPuzzleDao
        .getPuzzlesWithGames()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
