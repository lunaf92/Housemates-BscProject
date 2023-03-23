package x.lunacode.housemates.use_cases.inventory

import x.lunacode.housemates.data.repositories.ItemsRepository

class IncreaseQty(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(
        itemId: String
    ) = repository.incrementItemInFirestore(itemId)
}