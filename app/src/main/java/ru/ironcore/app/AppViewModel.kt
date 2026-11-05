package ru.ironcore.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.exception.AuthRestException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.ironcore.app.data.CartItem
import ru.ironcore.app.data.CartLine
import ru.ironcore.app.data.Category
import ru.ironcore.app.data.NewCartItem
import ru.ironcore.app.data.OrderWithItems
import ru.ironcore.app.data.Prefs
import ru.ironcore.app.data.Product
import ru.ironcore.app.data.Repository
import ru.ironcore.app.data.UserProfile
import ru.ironcore.app.ui.theme.ThemeMode

data class AppState(
    val ready: Boolean = false,
    val loggedIn: Boolean = false,
    val busy: Boolean = false,
    val profile: UserProfile? = null,
    val categories: List<Category> = emptyList(),
    val products: List<Product> = emptyList(),
    val cartId: Long? = null,
    val cartItems: List<CartItem> = emptyList(),
    val favorites: Set<Long> = emptySet(),
    val orders: List<OrderWithItems> = emptyList(),
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val addresses: List<String> = emptyList(),
    val defaultAddress: String? = null,
    val payment: String = "card",
) {
    fun product(id: Long) = products.find { it.id == id }

    val cartLines: List<CartLine>
        get() = cartItems.mapNotNull { item -> product(item.productId)?.let { CartLine(item, it) } }

    val cartTotal: Double
        get() = cartLines.sumOf { it.sum }
}

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository()
    private val prefs = Prefs(app)

    val state = MutableStateFlow(
        AppState(
            theme = prefs.theme,
            addresses = prefs.addresses,
            defaultAddress = prefs.defaultAddress,
            payment = prefs.payment,
        )
    )
    val messages = MutableSharedFlow<String>(extraBufferCapacity = 4)

    init {
        viewModelScope.launch {
            runCatching { repo.waitForSession() }
            state.update { it.copy(ready = true, loggedIn = repo.userId != null) }
            load {
                loadCatalog()
                loadUser()
            }
        }
    }

    fun showMessage(text: String) {
        messages.tryEmit(text)
    }

    private fun load(showBusy: Boolean = false, block: suspend () -> Unit) = viewModelScope.launch {
        if (showBusy) state.update { it.copy(busy = true) }
        try {
            block()
        } catch (e: AuthRestException) {
            showMessage(authError(e.message.orEmpty()))
        } catch (e: Exception) {
            showMessage("Не удалось связаться с сервером")
        } finally {
            if (showBusy) state.update { it.copy(busy = false) }
        }
    }

    private fun authError(message: String) = when {
        "Invalid login" in message -> "Неверный email или пароль"
        "already registered" in message -> "Этот email уже зарегистрирован"
        "Password" in message -> "Пароль должен быть не короче 6 символов"
        else -> "Ошибка входа"
    }

    private suspend fun loadCatalog() {
        val categories = repo.categories()
        val products = repo.products()
        state.update { it.copy(categories = categories, products = products) }
    }

    private suspend fun loadUser() {
        val uid = repo.userId ?: return
        val profile = repo.profile(uid)
        val cart = repo.cart(uid)
        val items = repo.cartItems(cart.id)
        val favorites = repo.favorites(uid).map { it.productId }.toSet()
        val orders = repo.orders(uid)
        state.update {
            it.copy(profile = profile, cartId = cart.id, cartItems = items, favorites = favorites, orders = orders)
        }
    }

    fun onAppClosed() {
        if (!prefs.rememberMe && repo.userId != null) {
            viewModelScope.launch { runCatching { repo.signOut() } }
        }
    }

    fun signIn(email: String, password: String, remember: Boolean, onDone: () -> Unit) {
        if (email.isBlank() || password.isBlank()) return showMessage("Введите email и пароль")
        load(showBusy = true) {
            repo.signIn(email.trim(), password)
            prefs.rememberMe = remember
            state.update { it.copy(loggedIn = true) }
            loadUser()
            onDone()
        }
    }

    fun signUp(name: String, email: String, password: String, onDone: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) return showMessage("Заполните все поля")
        if (password.length < 6) return showMessage("Пароль должен быть не короче 6 символов")
        load(showBusy = true) {
            repo.signUp(name.trim(), email.trim(), password)
            prefs.rememberMe = true
            state.update { it.copy(loggedIn = true) }
            loadUser()
            onDone()
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) return showMessage("Введите email, чтобы восстановить пароль")
        load {
            repo.resetPassword(email.trim())
            showMessage("Ссылка для сброса пароля отправлена на почту")
        }
    }

    fun signOut(onDone: () -> Unit) = load {
        repo.signOut()
        state.update {
            it.copy(loggedIn = false, profile = null, cartId = null, cartItems = emptyList(), favorites = emptySet(), orders = emptyList())
        }
        onDone()
    }

    fun updateName(name: String) = load {
        val uid = repo.userId ?: return@load
        repo.updateName(uid, name.trim())
        state.update { it.copy(profile = it.profile?.copy(name = name.trim())) }
        showMessage("Имя сохранено")
    }

    fun addToCart(product: Product, quantity: Int, weight: String?) = load {
        val cartId = state.value.cartId ?: return@load
        val same = state.value.cartItems.find { it.productId == product.id && it.weight == weight }
        if (same != null) {
            repo.setQuantity(same.id, same.quantity + quantity)
        } else {
            repo.addCartItem(NewCartItem(cartId, product.id, quantity, weight))
        }
        state.update { it.copy(cartItems = repo.cartItems(cartId)) }
        showMessage("${product.name} добавлен в корзину")
    }

    fun changeQuantity(item: CartItem, delta: Int) {
        val quantity = item.quantity + delta
        if (quantity < 1) return removeItem(item)
        state.update { s -> s.copy(cartItems = s.cartItems.map { if (it.id == item.id) it.copy(quantity = quantity) else it }) }
        load { repo.setQuantity(item.id, quantity) }
    }

    fun removeItem(item: CartItem) {
        state.update { s -> s.copy(cartItems = s.cartItems - item) }
        load { repo.deleteCartItem(item.id) }
    }

    fun toggleFavorite(productId: Long) {
        val uid = repo.userId ?: return
        val isFavorite = productId in state.value.favorites
        state.update { s -> s.copy(favorites = if (isFavorite) s.favorites - productId else s.favorites + productId) }
        load {
            if (isFavorite) repo.deleteFavorite(uid, productId) else repo.addFavorite(uid, productId)
        }
    }

    fun placeOrder(address: String, payment: String, comment: String, onDone: () -> Unit) {
        val s = state.value
        val uid = repo.userId ?: return
        val cartId = s.cartId ?: return
        if (s.cartLines.isEmpty()) return showMessage("Корзина пуста")
        if (address.isBlank()) return showMessage("Укажите адрес доставки")
        load(showBusy = true) {
            val order = repo.placeOrder(uid, cartId, s.cartLines, address.trim(), payment, comment.ifBlank { null })
            saveAddress(address.trim())
            state.update { it.copy(cartItems = emptyList(), orders = repo.orders(uid)) }
            showMessage("Заказ №${order.id} оформлен")
            onDone()
        }
    }

    fun refreshOrders() = load {
        val uid = repo.userId ?: return@load
        state.update { it.copy(orders = repo.orders(uid)) }
    }

    fun setTheme(mode: ThemeMode) {
        prefs.theme = mode
        state.update { it.copy(theme = mode) }
    }

    fun saveAddress(address: String) {
        val list = listOf(address) + state.value.addresses.filter { it != address }
        prefs.addresses = list
        prefs.defaultAddress = address
        state.update { it.copy(addresses = list, defaultAddress = address) }
    }

    fun setDefaultAddress(address: String) {
        prefs.defaultAddress = address
        state.update { it.copy(defaultAddress = address) }
    }

    fun deleteAddress(address: String) {
        val list = state.value.addresses - address
        val default = state.value.defaultAddress.takeIf { it != address } ?: list.firstOrNull()
        prefs.addresses = list
        prefs.defaultAddress = default
        state.update { it.copy(addresses = list, defaultAddress = default) }
    }

    fun setPayment(method: String) {
        prefs.payment = method
        state.update { it.copy(payment = method) }
    }
}
