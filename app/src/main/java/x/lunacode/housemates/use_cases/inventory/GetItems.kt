package x.lunacode.housemates.use_cases.inventory

import x.lunacode.housemates.data.repositories.ItemsRepository

class GetItems(
    private val repository: ItemsRepository
) {
    operator fun invoke(groupId: String) = repository.getItemsFromFirestore(groupId)
}