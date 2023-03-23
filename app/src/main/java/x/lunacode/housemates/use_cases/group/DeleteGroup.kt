package x.lunacode.housemates.use_cases.group

import x.lunacode.housemates.data.repositories.GroupRepository

class DeleteGroup(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        groupId: String
    ) = repository.deleteGroupFromFirestore(groupId)
}