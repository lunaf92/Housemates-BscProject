package x.lunacode.housemates.ui.screens.balance.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.User
import x.lunacode.housemates.ui.screens.balance.BalanceViewModel

@Composable
fun PayExpenseDialog(
    balanceViewModel: BalanceViewModel = hiltViewModel()
) {
    balanceViewModel.getAllUsersFromGroup(balanceViewModel.user.group!!)
    var user by remember { mutableStateOf(User()) }
    var description by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()

    if (balanceViewModel.openPayExpenseDialogState) {
        when (val users = balanceViewModel.usersInGroup) {
            is Response.Error -> {
            }
            Response.Loading -> CircularProgressIndicator()
            is Response.Success -> {
                // variables for the dropdown
                val usersList = users.data
                var expanded by remember { mutableStateOf(false) }
                var selectedUserName by remember { mutableStateOf("") }
                var textfieldSize by remember { mutableStateOf(Size.Zero) }
                val icon = if (expanded)
                    Icons.Filled.ArrowDropUp
                else
                    Icons.Filled.ArrowDropDown

                AlertDialog(
                    onDismissRequest = {
                        balanceViewModel.openPayExpenseDialogState = false
                    },
                    title = {
                        Text(text = "Pay Someone")
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
                            Spacer(modifier = Modifier.height(16.dp))


                            Column {
                                OutlinedTextField(
                                    value = selectedUserName,
                                    onValueChange = { selectedUserName = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onGloballyPositioned { coordinates ->
                                            //This value is used to assign to the DropDown the same width
                                            textfieldSize = coordinates.size.toSize()
                                        },
                                    label = { Text("Select User") },
                                    trailingIcon = {
                                        Icon(icon, "contentDescription",
                                            Modifier.clickable { expanded = !expanded })
                                    }
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
                                ) {
                                    usersList.forEach { selection ->
                                        DropdownMenuItem(onClick = {
                                            selectedUserName = selection.username!!
                                            user = selection
                                            expanded = false
                                        }) {
                                            Text(text = selection.username!!)
                                        }
                                    }
                                }
                            }

                            if (error) {
                                Text(text = "Both cost ands description must be filled!")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "All submitted transactions are final. Please ensure all details are correct before continue")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (description.isNotBlank() && cost.isNotBlank() && user.id != null) {
                                balanceViewModel.openPayExpenseDialogState = false
                                balanceViewModel.changeUserBalanceAndAddPaymentToFirestore(
                                    user,
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
                            balanceViewModel.openPayExpenseDialogState = false
                        }) {
                            Text(text = "Dismiss")
                        }
                    }
                )
            }
        }
    }
}

