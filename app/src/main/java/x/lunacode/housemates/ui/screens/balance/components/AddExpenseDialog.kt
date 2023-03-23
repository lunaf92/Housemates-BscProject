package x.lunacode.housemates.ui.screens.balance.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import x.lunacode.housemates.ui.screens.balance.BalanceViewModel


@Composable
fun AddExpenseDialog(
    balanceViewModel: BalanceViewModel = hiltViewModel()
) {
    var description by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()

    if (balanceViewModel.openAddExpenseDialogState) {
        AlertDialog(
            onDismissRequest = {
                balanceViewModel.openAddExpenseDialogState = false
            },
            title = {
                Text(text = "Add Expense")
            },
            text = {
                Column {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Description") },
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                    DisposableEffect(Unit) {
                        focusRequester.requestFocus()
                        onDispose { }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = cost,
                        onValueChange = { value ->
                            cost = value
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        placeholder = { Text(text = "Cost") }
                    )
                    if (error) {
                        Text(text = "Both cost ands description must be filled!")
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "All submitted transactions are final. Please ensure all details are correct before continue")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (description.isNotBlank() && cost.isNotBlank()) {
                        balanceViewModel.openAddExpenseDialogState = false
                        balanceViewModel.changeUserBalanceAndAddExpenseToFirestore(
                            description,
                            cost.toDouble()
                        )
                    } else {
                        error = true
                    }
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    balanceViewModel.openAddExpenseDialogState = false
                }) {
                    Text(text = "Dismiss")
                }
            }
        )
    }
}

