package x.lunacode.housemates.ui.screens.inventory

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
import x.lunacode.housemates.data.models.Item
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.use_cases.UseCases
import x.lunacode.housemates.util.Const
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val useCases: UseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val TAG = "InventoryVM"

    private var userId: String? = null

    var user by mutableStateOf(User(null))
    var group by mutableStateOf(Group())

    var itemsState by mutableStateOf<Response<List<Item>>>(Response.Loading)
    var isItemAddedState by mutableStateOf<Response<Void?>>(Response.Success(null))
    var isItemDeletedState by mutableStateOf<Response<Void?>>(Response.Success(null))
    var isItemQuantityIncreasedState by mutableStateOf<Response<Void?>>(Response.Success(null))
    var isItemQuantityDecreasedState by mutableStateOf<Response<Void?>>(Response.Success(null))
    var openDialogState = mutableStateOf(false)

    init {
        userId = savedStateHandle.get<String>("userId")
        getUserDetails()
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
                                group = document.data?.get("group") as String
                            )
                            getItems(user.group!!)
                            getGroupDetails(user.group!!)
                        }
                    } else {
                    }
                }
                .addOnFailureListener {
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

    private fun getItems(group: String) {
        viewModelScope.launch {
            useCases.getItems(group).collect {
                itemsState = it
            }
        }
    }

    fun addItem(name: String, quantity: Long) {
        viewModelScope.launch {
            useCases.addItem(name, quantity, user.group!!).collect {
                isItemAddedState = it
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            useCases.deleteItem(itemId).collect {
                isItemDeletedState = it
            }
        }
    }

    fun increaseItem(itemId: String) {
        viewModelScope.launch {
            useCases.increaseQty(itemId).collect {
                isItemQuantityIncreasedState = it
            }
        }
    }

    fun decreaseItem(itemId: String) {
        viewModelScope.launch {
            useCases.decreaseQty(itemId).collect {
                isItemQuantityDecreasedState = it
            }
        }
    }
}