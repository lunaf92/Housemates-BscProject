package x.lunacode.housemates.ui.screens.inventory.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import x.lunacode.housemates.data.models.Item
import x.lunacode.housemates.ui.screens.inventory.InventoryViewModel

@Composable
fun ItemCard(
    item: Item,
    inventoryViewModel: InventoryViewModel = hiltViewModel()

) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth(),
        elevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            ) {
                item.name?.let {
                    Text(
                        text = it,
                        color = Color.LightGray,
                        fontSize = 36.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item.quantity?.let {
                        Text(
                            text = it.toString(),
                            color = Color.LightGray,
                            fontSize = 24.sp
                        )
                    }

                    if (item.quantity!! > 0) {
                        IconButton(onClick = {
                            item.id?.let {
                                inventoryViewModel.decreaseItem(it)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Increase count"
                            )
                        }
                    }

                    IconButton(onClick = {
                        item.id?.let {
                            inventoryViewModel.increaseItem(it)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase count")
                    }
                }
            }
            IconButton(
                onClick = {
                    item.id?.let {
                        inventoryViewModel.deleteItem(it)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}
