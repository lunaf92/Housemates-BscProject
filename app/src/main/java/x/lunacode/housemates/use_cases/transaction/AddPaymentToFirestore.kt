package x.lunacode.housemates.use_cases.transaction

import x.lunacode.housemates.data.repositories.TransactionRepository

class AddPaymentToFirestore(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        description: String,
        beneficiaryId: String,
        amount: Double,
        payerId: String,
        groupId: String,
        beneficiaryUsername: String,
        payerUsername: String
    ) = repository.addPaymentToFirestore(
        description = description,
        beneficiaryId = beneficiaryId,
        amount = amount,
        payerId = payerId,
        groupId = groupId,
        payerUsername = payerUsername,
        beneficiaryUsername = beneficiaryUsername
    )
}