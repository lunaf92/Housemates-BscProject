package x.lunacode.housemates.data.models

import java.util.*

data class Transaction(
    val id: String? = null,
    val beneficiaryId: String? = null,
    val beneficiaryUsername: String? = null,
    val payerId: String? = null,
    val payerUsername: String? = null,
    val amount: Double? = null,
    val description: String? = null,
    val groupId: String? = null,
    val timestamp: Date = Calendar.getInstance().time
)
