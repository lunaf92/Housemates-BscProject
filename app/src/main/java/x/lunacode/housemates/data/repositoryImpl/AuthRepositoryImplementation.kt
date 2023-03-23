package x.lunacode.housemates.data.repositoryImpl

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import x.lunacode.housemates.data.AuthState
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.data.repositories.AuthRepository
import x.lunacode.housemates.util.Const

class AuthRepositoryImplementation(
    private val db: FirebaseFirestore
) : AuthRepository {

    private val userRef = db.collection(Const.USER_COLLECTION)

    override suspend fun signInWithGoogle(credential: AuthCredential): Flow<AuthState> = flow {
        try {
            emit(AuthState.LOADING)
            Firebase.auth.signInWithCredential(credential)
            emit(AuthState.LOADED)
        } catch (e: Exception) {
            emit(AuthState.error(e.message ?: e.toString()))
        }
    }

    override suspend fun addUserToFirestore(
        id: String,
        username: String,
        group: String
    ): Flow<Response<Void>> = flow {
        try {
            emit(Response.Loading)
            val user = User(
                id = id,
                username = username,
                group = group
            )
            val addition = userRef.document(id).set(user).await()
            emit(Response.Success(addition))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun getUserFromFirestore(id: String): Flow<Response<User>> = callbackFlow {
        val snapshotListener = userRef
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val user = snapshot.toObjects(User::class.java).first()
                    Response.Success(user)
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