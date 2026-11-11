package ru.ironcore.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ironcore.app.AppState
import ru.ironcore.app.R
import ru.ironcore.app.data.CartItem
import ru.ironcore.app.data.OrderStatus
import ru.ironcore.app.data.OrderWithItems
import ru.ironcore.app.data.Payment
import ru.ironcore.app.ui.components.DeleteButton
import ru.ironcore.app.ui.components.EmptyState
import ru.ironcore.app.ui.components.IronButton
import ru.ironcore.app.ui.components.IronCard
import ru.ironcore.app.ui.components.IronField
import ru.ironcore.app.ui.components.ProductImage
import ru.ironcore.app.ui.components.ProductThumb
import ru.ironcore.app.ui.components.ScreenTitle
import ru.ironcore.app.ui.components.StatusLabel
import ru.ironcore.app.ui.components.Stepper
import ru.ironcore.app.ui.components.formatPrice
import ru.ironcore.app.ui.components.glow
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronType

@Composable
fun CartScreen(
    state: AppState,
    onProduct: (Long) -> Unit,
    onQuantity: (CartItem, Int) -> Unit,
    onRemove: (CartItem) -> Unit,
    onCheckout: () -> Unit,
    onCatalog: () -> Unit,
) {
    val c = Iron.colors
    Column(Modifier.fillMaxSize().background(c.background).statusBarsPadding()) {
        ScreenTitle("Корзина", Modifier.padding(start = 24.dp, top = 40.dp, bottom = 14.dp))
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.cartLines.isEmpty()) item {
                EmptyState("Корзина пуста", "Добавьте товары из каталога") {
                    IronButton("Перейти в каталог", onCatalog, Modifier.padding(horizontal = 30.dp))
                }
            }
            items(state.cartLines, key = { it.item.id }) { line ->
                IronCard(Modifier.fillMaxWidth().height(156.dp), onClick = { onProduct(line.product.id) }) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        ProductThumb(line.product.image, 118.dp)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Text(line.product.name, style = IronType.body.copy(fontSize = 16.sp), color = c.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(line.item.weight.orEmpty(), style = IronType.small, color = c.textSecondary)
                            Text(formatPrice(line.product.price), style = IronType.small, color = c.textSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Stepper(line.item.quantity, { onQuantity(line.item, -1) }, { onQuantity(line.item, 1) }, cell = 32.dp)
                                Spacer(Modifier.weight(1f))
                                DeleteButton({ onRemove(line.item) })
                            }
                        }
                    }
                }
            }
        }
        if (state.cartLines.isNotEmpty()) {
            Column(Modifier.padding(start = 14.dp, end = 14.dp, top = 18.dp, bottom = BottomBarSpace)) {
                TotalRow(state.cartTotal)
                Spacer(Modifier.height(22.dp))
                IronButton("Оформить заказ", onCheckout)
            }
        }
    }
}

@Composable
private fun TotalRow(total: Double) {
    val c = Iron.colors
    Row(Modifier.padding(horizontal = 8.dp)) {
        Text("Итого", style = IronType.h2, color = c.text, modifier = Modifier.weight(1f))
        Text(formatPrice(total), style = IronType.h2, color = c.text)
    }
}

@Composable
fun CheckoutScreen(state: AppState, onPlaceOrder: (String, String, String) -> Unit) {
    val c = Iron.colors
    var address by rememberSaveable { mutableStateOf(state.defaultAddress.orEmpty()) }
    var payment by rememberSaveable { mutableStateOf(state.payment) }
    var comment by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        ScreenTitle("Оформление заказа")
        Spacer(Modifier.height(28.dp))
        Label("Адрес доставки")
        IronField(address, { address = it }, "г. Якутск, ул. Ленина, 15")
        if (state.addresses.size > 1) {
            Spacer(Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.addresses) { Chip(it, it == address) { address = it } }
            }
        }
        Spacer(Modifier.height(26.dp))
        Label("Способ оплаты")
        Payment.all.forEach { method ->
            PaymentRow(method, method == payment) { payment = method }
        }
        Spacer(Modifier.height(14.dp))
        Label("Комментарий")
        IronField(comment, { comment = it }, "Например, удобное время доставки")
        Spacer(Modifier.height(28.dp))
        TotalRow(state.cartTotal)
        Spacer(Modifier.height(56.dp))
        IronButton("Оформить заказ", { onPlaceOrder(address, payment, comment) }, loading = state.busy)
        Spacer(Modifier.height(BottomBarSpace))
    }
}

