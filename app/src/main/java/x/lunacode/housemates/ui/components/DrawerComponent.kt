package x.lunacode.housemates.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import x.lunacode.housemates.util.Routes


enum class Screens(val label: String, val icon: ImageVector, val route: String) {
    InventoryScreen(label = "Home", Icons.Default.List, route = Routes.GROUP_INVENTORY),
    AccountScreen(label = "My Account", Icons.Default.Person, route = Routes.ACCOUNT_SCREEN),
    ExpensesScreen(label = "Group Ledger", icon = Icons.Default.Money, route = Routes.GROUP_BALANCE)
}


@Composable
fun DrawerComponent(
    items: List<Screens> = Screens.values().toList(),
    onClickItem: (Screens) -> Unit,
    onClickExit: () -> Unit
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onClickExit) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close drawer")
            }
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(0.6f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            items.forEach {
                Row(
                    modifier = Modifier
                        .clickable {
                            onClickItem(it)
                        }
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                ) {
                    Icon(imageVector = it.icon, contentDescription = it.label)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = it.label)
                }
            }
        }
        Column(
            modifier = Modifier.weight(0.1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Housemates ©")
        }
    }

}

