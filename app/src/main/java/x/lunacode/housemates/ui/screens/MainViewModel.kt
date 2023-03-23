package x.lunacode.housemates.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.use_cases.UseCases
import x.lunacode.housemates.util.Const
import x.lunacode.housemates.util.Routes
import x.lunacode.housemates.util.UiEvent
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val useCases: UseCases
) : ViewModel() {

    val TAG = "MainViewModel"

    var user by mutableStateOf(User())

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event: ScreenEvent) {
        when (event) {
            is ScreenEvent.OnSuccessLogin -> {
                val ref = db.collection(Const.USER_COLLECTION).document(event.id ?: "")
                ref.get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            runBlocking {
                                _uiEvent.send(UiEvent.Navigate(Routes.GROUP_INVENTORY + "?userId=${event.id}"))
                            }
                        } else {
                            runBlocking {
                                _uiEvent.send(UiEvent.Navigate(Routes.REGISTER_USER))
                            }
                        }
                    }
            }
            is ScreenEvent.OnUserSubscribed -> {
                runBlocking {
                    _uiEvent.send(UiEvent.Navigate(Routes.GROUP_INVENTORY + "?userId=${event.id}"))
                }
            }
            is ScreenEvent.SidePanelNav -> {
                if (event.route == Routes.ACCOUNT_SCREEN) {
                    runBlocking {

                        _uiEvent.send(UiEvent.Navigate(event.route + "?userId=${event.id}"))
                    }
                }
                if (event.route == Routes.GROUP_INVENTORY) {
                    runBlocking {
                        _uiEvent.send(UiEvent.Navigate(event.route + "?userId=${event.id}"))
                    }
                }
                if (event.route == Routes.GROUP_BALANCE) {
                    runBlocking {
                        _uiEvent.send(UiEvent.Navigate(event.route + "?userId=${event.id}"))
                    }
                }
            }
        }
    }

}