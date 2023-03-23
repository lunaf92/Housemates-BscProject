package x.lunacode.housemates.ui.screens.balance.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import x.lunacode.housemates.data.models.Transaction
import x.lunacode.housemates.ui.screens.balance.BalanceViewModel

@Composable
fun ItemLine(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                       onClick()
            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = transaction.payerUsername ?: "username",
            modifier = Modifier.weight(0.33f),
            color = MaterialTheme.colors.onSurface
        )
        Text(
            text = transaction.beneficiaryUsername ?: "Group",
            modifier = Modifier.weight(0.33f),
            color = MaterialTheme.colors.onSurface
        )
        Text(
            text = transaction.amount.toString(),
            modifier = Modifier.weight(0.33f),
            color = MaterialTheme.colors.onSurface
        )
    }
}