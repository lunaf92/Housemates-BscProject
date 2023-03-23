package x.lunacode.housemates.use_cases.group

import x.lunacode.housemates.data.repositories.GroupRepository

class GetGroups(
    private val repository: GroupRepository
) {
    operator fun invoke() = repository.getGroupsFromFirestore()
}