package x.lunacode.housemates.use_cases.login

import x.lunacode.housemates.data.repositories.AuthRepository

class GetUser(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        id: String
    ) = repository.getUserFromFirestore(id)
}