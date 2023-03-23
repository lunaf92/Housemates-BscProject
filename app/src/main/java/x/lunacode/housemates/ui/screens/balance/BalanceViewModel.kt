package x.lunacode.housemates.ui.screens.balance

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Group
import x.lunacode.housemates.data.models.Transaction
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.use_cases.UseCases
import x.lunacode.housemates.util.Const
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    val db: FirebaseFirestore,
    val useCases: UseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val TAG = "BalanceViewModel"
    private var userId: String? = null

    var user by mutableStateOf(User(null))
    var group by mutableStateOf(Group())
    var usersInGroup by mutableStateOf<Response<List<User>>>(Response.Loading)
    var transactionsState by mutableStateOf<Response<List<Transaction>>>(Response.Loading)
    var openAddExpenseDialogState by mutableStateOf(false)
    var openPayExpenseDialogState by mutableStateOf(false)
    var isBalanceChangedState by mutableStateOf<Response<Void?>>(Response.Success(null))
    var isTransactionAddedState by mutableStateOf<Response<Void?>>(Response.Success(null))
    var transactionDetailsDialogOpen by mutableStateOf(false)

    init {
        userId = savedStateHandle.get<String>("userId")
        getUserDetails()
    }

    fun getAllUsersFromGroup(groupId: String) {
        viewModelScope.launch {
            useCases.getUsersInGroup(groupId = groupId).collect {
                usersInGroup = it
            }
        }
    }

    private fun getUserDetails() {
        viewModelScope.launch {

            val ref = db.collection(Const.USER_COLLECTION).document(userId!!)
            ref.get()
                .addOnSuccessListener { document ->

                    if (document.exists()) {

                        runBlocking {
                            user = User(
                                id = userId,
                                username = document.data?.get("username") as String,
                                group = document.data?.get("group") as String,
                                balance = document.data?.get("balance") as Double
                            )
                            getGroupDetails(user.group!!)
                            getTransactionsFromFirestore(user.group!!)
                        }
                    } else {
                        Log.d(TAG, "user does not exist")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "Error")
                }
        }
    }

    private fun getGroupDetails(groupId: String) {
        viewModelScope.launch {
            val ref = db.collection(Const.GROUP_COLLECTION).document(groupId)
            ref.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        runBlocking {
                            group = Group(
                                id = document.id,
                                name = document.data?.get("name") as String
                            )
                        }
                    } else {
                        Log.d(TAG, "group does not exist")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, it.message ?: it.toString())
                }
        }
    }

    fun changeUserBalanceAndAddPaymentToFirestore(
        beneficiary: User,
        description: String,
        amount: Double
    ) {
        viewModelScope.launch {
            useCases.changeUserBalance(
                user = beneficiary,
                change = amount,
                add = false
            ).collect {
                isBalanceChangedState = it
            }
            useCases.addPaymentToFirestore(
                amount = amount,
                beneficiaryId = beneficiary.id!!,
                description = description,
                payerId = userId!!,
                groupId = group.id!!,
                payerUsername = user.username!!,
                beneficiaryUsername = beneficiary.username!!
            ).collect {
                isTransactionAddedState = it
            }
        }
    }

    fun changeUserBalanceAndAddExpenseToFirestore(description: String, change: Double) {
        viewModelScope.launch {
            useCases.changeUserBalance(
                user = user,
                change = change,
                add = true
            ).collect {
                isBalanceChangedState = it
            }
            useCases.addExpenseToFirestore(
                description = description,
                cost = change,
                userId = userId!!,
                groupId = user.group!!,
                username = user.username!!
            ).collect {
                isTransactionAddedState = it
            }
        }
    }

    private fun getTransactionsFromFirestore(groupId: String) {
        viewModelScope.launch {
            useCases.getTransactions(groupId).collect {
                transactionsState = it
            }
        }
    }

}