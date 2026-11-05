package ru.ironcore.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.ironcore.app.ui.components.BottomBar
import ru.ironcore.app.ui.components.Tab
import ru.ironcore.app.ui.screens.AddressesScreen
import ru.ironcore.app.ui.screens.CartScreen
import ru.ironcore.app.ui.screens.CatalogScreen
import ru.ironcore.app.ui.screens.CheckoutScreen
import ru.ironcore.app.ui.screens.FavoritesScreen
import ru.ironcore.app.ui.screens.HelpScreen
import ru.ironcore.app.ui.screens.HomeScreen
import ru.ironcore.app.ui.screens.LoginScreen
import ru.ironcore.app.ui.screens.OrdersScreen
import ru.ironcore.app.ui.screens.PaymentScreen
import ru.ironcore.app.ui.screens.ProductScreen
import ru.ironcore.app.ui.screens.ProfileScreen
import ru.ironcore.app.ui.screens.RegisterScreen
import ru.ironcore.app.ui.screens.SettingsScreen
import ru.ironcore.app.ui.screens.SplashScreen
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronTheme
import ru.ironcore.app.ui.theme.IronType
import ru.ironcore.app.ui.theme.isDarkTheme

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by vm.state.collectAsStateWithLifecycle()
            val dark = isDarkTheme(state.theme)
            SideEffect {
                val style = if (dark) SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                enableEdgeToEdge(style, style)
            }
            IronTheme(state.theme) {
                IronApp(vm)
            }
        }
    }

    override fun onDestroy() {
        if (isFinishing) vm.onAppClosed()
        super.onDestroy()
    }
}

@Composable
private fun IronApp(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route?.substringBefore("?")
    val c = Iron.colors

    LaunchedEffect(Unit) {
        vm.messages.collect {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(it)
        }
    }

    Box(Modifier.fillMaxSize().background(c.background)) {
        NavHost(nav, startDestination = "splash") {
            composable("splash") {
                SplashScreen(state.ready) {
                    nav.openFresh(if (state.loggedIn) "home" else "login")
                }
            }
            composable("login") {
                LoginScreen(
                    busy = state.busy,
                    onLogin = { email, password, remember -> vm.signIn(email, password, remember) { nav.openFresh("home") } },
                    onForgotPassword = vm::resetPassword,
                    onSocialLogin = { vm.showMessage("Вход через Apple и Google появится позже") },
                    onRegister = { nav.navigate("register") },
                )
            }
            composable("register") {
                RegisterScreen(
                    busy = state.busy,
                    onRegister = { name, email, password -> vm.signUp(name, email, password) { nav.openFresh("home") } },
                    onLogin = { nav.popBackStack() },
                )
            }
            composable("home") {
                HomeScreen(
                    state = state,
                    onSearch = { nav.openTab(Tab.CATALOG) },
                    onCategory = { nav.navigate("catalog?category=$it") },
                    onProduct = { nav.navigate("product/$it") },
                    onFavorite = vm::toggleFavorite,
                )
            }
            composable(
                "catalog?category={category}",
                arguments = listOf(navArgument("category") { type = NavType.LongType; defaultValue = 0L }),
            ) { entry ->
                CatalogScreen(
                    state = state,
                    startCategory = entry.arguments?.getLong("category")?.takeIf { it > 0 },
                    onProduct = { nav.navigate("product/$it") },
                    onFavorite = vm::toggleFavorite,
                )
            }
            composable("favorites") {
                FavoritesScreen(state, onProduct = { nav.navigate("product/$it") }, onFavorite = vm::toggleFavorite)
            }
            composable("cart") {
                CartScreen(
                    state = state,
                    onProduct = { nav.navigate("product/$it") },
                    onQuantity = vm::changeQuantity,
                    onRemove = vm::removeItem,
                    onCheckout = { nav.navigate("checkout") },
                    onCatalog = { nav.openTab(Tab.CATALOG) },
                )
            }
            composable("profile") {
                ProfileScreen(
                    state = state,
                    onOrders = {
                        vm.refreshOrders()
                        nav.navigate("orders")
                    },
                    onFavorites = { nav.openTab(Tab.FAVORITES) },
                    onAddresses = { nav.navigate("addresses") },
                    onPayment = { nav.navigate("payment") },
                    onSettings = { nav.navigate("settings") },
                    onHelp = { nav.navigate("help") },
                    onLogout = { vm.signOut { nav.openFresh("login") } },
                )
            }
            composable("product/{id}", arguments = listOf(navArgument("id") { type = NavType.LongType })) { entry ->
                ProductScreen(state.product(entry.arguments?.getLong("id") ?: 0), vm::addToCart)
            }
            composable("checkout") {
                CheckoutScreen(state) { address, payment, comment ->
                    vm.placeOrder(address, payment, comment) {
                        nav.navigate("orders") { popUpTo("cart") }
                    }
                }
            }
            composable("orders") { OrdersScreen(state, onProduct = { nav.navigate("product/$it") }) }
            composable("settings") { SettingsScreen(state, vm::setTheme, vm::updateName) }
            composable("addresses") { AddressesScreen(state, vm::saveAddress, vm::setDefaultAddress, vm::deleteAddress) }
            composable("payment") { PaymentScreen(state, vm::setPayment) }
            composable("help") { HelpScreen() }
        }

        val tab = Tab.entries.find { it.route == route }
        if (tab != null || route == "orders") {
            BottomBar(
                current = tab ?: Tab.PROFILE,
                cartCount = state.cartItems.sumOf { it.quantity },
                onSelect = { nav.openTab(it) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(12.dp)) {
            Snackbar(containerColor = c.buttonEnd, shape = RoundedCornerShape(14.dp)) {
                Text(it.visuals.message, style = IronType.small, color = c.buttonText)
            }
        }
    }
}

private fun NavHostController.openFresh(route: String) {
    navigate(route) { popUpTo(graph.id) { inclusive = true } }
}

private fun NavHostController.openTab(tab: Tab) {
    navigate(tab.route) {
        popUpTo("home") { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
