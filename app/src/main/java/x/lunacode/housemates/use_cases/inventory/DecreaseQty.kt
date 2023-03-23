package x.lunacode.housemates.use_cases.inventory

import x.lunacode.housemates.data.repositories.ItemsRepository

class DecreaseQty(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(
        itemId: String
    ) = repository.decrementItemInFirestore(itemId)
}