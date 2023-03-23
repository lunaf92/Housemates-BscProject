package x.lunacode.housemates.use_cases.login

import com.google.firebase.auth.AuthCredential
import x.lunacode.housemates.data.repositories.AuthRepository

class LoginWithGoogle(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        credential: AuthCredential
    ) = repository.signInWithGoogle(credential)
}