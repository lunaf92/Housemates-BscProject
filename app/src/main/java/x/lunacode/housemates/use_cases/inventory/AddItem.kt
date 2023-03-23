package x.lunacode.housemates.use_cases.inventory

import x.lunacode.housemates.data.repositories.ItemsRepository

class AddItem(
    private val repository: ItemsRepository
) {
    suspend operator fun invoke(
        name: String,
        quantity: Long,
        group: String
    ) = repository.addItemToFirestore(name, quantity, group)
}