package x.lunacode.housemates.use_cases.login

import x.lunacode.housemates.data.repositories.AuthRepository

class AddUser(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        id: String,
        username: String,
        group: String
    ) = repository.addUserToFirestore(id, username, group)
}