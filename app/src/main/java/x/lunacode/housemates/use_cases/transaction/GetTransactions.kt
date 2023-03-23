package x.lunacode.housemates.use_cases.transaction

import x.lunacode.housemates.data.repositories.TransactionRepository

class GetTransactions(
    private val repository: TransactionRepository
) {
    operator fun invoke(groupId: String) =
        repository.getTransactionsFromFirestore(groupId = groupId)
}