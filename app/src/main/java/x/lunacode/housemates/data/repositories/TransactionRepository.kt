package x.lunacode.housemates.data.repositories

import kotlinx.coroutines.flow.Flow
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Transaction
import x.lunacode.housemates.data.models.User

interface TransactionRepository {

    suspend fun addPaymentToFirestore(
        beneficiaryId: String,
        beneficiaryUsername: String,
        payerId: String,
        payerUsername: String,
        amount: Double,
        description: String,
        groupId: String
    ): Flow<Response<Void>>

    suspend fun addExpenseToFirestore(
        description: String,
        cost: Double,
        userId: String,
        username: String,
        groupId: String
    ): Flow<Response<Void>>

    suspend fun removeTransactionToFirestore(id: String): Flow<Response<Transaction>>

    suspend fun changeUserOutstandingBalance(
        user: User,
        change: Double,
        add: Boolean = true
    ): Flow<Response<Void>>

    fun getUsersFromGroup(groupId: String): Flow<Response<List<User>>>
    fun getTransactionsFromFirestore(groupId: String): Flow<Response<List<Transaction>>>
}