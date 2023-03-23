package x.lunacode.housemates.data.repositories

import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import x.lunacode.housemates.data.AuthState
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.User

interface AuthRepository {
    suspend fun signInWithGoogle(credential: AuthCredential): Flow<AuthState>

    // Different Repository maybe?
    suspend fun addUserToFirestore(
        id: String,
        username: String,
        group: String
    ): Flow<Response<Void>>

    fun getUserFromFirestore(id: String): Flow<Response<User>>
}