@Composable
private fun Label(text: String) {
    Text(text, style = IronType.body.copy(fontSize = 16.sp), color = Iron.colors.text, modifier = Modifier.padding(bottom = 14.dp))
}

@Composable
private fun PaymentRow(method: String, selected: Boolean, onClick: () -> Unit) {
    val c = Iron.colors
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(52.dp)) {
            when (method) {
                Payment.CARD -> Image(painterResource(R.drawable.ic_mir), "МИР", Modifier.width(48.dp).height(22.dp))
                Payment.SBP -> Image(painterResource(R.drawable.ic_sbp), "СБП", Modifier.size(28.dp))
                else -> CashBox(selected)
            }
        }
        Spacer(Modifier.width(10.dp))
        Text(Payment.title(method), style = IronType.small, color = if (selected) c.text else c.textSecondary, modifier = Modifier.weight(1f))
        if (selected && method != Payment.CASH) {
            Icon(Icons.Outlined.Check, null, tint = c.accent, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun CashBox(checked: Boolean) {
    val c = Iron.colors
    val shape = RoundedCornerShape(10.dp)
    Box(
        Modifier
            .size(38.dp)
            .glow(radius = 7.dp, corner = 10.dp)
            .clip(shape)
            .background(if (checked) c.buttonEnd else c.stepper)
            .border(1.dp, c.fieldBorder, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) Icon(Icons.Outlined.Check, null, tint = c.buttonText, modifier = Modifier.size(22.dp))
    }
}

private val orderFilters = listOf(
    "Все" to null,
    "В обработке" to OrderStatus.PROCESSING,
    "Доставлен" to OrderStatus.DELIVERED,
    "Отмена" to OrderStatus.CANCELLED,
)

@Composable
fun OrdersScreen(state: AppState, onProduct: (Long) -> Unit) {
    val c = Iron.colors
    var status by rememberSaveable { mutableStateOf<String?>(null) }
    val orders = state.orders.filter { status == null || it.order.status == status }

    LazyColumn(
        Modifier.fillMaxSize().background(c.background).statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 40.dp, bottom = BottomBarSpace),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { ScreenTitle("Мои заказы") }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(orderFilters) { (title, value) ->
                    Text(
                        title,
                        style = IronType.small.copy(fontSize = 14.sp),
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (value == status) c.chip else c.chip.copy(alpha = 0.55f))
                            .border(1.dp, if (value == status) c.glow else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { status = value }
                            .padding(horizontal = 9.dp, vertical = 10.dp),
                    )
                }
            }
        }
        if (orders.isEmpty()) item { EmptyState("Заказов пока нет", "Оформленные заказы появятся здесь") }
        items(orders, key = { it.order.id }) { OrderCard(it, state, onProduct) }
    }
}

@Composable
private fun OrderCard(order: OrderWithItems, state: AppState, onProduct: (Long) -> Unit) {
    val c = Iron.colors
    val statusColor = when (order.order.status) {
        OrderStatus.DELIVERED -> Color(0xFF0E8A45)
        OrderStatus.CANCELLED -> Color(0xFF8E0B0B)
        else -> Color(0xFF9A6418)
    }
    IronCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Row {
                Column(
                    Modifier
                        .weight(1f)
                        .height(100.dp)
                        .glow(radius = 9.dp, corner = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(c.inner)
                        .padding(10.dp)
                ) {
                    Text("Заказ №${order.order.id}", style = IronType.body, color = c.textSecondary)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        order.items.take(4).forEach { item ->
                            ProductImage(
                                state.product(item.productId)?.image,
                                Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .clickable { onProduct(item.productId) },
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                StatusLabel(OrderStatus.title(order.order.status), statusColor, Modifier.width(96.dp))
            }
            Spacer(Modifier.height(14.dp))
            Row {
                Text("Сумма: ${formatPrice(order.order.totalPrice)}", style = IronType.body, color = c.text, modifier = Modifier.weight(1f))
                Text("Товаров: ${order.items.sumOf { it.quantity }}", style = IronType.body, color = c.text)
            }
        }
    }
}
