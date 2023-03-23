package x.lunacode.housemates.data.repositoryImpl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Transaction
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.data.repositories.TransactionRepository
import x.lunacode.housemates.util.Const

class TransactionsRepositoryImplementation(
    val db: FirebaseFirestore
) : TransactionRepository {

    private val transactionsRef = db.collection(Const.TRANSACTIONS_COLLECTION)
    private val usersRef = db.collection(Const.USER_COLLECTION)

    override suspend fun addPaymentToFirestore(
        beneficiaryId: String,
        beneficiaryUsername: String,
        payerId: String,
        payerUsername: String,
        amount: Double,
        description: String,
        groupId: String
    ): Flow<Response<Void>> = flow {
        try {
            emit(Response.Loading)
            val transactionId = transactionsRef.document().id
            val transaction = Transaction(
                id = transactionId,
                beneficiaryId = beneficiaryId,
                description = description,
                amount = amount,
                payerId = payerId,
                groupId = groupId,
                beneficiaryUsername = beneficiaryUsername,
                payerUsername = payerUsername
            )
            val addition = transactionsRef.document(transactionId).set(transaction).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override suspend fun addExpenseToFirestore(
        description: String,
        cost: Double,
        userId: String,
        username: String,
        groupId: String
    ): Flow<Response<Void>> = flow {
        try {
            emit(Response.Loading)
            val transactionId = transactionsRef.document().id
            val transaction = Transaction(
                id = transactionId,
                description = description,
                amount = cost,
                payerId = userId,
                groupId = groupId,
                payerUsername = username
            )
            val addition = transactionsRef.document(transactionId).set(transaction).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override suspend fun removeTransactionToFirestore(id: String): Flow<Response<Transaction>> =
        flow {
            TODO("Not yet implemented")
        }

    override suspend fun changeUserOutstandingBalance(user: User, change: Double, add: Boolean) =
        flow {
            try {
                emit(Response.Loading)
                val user =
                    if (add) user.copy(balance = (user.balance + change)) else user.copy(balance = (user.balance - change))
                val addition = usersRef.document(user.id!!).set(user).await()
                emit(Response.Success(addition))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: e.toString()))
            }
        }

    override fun getUsersFromGroup(groupId: String): Flow<Response<List<User>>> = callbackFlow {
        val snapshotListener = usersRef
            .whereEqualTo("group", groupId)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val users = snapshot.toObjects(User::class.java)
                    Response.Success(users)
                } else {
                    Response.Error(e?.message ?: e.toString())
                }
                trySend(response).isSuccess
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    override fun getTransactionsFromFirestore(groupId: String): Flow<Response<List<Transaction>>> =
        callbackFlow {
            val snapshotListener = transactionsRef
                .whereEqualTo("groupId", groupId)
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val transactions = snapshot.toObjects(Transaction::class.java)
                        Response.Success(transactions)
                    } else {
                        Response.Error(e?.message ?: e.toString())
                    }
                    trySend(response).isSuccess
                }
            awaitClose {
                snapshotListener.remove()
            }
        }

}