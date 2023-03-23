package x.lunacode.housemates.data.repositories

import kotlinx.coroutines.flow.Flow
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Item

interface ItemsRepository {

    suspend fun addItemToFirestore(
        name: String,
        quantity: Long,
        group: String
    ): Flow<Response<Void>>

    suspend fun deleteItemFromFirestore(itemId: String): Flow<Response<Void?>>

    suspend fun incrementItemInFirestore(itemId: String): Flow<Response<Void?>>
    suspend fun decrementItemInFirestore(itemId: String): Flow<Response<Void?>>

    fun getItemsFromFirestore(groupId: String): Flow<Response<List<Item>>>
}