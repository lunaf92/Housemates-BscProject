package x.lunacode.housemates.use_cases.transaction

import x.lunacode.housemates.data.repositories.TransactionRepository

class AddExpenseToFirestore(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        description: String,
        cost: Double,
        userId: String,
        groupId: String,
        username: String
    ) = repository.addExpenseToFirestore(
        description = description,
        cost = cost,
        userId = userId,
        groupId = groupId,
        username = username
    )
}