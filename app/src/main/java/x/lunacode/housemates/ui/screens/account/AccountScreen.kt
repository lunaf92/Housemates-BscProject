package x.lunacode.housemates.ui.screens.account

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import x.lunacode.housemates.ui.components.DrawerComponent
import x.lunacode.housemates.ui.screens.MainViewModel
import x.lunacode.housemates.ui.screens.ScreenEvent
import x.lunacode.housemates.util.UiEvent

@Composable
fun AccountScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    accountScreenViewModel: AccountScreenViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {

    val user = accountScreenViewModel.user
    val group = accountScreenViewModel.group

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    var changingUsername by remember { mutableStateOf(false) }
    var changingGroup by remember { mutableStateOf(false) }
    var startingGroup by remember { mutableStateOf(false) }
    var usernameToChange by remember { mutableStateOf("") }
    var newGroupCode by remember { mutableStateOf("") }
    var newGroupName by remember { mutableStateOf("") }

    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hi, paste this code into the Housemates App to join my group: \n \n ${group.id}"
        )
        type = "text/*"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        mainViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            DrawerComponent(onClickItem = {
                mainViewModel.onEvent(ScreenEvent.SidePanelNav(it.route, user.id))
            },
                onClickExit = {
                    coroutineScope.launch { scaffoldState.drawerState.close() }
                })
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        modifier = Modifier.clickable(onClick = {
                            coroutineScope.launch { scaffoldState.drawerState.open() }
                        }),
                        contentDescription = ""
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp)),
                title = { Text(text = "${group.name?.uppercase()}") }
            )
        },
    ) {
        if (user.username == "") {
            CircularProgressIndicator()
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!changingUsername) {
                        Text(
                            text = "Welcome, ${user.username ?: ""}"
                        )
                        OutlinedButton(
                            onClick = {
                                changingUsername = true
                            }
                        ) {
                            Text(text = "changing username?")
                        }
                    } else {
                        OutlinedTextField(
                            value = usernameToChange,
                            onValueChange = { usernameToChange = it }
                        )
                        OutlinedButton(onClick = {
                            accountScreenViewModel.changeUsername(usernameToChange)
                            changingUsername = false
                            Toast.makeText(
                                context,
                                "Changes will be visible the next time you'll visit this page",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                            Text(text = "submit?")
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = 16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your current outstanding balance is: ${user.balance}"
                    )
                }

                Spacer(modifier = Modifier.padding(vertical = 16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Your current group is: ${group.name}")
                    Button(onClick = {
                        context.startActivity(shareIntent)
                    }) {
                        Text(text = "share group code")
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Or")
                            Text(text = "Leave the group and")
                        }
                        if (!changingGroup && !startingGroup) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = { changingGroup = true }) {
                                    Text(text = "Join another group")
                                }
                                Button(onClick = { startingGroup = true }) {
                                    Text(text = "Create another group")
                                }
                            }
                        }
                        if (changingGroup) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                OutlinedTextField(
                                    value = newGroupCode,
                                    onValueChange = { newGroupCode = it },
                                    label = {
                                        Text(text = "New group's code")
                                    }
                                )
                                Button(onClick = {
                                    accountScreenViewModel.changeGroup(newGroupCode)
                                    changingGroup = false
                                }) {
                                    Text(text = "Submit")
                                }
                            }
                        }
                        if (startingGroup) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                OutlinedTextField(
                                    value = newGroupName,
                                    onValueChange = { newGroupName = it },
                                    label = {
                                        Text(text = "New group's name")
                                    }
                                )
                                Button(onClick = {
                                    accountScreenViewModel.addAndChangeGroup(
                                        newGroupName
                                    )
                                }) {
                                    Text(text = "Submit")
                                }
                            }
                        }

                        when (accountScreenViewModel.isGroupChangedState) {
                            is x.lunacode.housemates.data.Response.Error -> {
                                Toast.makeText(context, "Group does not exist", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            x.lunacode.housemates.data.Response.Loading -> {}
                            is x.lunacode.housemates.data.Response.Success -> {
                                Toast.makeText(
                                    context,
                                    "Changes will be visible the next time you'll visit this page",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}