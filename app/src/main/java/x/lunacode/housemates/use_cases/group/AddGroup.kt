package x.lunacode.housemates.use_cases.group

import x.lunacode.housemates.data.repositories.GroupRepository

class AddGroup(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(
        name: String,
        id: String
    ) = repository.addGroupToFirestore(name, id)
}