package x.lunacode.housemates.ui.screens.inventory

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.ui.components.DrawerComponent
import x.lunacode.housemates.ui.screens.MainViewModel
import x.lunacode.housemates.ui.screens.ScreenEvent
import x.lunacode.housemates.ui.screens.inventory.components.AddItemDialog
import x.lunacode.housemates.ui.screens.inventory.components.ItemCard
import x.lunacode.housemates.util.UiEvent


@Composable
fun InventoryScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    inventoryViewModel: InventoryViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    val user = inventoryViewModel.user
    val group = inventoryViewModel.group

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        mainViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    inventoryViewModel.openDialogState.value = true
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Items"
                )
            }
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            DrawerComponent(onClickItem = {
                mainViewModel.onEvent(ScreenEvent.SidePanelNav(route = it.route, id = user.id))
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

//        TopBarComponent(

        if (inventoryViewModel.openDialogState.value) {
            AddItemDialog()
        }

        when (val itemsResponse = inventoryViewModel.itemsState) {
            is Response.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            is Response.Success -> Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {

                    if (group.name != null) {
                        Row(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Inventory",
                                color = MaterialTheme.colors.secondaryVariant,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (itemsResponse.data.isEmpty()) {
                        Column {
                            Text(
                                text = "This is your main inventory screen."
                            )
                            Text(text = "Please add all the essential items you need in your household," +
                                    " so that you always have a handy shopping list when you need it!")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "WARNING: If this is your first access after the creation of the group," +
                                    " please restart the app before adding any items to the inventory.",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Red
                            )
                            Text(text = "Failing to do so might result in loss of items. We apologise for the inconvenience")
                        }
                    }
                    LazyColumn {
                        items(
                            items = itemsResponse.data
                        ) { item ->
                            ItemCard(item)
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val additionResponse = inventoryViewModel.isItemAddedState) {
                            is Response.Loading -> CircularProgressIndicator()
                            is Response.Success -> Unit
                            is Error -> Toast.makeText(
                                LocalContext.current,
                                additionResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
            is Response.Error -> {
                Toast.makeText(LocalContext.current, itemsResponse.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val deletionResponse = inventoryViewModel.isItemDeletedState) {
                is Response.Loading -> CircularProgressIndicator()
                is Response.Success -> Unit
                is Response.Error -> Toast.makeText(
                    LocalContext.current,
                    deletionResponse.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}