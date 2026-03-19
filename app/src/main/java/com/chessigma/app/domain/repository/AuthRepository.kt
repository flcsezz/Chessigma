package com.chessigma.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String)
    suspend fun signUpWithEmail(email: String, password: String)
    suspend fun signOut()
}

data class User(
    val id: String,
    val email: String?
)
