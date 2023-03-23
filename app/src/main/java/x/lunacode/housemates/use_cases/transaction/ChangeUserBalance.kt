package x.lunacode.housemates.use_cases.transaction

import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.data.repositories.TransactionRepository

class ChangeUserBalance(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        user: User,
        change: Double,
        add: Boolean
    ) = repository.changeUserOutstandingBalance(user, change, add)
}