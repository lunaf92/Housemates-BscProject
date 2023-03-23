package x.lunacode.housemates.data.repositories

import kotlinx.coroutines.flow.Flow
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Group

interface GroupRepository {

    suspend fun addGroupToFirestore(groupId: String, name: String): Flow<Response<Void>>

    // not priority ATM
    suspend fun deleteGroupFromFirestore(groupId: String): Flow<Response<Void?>>

    // doubtful that this will come in useful
    fun getGroupsFromFirestore(): Flow<Response<List<Group>>>
}