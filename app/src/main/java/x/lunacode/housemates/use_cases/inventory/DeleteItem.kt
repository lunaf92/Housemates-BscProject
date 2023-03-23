package x.lunacode.housemates.use_cases.inventory

import x.lunacode.housemates.data.repositories.ItemsRepository

class DeleteItem(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(
        itemId: String
    ) = repository.deleteItemFromFirestore(itemId)
}