package ru.ironcore.app.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order as SortOrder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import ru.ironcore.app.BuildConfig

class Repository {
    private val supabase = createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY) {
        install(Auth)
        install(Postgrest)
    }

    val userId: String?
        get() = supabase.auth.currentUserOrNull()?.id

    suspend fun waitForSession() = supabase.auth.awaitInitialization()

    suspend fun signUp(name: String, email: String, password: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject { put("name", name) }
        }
    }

    suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() = supabase.auth.signOut()

    suspend fun resetPassword(email: String) = supabase.auth.resetPasswordForEmail(email)

    suspend fun profile(uid: String): UserProfile? =
        supabase.from("users").select { filter { eq("id", uid) } }.decodeSingleOrNull()

    suspend fun updateName(uid: String, name: String) {
        supabase.from("users").update({ set("name", name) }) { filter { eq("id", uid) } }
    }

    suspend fun categories(): List<Category> =
        supabase.from("categories").select { order("id", SortOrder.ASCENDING) }.decodeList()

    suspend fun products(): List<Product> =
        supabase.from("products").select { order("id", SortOrder.ASCENDING) }.decodeList()

    suspend fun cart(uid: String): Cart {
        val carts = supabase.from("cart").select { filter { eq("user_id", uid) } }.decodeList<Cart>()
        return carts.firstOrNull()
            ?: supabase.from("cart").insert(NewCart(uid)) { select() }.decodeSingle()
    }

    suspend fun cartItems(cartId: Long): List<CartItem> =
        supabase.from("cart_items").select {
            filter { eq("cart_id", cartId) }
            order("id", SortOrder.ASCENDING)
        }.decodeList()

    suspend fun addCartItem(item: NewCartItem) {
        supabase.from("cart_items").insert(item)
    }

    suspend fun setQuantity(itemId: Long, quantity: Int) {
        supabase.from("cart_items").update({ set("quantity", quantity) }) { filter { eq("id", itemId) } }
    }

    suspend fun deleteCartItem(itemId: Long) {
        supabase.from("cart_items").delete { filter { eq("id", itemId) } }
    }

    suspend fun favorites(uid: String): List<Favorite> =
        supabase.from("favorites").select { filter { eq("user_id", uid) } }.decodeList()

    suspend fun addFavorite(uid: String, productId: Long) {
        supabase.from("favorites").insert(Favorite(uid, productId))
    }

    suspend fun deleteFavorite(uid: String, productId: Long) {
        supabase.from("favorites").delete {
            filter {
                eq("user_id", uid)
                eq("product_id", productId)
            }
        }
    }

    suspend fun placeOrder(uid: String, cartId: Long, lines: List<CartLine>, address: String, payment: String, comment: String?): Order {
        val order = supabase.from("orders")
            .insert(NewOrder(uid, lines.sumOf { it.sum }, address, payment, comment)) { select() }
            .decodeSingle<Order>()
        val items = lines.map { OrderItem(order.id, it.product.id, it.item.quantity, it.product.price, it.item.weight) }
        supabase.from("order_items").insert(items)
        supabase.from("cart_items").delete { filter { eq("cart_id", cartId) } }
        return order
    }

    suspend fun orders(uid: String): List<OrderWithItems> {
        val orders = supabase.from("orders").select {
            filter { eq("user_id", uid) }
            order("created_at", SortOrder.DESCENDING)
        }.decodeList<Order>()
        if (orders.isEmpty()) return emptyList()
        val items = supabase.from("order_items").select {
            filter { isIn("order_id", orders.map { it.id }) }
        }.decodeList<OrderItem>()
        return orders.map { order -> OrderWithItems(order, items.filter { it.orderId == order.id }) }
    }
}
