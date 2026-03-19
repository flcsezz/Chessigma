package com.chessigma.app.data.repository

import com.chessigma.app.domain.repository.AuthRepository
import com.chessigma.app.domain.repository.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseAuthRepositoryImpl @Inject constructor() : AuthRepository {
    
    // Placeholder for actual Supabase client
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    override suspend fun signInWithEmail(email: String, password: String) {
        // TODO: Implement Supabase signIn
        _currentUser.value = User("mock-id", email)
    }

    override suspend fun signUpWithEmail(email: String, password: String) {
        // TODO: Implement Supabase signUp
    }

    override suspend fun signOut() {
        // TODO: Implement Supabase signOut
        _currentUser.value = null
    }
}
