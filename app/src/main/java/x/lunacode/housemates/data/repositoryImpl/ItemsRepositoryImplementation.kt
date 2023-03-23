package x.lunacode.housemates.data.repositoryImpl

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Item
import x.lunacode.housemates.data.repositories.ItemsRepository
import x.lunacode.housemates.util.Const

class ItemsRepositoryImplementation(
    db: FirebaseFirestore
) : ItemsRepository {

    private val inventoryRef = db.collection(Const.ITEM_COLLECTION)

    override suspend fun addItemToFirestore(
        name: String,
        quantity: Long,
        group: String
    ): Flow<Response<Void>> =
        flow {
            try {
                emit(Response.Loading)
                val itemId = inventoryRef.document().id
                val item = Item(
                    id = itemId,
                    name = name,
                    quantity = quantity,
                    group = group
                )
                val addition = inventoryRef.document(itemId).set(item).await()
                emit(Response.Success(addition))
            } catch (e: Exception) {
                emit(Response.Error(e.message ?: e.toString()))
            }
        }

    override suspend fun deleteItemFromFirestore(itemId: String) = flow {
        try {
            emit(Response.Loading)
            val deletion = inventoryRef.document(itemId).delete().await()
            emit(Response.Success(deletion))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override suspend fun incrementItemInFirestore(itemId: String) = flow {
        try {
            emit(Response.Loading)
            val incrementByOne = inventoryRef.document(itemId)
            incrementByOne.update("quantity", FieldValue.increment(1))
            Response.Success(incrementByOne)
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override suspend fun decrementItemInFirestore(itemId: String) = flow {
        try {
            emit(Response.Loading)
            val incrementByOne = inventoryRef.document(itemId)
            incrementByOne.update("quantity", FieldValue.increment(-1))
            Response.Success(incrementByOne)
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: e.toString()))
        }
    }

    override fun getItemsFromFirestore(groupId: String): Flow<Response<List<Item>>> = callbackFlow {
        val snapshotListener = inventoryRef.whereEqualTo("group", groupId).orderBy("name")
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val items = snapshot.toObjects(Item::class.java)
                    Response.Success(items)
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