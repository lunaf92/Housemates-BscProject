package x.lunacode.housemates.data.repositoryImpl

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Group
import x.lunacode.housemates.data.repositories.GroupRepository
import x.lunacode.housemates.util.Const

class GroupRepositoryImplementation(
    db: FirebaseFirestore
) : GroupRepository {

    private val groupsRef = db.collection(Const.GROUP_COLLECTION)

    override suspend fun addGroupToFirestore(name: String, groupId: String): Flow<Response<Void>> =
        flow {
            try {
                emit(Response.Loading)
                val group = Group(
                    id = groupId,
                    name = name
                )
                val addition = groupsRef.document(groupId).set(group).await()
                emit(Response.Success(addition))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: e.toString()))
            }
        }

    override suspend fun deleteGroupFromFirestore(groupId: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val deletion = groupsRef.document(groupId).delete().await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun getGroupsFromFirestore(): Flow<Response<List<Group>>> = callbackFlow {
        val snapshotListener = groupsRef.addSnapshotListener { snapshot, e ->
            val response = if (snapshot != null) {
                val groups = snapshot.toObjects(Group::class.java)
                Response.Success(groups)
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