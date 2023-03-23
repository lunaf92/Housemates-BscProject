package x.lunacode.housemates.ui.screens.account

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
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.use_cases.UseCases
import x.lunacode.housemates.util.Const
import x.lunacode.housemates.util.generateGroupId
import javax.inject.Inject

@HiltViewModel
class AccountScreenViewModel @Inject constructor(
    val db: FirebaseFirestore,
    val useCases: UseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var userId: String? = null

    var user by mutableStateOf(User(null))
    var group by mutableStateOf(Group())

    var isUsernameChangedState by mutableStateOf<Response<Void?>>(Response.Loading)
    var isGroupChangedState by mutableStateOf<Response<Void?>>(Response.Loading)
    var isGroupCreatedState by mutableStateOf<Response<Void?>>(Response.Loading)

    val TAG = "AccountScreenViewModel"

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
                                group = document.data?.get("group") as String,
                                balance = document.data?.get("balance") as Double
                            )
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

    fun changeUsername(username: String) {
        viewModelScope.launch {
            useCases.addUser(user.id!!, username, group.id!!).collect { response ->
                isUsernameChangedState = response
            }
        }
    }

    fun changeGroup(newGroupCode: String) {
        viewModelScope.launch {
            val ref = db.collection(Const.GROUP_COLLECTION).document(newGroupCode)
            ref.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        viewModelScope.launch {
                            useCases.addUser(user.id!!, user.username!!, newGroupCode)
                                .collect { response ->
                                    isGroupChangedState = response
                                }
                        }
                    } else {
                        isGroupChangedState = Response.Error("group does not exist")
                    }
                }

        }
    }

    fun addAndChangeGroup(newGroupName: String) {
        var groupId = generateGroupId()
        db.collection(Const.GROUP_COLLECTION).document(groupId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // back with error
                } else {
                    viewModelScope.launch {
                        useCases.addGroup(newGroupName, groupId).collect { response ->
                            isGroupCreatedState = response
                        }

                        useCases.addUser(user.id!!, user.username!!, groupId).collect { response ->
                            isGroupChangedState = response
                        }
                    }
                }
            }
    }
}