package x.lunacode.housemates.ui.screens.subscribe

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.use_cases.UseCases
import x.lunacode.housemates.util.Const
import x.lunacode.housemates.util.generateGroupId
import javax.inject.Inject

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    val db: FirebaseFirestore,
    private val useCases: UseCases
) : ViewModel() {

    var groupname by mutableStateOf("")
    var groupcode by mutableStateOf("")
    var username = ""
    var isUserAddedState by mutableStateOf<Response<Void?>>(Response.Loading)
    var isGroupAddedState by mutableStateOf<Response<Void?>>(Response.Loading)

    fun newGroupAddUserToFirestore(
        userId: String,
    ) {
        var groupId = generateGroupId()
        var groupNotExist = true

        // check the Firestore Database to see if the id is already in use, otherwise generate a new one

        viewModelScope.launch {
            db.collection(Const.GROUP_COLLECTION).document(groupId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // back with error
                    } else {
                        viewModelScope.launch {
                            useCases.addGroup(groupname, groupId).collect { response ->
                                isGroupAddedState = response
                            }

                            useCases.addUser(userId, username, groupId).collect { response ->
                                isUserAddedState = response
                            }
                        }
                    }
                }
        }
    }

    fun existGroupAddUserToFirestore(
        userId: String,
    ) {

        db.collection(Const.GROUP_COLLECTION).document(groupcode).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    viewModelScope.launch {
                        useCases.addUser(userId, username, groupcode).collect { response ->
                            isUserAddedState = response
                        }
                    }
                }
            }
    }


}
