package x.lunacode.housemates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import x.lunacode.housemates.ui.screens.account.AccountScreen
import x.lunacode.housemates.ui.screens.balance.BalanceScreen
import x.lunacode.housemates.ui.screens.inventory.InventoryScreen
import x.lunacode.housemates.ui.screens.login.LoginScreen
import x.lunacode.housemates.ui.screens.subscribe.SubscribeScreen
import x.lunacode.housemates.ui.theme.HousematesTheme
import x.lunacode.housemates.util.Routes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HousematesTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Routes.LOGIN_SCREEN
                ) {
                    composable(
                        route = Routes.LOGIN_SCREEN
                    ) {
                        LoginScreen(
                            onNavigate = {
                                navController.navigate(it.route) {
                                    popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(
                        route = Routes.REGISTER_USER
                    ) {
                        SubscribeScreen(
                            onNavigate = {
                                navController.navigate(it.route) {
                                    popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(
                        route = Routes.GROUP_INVENTORY + "?userId={userId}",
                        arguments = listOf(
                            navArgument(name = "userId") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) {
                        InventoryScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                    composable(
                        route = Routes.ACCOUNT_SCREEN + "?userId={userId}",
                        arguments = listOf(
                            navArgument(name = "userId") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) {
                        AccountScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                    composable(
                        route = Routes.GROUP_BALANCE + "?userId={userId}",
                        arguments = listOf(
                            navArgument(name = "userId") {
                                type = NavType.StringType
                                defaultValue = ""
                            }
                        )
                    ) {
                        BalanceScreen(
                            onNavigate = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

