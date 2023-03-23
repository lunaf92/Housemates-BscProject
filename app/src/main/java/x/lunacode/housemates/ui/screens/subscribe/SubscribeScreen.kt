package x.lunacode.housemates.ui.screens.subscribe

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.ui.screens.MainViewModel
import x.lunacode.housemates.ui.screens.ScreenEvent
import x.lunacode.housemates.util.UiEvent

@Composable
fun SubscribeScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    subscribeViewModel: SubscribeViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var groupname by remember { mutableStateOf("") }
    var groupcode by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var goTo by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val userId = FirebaseAuth.getInstance().uid


    LaunchedEffect(key1 = true) {
        mainViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
            }
        }
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.weight(0.2f)) {
                Text(
                    text = "Choose a name to display",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Text(
                    text = "Letters, numbers and space allowed",
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        subscribeViewModel.username = username
                    },
                    label = { Text(text = "Display name") }
                )
                if (error) {
                    Row {
                        Text(
                            text = errorMessage,
                            fontSize = 10.sp,
                            color = Color.Red
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier.padding(1.dp),
                thickness = 2.dp,
                color = MaterialTheme.colors.onSurface
            )

            Column(
                modifier = Modifier.weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                Text(
                    text = "Do you want to:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "Join an existing group")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(10.dp)
                        ) {
                            OutlinedTextField(
                                value = groupcode,
                                onValueChange = {
                                    groupcode = it
                                    subscribeViewModel.groupcode = it
                                },
                                label = { Text(text = "Enter the group code") }
                            )
                        }

                        Button(onClick = {
                            if (groupcode.isNotBlank()) {
                                if (userId != null) {
                                    subscribeViewModel.existGroupAddUserToFirestore(
                                        userId
                                    )
                                }
                                when (val user = subscribeViewModel.isUserAddedState) {
                                    is Response.Loading -> {
                                        error = true
                                        errorMessage = "Please try again a few seconds"
                                    }
                                    is Response.Success -> {
                                            mainViewModel.onEvent(ScreenEvent.OnUserSubscribed(userId))
                                    }
                                    is Response.Error -> {
                                        Log.d("SubscribeScreen", "error: ${user.message}")
                                    }
                                }
                            } else {
                                error = true
                                errorMessage = "Please fill username and group name"
                            }
                        }
                        ) {
                            Text(text = "Submit")
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = "create a new group")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .padding(10.dp)
                        ) {
                            OutlinedTextField(
                                value = groupname,
                                onValueChange = {
                                    groupname = it
                                    subscribeViewModel.groupname = it
                                },
                                label = { Text(text = "Enter the group name") }
                            )
                        }
                        Button(onClick = {
                            if (groupname.isNotBlank()) {
                                if (goTo){
                                    mainViewModel.onEvent(ScreenEvent.OnUserSubscribed(userId))
                                    goTo = true
                                }
                                    subscribeViewModel.newGroupAddUserToFirestore(
                                        userId!!
                                    )
                                when (val user = subscribeViewModel.isUserAddedState) {
                                    is Response.Loading -> {
                                        error = true
                                        errorMessage = "Please try again a few seconds"
                                    }
                                    is Response.Success -> {
                                        mainViewModel.onEvent(ScreenEvent.OnUserSubscribed(userId))
                                    }
                                    is Response.Error -> {
                                    }
                                }
                            } else {
                                error = true
                                errorMessage = "Please fill username and group name"
                            }
                        }
                        ) {
                            Text(text = "Submit")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

        }
    }
}


