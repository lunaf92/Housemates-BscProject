package x.lunacode.housemates.ui.screens.inventory.components

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
import x.lunacode.housemates.ui.screens.inventory.InventoryViewModel

@Composable
fun AddItemDialog(
    viewModel: InventoryViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()

    if (viewModel.openDialogState.value) {
        AlertDialog(
            onDismissRequest = {
                viewModel.openDialogState.value = false
            },
            title = {
                Text(text = "Add Item")
            },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Add Item") },
                        modifier = Modifier.focusRequester(focusRequester)
                    )
                    DisposableEffect(Unit) {
                        focusRequester.requestFocus()
                        onDispose { }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = quantity,
                        onValueChange = { value ->
                            quantity = value.filter { it.isDigit() }
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        placeholder = { Text(text = "Quantity") }
                    )
                    if (error) {
                        Text(text = "name and quantity must be filled!")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && quantity.isNotBlank()) {
                        viewModel.openDialogState.value = false
                        viewModel.addItem(name, quantity.toLong())
                    } else {
                        error = true
                    }
                }) {
                    Text(text = "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.openDialogState.value = false
                }) {
                    Text(text = "Dismiss")
                }
            }
        )
    }
}

