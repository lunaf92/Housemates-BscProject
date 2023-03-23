package x.lunacode.housemates.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import x.lunacode.housemates.data.AuthState
import x.lunacode.housemates.use_cases.UseCases
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val useCases: UseCases
) : ViewModel() {

    var loadingState by mutableStateOf(AuthState.IDLE)

    fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            useCases.loginWithGoogle(credential).collect {
                loadingState = it
            }
        }
    }
}
