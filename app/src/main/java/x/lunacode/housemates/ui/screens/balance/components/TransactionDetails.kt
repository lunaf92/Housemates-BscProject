package x.lunacode.housemates.ui.screens.balance.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import x.lunacode.housemates.data.models.Transaction
import x.lunacode.housemates.ui.screens.balance.BalanceViewModel

@Composable
fun TransactionDetails(
    transaction: Transaction,
    balanceViewModel: BalanceViewModel = hiltViewModel()
){

    if (balanceViewModel.transactionDetailsDialogOpen) {
        AlertDialog(
            onDismissRequest = {
                balanceViewModel.transactionDetailsDialogOpen = false
            },
            title = {
                Text(text = "Details")
            },
            text = {
                   Column() {
                       Text(text = "From: ${transaction.payerUsername}")
                       Text(text = "To: ${transaction.beneficiaryUsername ?: "Group Expense"}")
                       Text(text = "£££: ${transaction.amount}")
                       Text(text = "What: ${transaction.description}")
                       Text(text = "When: ${transaction.timestamp}")

                   }
            },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { balanceViewModel.transactionDetailsDialogOpen = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        )
    }
}