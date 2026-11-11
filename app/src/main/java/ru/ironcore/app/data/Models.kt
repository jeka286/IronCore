package ru.ironcore.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val name: String? = null,
    val email: String? = null,
)

@Serializable
data class Category(
    val id: Long,
    val name: String,
    val image: String? = null,
)

@Serializable
data class Product(
    val id: Long,
    @SerialName("category_id") val categoryId: Long? = null,
    val name: String,
    val description: String? = null,
    val price: Double = 0.0,
    @SerialName("old_price") val oldPrice: Double? = null,
    val image: String? = null,
    val rating: Double? = null,
    @SerialName("reviews_count") val reviewsCount: Int = 0,
    val weight: String? = null,
) {
    val weights: List<String>
        get() = weight.orEmpty().split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Serializable
data class Cart(
    val id: Long,
    @SerialName("user_id") val userId: String,
)

@Serializable
data class NewCart(@SerialName("user_id") val userId: String)

@Serializable
data class CartItem(
    val id: Long,
    @SerialName("cart_id") val cartId: Long,
    @SerialName("product_id") val productId: Long,
    val quantity: Int,
    val weight: String? = null,
)

@Serializable
data class NewCartItem(
    @SerialName("cart_id") val cartId: Long,
    @SerialName("product_id") val productId: Long,
    val quantity: Int,
    val weight: String?,
)

@Serializable
data class Favorite(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: Long,
)

@Serializable
data class Order(
    val id: Long,
    @SerialName("total_price") val totalPrice: Double,
    val status: String,
)

@Serializable
data class NewOrder(
    @SerialName("user_id") val userId: String,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("delivery_address") val address: String,
    @SerialName("payment_method") val payment: String,
    val comment: String?,
)

@Serializable
data class OrderItem(
    @SerialName("order_id") val orderId: Long,
    @SerialName("product_id") val productId: Long,
    val quantity: Int,
    val price: Double,
    val weight: String?,
)

data class CartLine(val item: CartItem, val product: Product) {
    val sum: Double get() = product.price * item.quantity
}

data class OrderWithItems(val order: Order, val items: List<OrderItem>)

object OrderStatus {
    const val PROCESSING = "processing"
    const val DELIVERED = "delivered"
    const val CANCELLED = "cancelled"

    fun title(status: String) = when (status) {
        DELIVERED -> "Доставлен"
        CANCELLED -> "Отменен"
        else -> "В обработке"
    }
}

object Payment {
    const val CARD = "card"
    const val SBP = "sbp"
    const val CASH = "cash"

    val all = listOf(CARD, SBP, CASH)

    fun title(method: String) = when (method) {
        SBP -> "СБП"
        CASH -> "Оплата при получении"
        else -> "Банковская карта"
    }
}
