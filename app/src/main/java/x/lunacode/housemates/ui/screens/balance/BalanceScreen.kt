package x.lunacode.housemates.ui.screens.balance

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import x.lunacode.housemates.R
import x.lunacode.housemates.data.Response
import x.lunacode.housemates.data.models.Transaction
import x.lunacode.housemates.ui.components.DrawerComponent
import x.lunacode.housemates.ui.screens.MainViewModel
import x.lunacode.housemates.ui.screens.ScreenEvent
import x.lunacode.housemates.ui.screens.balance.components.AddExpenseDialog
import x.lunacode.housemates.ui.screens.balance.components.ItemLine
import x.lunacode.housemates.ui.screens.balance.components.PayExpenseDialog
import x.lunacode.housemates.ui.screens.balance.components.TransactionDetails
import x.lunacode.housemates.util.UiEvent

enum class MultiFabState {
    COLLAPSED, EXPANDED
}

enum class Identifier {
    Send,
    Add
}

class MinFabItem(
    val icon: ImageBitmap,
    val label: String,
    val identifier: String
)

@Composable
fun BalanceScreen(
    balanceViewModel: BalanceViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {

    var fabState by remember { mutableStateOf(MultiFabState.COLLAPSED) }

    val items = listOf(
        MinFabItem(
            icon = ImageBitmap.imageResource(id = R.drawable.outline_call_received_black_18),
            label = "Add",
            identifier = Identifier.Add.name
        ),
        MinFabItem(
            icon = ImageBitmap.imageResource(id = R.drawable.outline_send_black_18),
            label = "Send",
            identifier = Identifier.Send.name
        )
    )

    val user = balanceViewModel.user
    val group = balanceViewModel.group

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var currentTransactionDetails by remember{ mutableStateOf(Transaction()) }
    
    LaunchedEffect(key1 = true) {
        mainViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            MultiFloatingActionButton(
                fabState = fabState,
                onFabStateChange = { fabState = it },
                items = items
            )
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
        if (balanceViewModel.openAddExpenseDialogState) {
            AddExpenseDialog()
        }
        if (balanceViewModel.openPayExpenseDialogState) {
            PayExpenseDialog()
        }
        if (balanceViewModel.transactionDetailsDialogOpen){
            TransactionDetails(transaction = currentTransactionDetails)
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                when (val transactionResponse = balanceViewModel.transactionsState) {
                    is Response.Error -> {
                        Toast.makeText(context, transactionResponse.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    Response.Loading -> CircularProgressIndicator()
                    is Response.Success -> {
                        Text(text = "Balance Screen")
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
                                        text = "Group Balance sheet",
                                        color = MaterialTheme.colors.secondaryVariant,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (transactionResponse.data.isEmpty()) {
                                Column {
                                    Text(
                                        text = "This is your transaction history."
                                    )
                                    Text(text = "")
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.DarkGray),
                                ) {
                                    Text(
                                        text = "From",
                                        modifier = Modifier.weight(0.33f),
                                        color = MaterialTheme.colors.secondaryVariant
                                    )
                                    Text(
                                        text = "To",
                                        modifier = Modifier.weight(0.33f),
                                        color = MaterialTheme.colors.secondaryVariant
                                    )
                                    Text(
                                        text = "£££",
                                        modifier = Modifier.weight(0.33f),
                                        color = MaterialTheme.colors.secondaryVariant
                                    )
                                }
                                LazyColumn {
                                    items(
                                        items = transactionResponse.data
                                    ) { transaction ->
//                                        Text(text = "${transaction.payerId}", modifier = Modifier.weight(0.33f), color = MaterialTheme.colors.secondaryVariant)
//                                        Text(text = "${transaction.beneficiaryId}", modifier = Modifier.weight(0.33f), color = MaterialTheme.colors.secondaryVariant)
//                                        Text(text = "${transaction.amount}", modifier = Modifier.weight(0.33f), color = MaterialTheme.colors.secondaryVariant)
                                        ItemLine(
                                            transaction = transaction,
                                            onClick = {
                                                balanceViewModel.transactionDetailsDialogOpen = true
                                                currentTransactionDetails = transaction
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MultiFloatingActionButton(
//    fabIcon: ImageBitmap,
    fabState: MultiFabState,
    onFabStateChange: (MultiFabState) -> Unit,
    items: List<MinFabItem>,
    balanceViewModel: BalanceViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val transition = updateTransition(targetState = fabState, label = "transition")
    val rotate by transition.animateFloat(label = "rotate") {
        if (it == MultiFabState.EXPANDED) 45f else 0f
    }
    val fabScale by transition.animateFloat(label = "FabScale") {
        if (it == MultiFabState.EXPANDED) 72f else 0f
    }
    val alpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = { tween(durationMillis = 50) }
    ) {
        if (it == MultiFabState.EXPANDED) 1f else 0f
    }
    val textShadow by transition.animateDp(
        label = "textShadow",
        transitionSpec = { tween(durationMillis = 50) }
    ) {
        if (it == MultiFabState.EXPANDED) 2.dp else 0.dp
    }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        if (transition.currentState == MultiFabState.EXPANDED) {
            items.forEach {
                MinFab(
                    item = it,
                    onMiniFabItemClick = { minFabItem ->
                        when (minFabItem.identifier) {
                            Identifier.Send.name -> {
                                balanceViewModel.openPayExpenseDialogState = true
                            }
                            Identifier.Add.name -> {
                                balanceViewModel.openAddExpenseDialogState = true
                            }
                        }
                    },
                    alpha = alpha,
                    textShadow = textShadow,
                    fabScale = fabScale,
                )
                Spacer(modifier = Modifier.size(32.dp))
            }
        }

        FloatingActionButton(
            onClick = {
                onFabStateChange(
                    if (transition.currentState == MultiFabState.EXPANDED) {
                        MultiFabState.COLLAPSED
                    } else {
                        MultiFabState.EXPANDED
                    }
                )
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add transaction",
                modifier = Modifier.rotate(rotate)
            )
        }
    }
}

@Composable
fun MinFab(
    item: MinFabItem,
    onMiniFabItemClick: (MinFabItem) -> Unit,
    alpha: Float,
    textShadow: Dp,
    fabScale: Float,
    showLabel: Boolean = true,
    modifier: Modifier = Modifier
) {
    val buttonColour = MaterialTheme.colors.secondary
    val shadow = Color.Black.copy(0.5f)
    Row(
        modifier = Modifier.padding(end = 12.dp)
    ) {
        if (showLabel) {
            Text(
                text = item.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .alpha(
                        animateFloatAsState(targetValue = alpha,animationSpec = tween(50)).value
                    )
                    .shadow(textShadow)
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 6.dp, end = 6.dp, top = 4.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
        }
        Canvas(
            modifier = Modifier
                .size(32.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    onClick = {onMiniFabItemClick.invoke(item)},
                    indication = rememberRipple(
                        bounded = false,
                        radius = 20.dp,
                        color = MaterialTheme.colors.onSurface
                    )
                )
        ) {
            drawCircle(
                color = shadow,
                radius = fabScale,
                center = Offset(x = center.x + 2f, y = center.y + 2f)
            )
            drawCircle(color = buttonColour, radius = fabScale)
            drawImage(
                image = item.icon,
                topLeft = Offset(x = center.x - (item.icon.width / 2),y = center.y - (item.icon.width / 2)),
                alpha = alpha
            )
        }
    }
}