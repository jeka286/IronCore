package ru.ironcore.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ironcore.app.AppState
import ru.ironcore.app.BuildConfig
import ru.ironcore.app.data.Payment
import ru.ironcore.app.ui.components.EmptyState
import ru.ironcore.app.ui.components.IronButton
import ru.ironcore.app.ui.components.IronField
import ru.ironcore.app.ui.components.ScreenTitle
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronType
import ru.ironcore.app.ui.theme.ThemeMode

@Composable
fun ProfileScreen(
    state: AppState,
    onOrders: () -> Unit,
    onFavorites: () -> Unit,
    onAddresses: () -> Unit,
    onPayment: () -> Unit,
    onSettings: () -> Unit,
    onHelp: () -> Unit,
    onLogout: () -> Unit,
) {
    val c = Iron.colors
    val name = state.profile?.name ?: "Пользователь"

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        ScreenTitle("Профиль")
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(90.dp).clip(CircleShape).background(Color(0xFF1ED0E6)), contentAlignment = Alignment.Center) {
                Text(name.take(1), style = IronType.h1.copy(fontSize = 40.sp), color = Color.White)
            }
            Spacer(Modifier.width(30.dp))
            Column {
                Text(name, style = IronType.h1.copy(fontSize = 30.sp), color = c.text, maxLines = 1)
                Text(
                    state.profile?.email.orEmpty(),
                    style = IronType.small.copy(fontSize = 15.sp, textDecoration = TextDecoration.Underline),
                    color = c.textSecondary,
                    maxLines = 1,
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            MenuRow(Icons.Outlined.ShoppingBag, "Мои заказы", onOrders)
            MenuRow(Icons.Outlined.FavoriteBorder, "Избранное", onFavorites)
            MenuRow(Icons.Outlined.LocationOn, "Адреса доставки", onAddresses)
            MenuRow(Icons.Outlined.CreditCard, "Способ оплаты", onPayment)
            MenuRow(Icons.Outlined.Settings, "Настройки", onSettings)
            MenuRow(Icons.AutoMirrored.Outlined.HelpOutline, "Помощь", onHelp)
        }
        Spacer(Modifier.height(48.dp))
        MenuRow(Icons.Outlined.PersonRemove, "Выйти", onLogout, danger = true)
        Spacer(Modifier.height(BottomBarSpace))
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    danger: Boolean = false,
    selected: Boolean = false,
    onDelete: (() -> Unit)? = null,
) {
    val c = Iron.colors
    val shape = RoundedCornerShape(12.dp)
    val color = if (danger) c.dangerText else c.textSecondary
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(shape)
            .then(
                if (danger) Modifier.background(c.danger)
                else Modifier.background(Brush.horizontalGradient(listOf(c.card, c.cardEnd))).border(1.dp, c.fieldBorder, shape)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(20.dp))
        Text(text, style = IronType.small.copy(fontSize = 14.sp), color = color, modifier = Modifier.weight(1f))
        if (selected) Icon(Icons.Outlined.Check, null, tint = c.accent, modifier = Modifier.size(22.dp))
        if (onDelete != null) {
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Outlined.Delete, "Удалить", tint = c.textSecondary, modifier = Modifier.size(24.dp).clickable(onClick = onDelete))
        }
    }
}

@Composable
private fun SubScreen(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Iron.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        ScreenTitle(title)
        Spacer(Modifier.height(24.dp))
        content()
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = IronType.body.copy(fontSize = 16.sp), color = Iron.colors.text, modifier = Modifier.padding(bottom = 14.dp))
}

@Composable
fun SettingsScreen(state: AppState, onTheme: (ThemeMode) -> Unit, onSaveName: (String) -> Unit) {
    var name by rememberSaveable { mutableStateOf(state.profile?.name.orEmpty()) }
    SubScreen("Настройки") {
        SectionTitle("Тема оформления")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuRow(Icons.Outlined.PhoneAndroid, "Как в системе Android", { onTheme(ThemeMode.SYSTEM) }, selected = state.theme == ThemeMode.SYSTEM)
            MenuRow(Icons.Outlined.LightMode, "Светлая", { onTheme(ThemeMode.LIGHT) }, selected = state.theme == ThemeMode.LIGHT)
            MenuRow(Icons.Outlined.DarkMode, "Тёмная", { onTheme(ThemeMode.DARK) }, selected = state.theme == ThemeMode.DARK)
        }
        Spacer(Modifier.height(28.dp))
        SectionTitle("Имя в профиле")
        IronField(name, { name = it }, "Имя")
        Spacer(Modifier.height(14.dp))
        IronButton("Сохранить", { onSaveName(name) })
        Spacer(Modifier.height(28.dp))
        Text("IRONCORE ${BuildConfig.VERSION_NAME}", style = IronType.small, color = Iron.colors.textSecondary)
    }
}

@Composable
fun AddressesScreen(state: AppState, onAdd: (String) -> Unit, onSelect: (String) -> Unit, onDelete: (String) -> Unit) {
    var newAddress by rememberSaveable { mutableStateOf("") }
    SubScreen("Адреса доставки") {
        if (state.addresses.isEmpty()) {
            EmptyState("Адресов пока нет", "Добавьте адрес — он подставится при оформлении заказа")
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.addresses.forEach { address ->
                MenuRow(
                    Icons.Outlined.LocationOn,
                    address,
                    { onSelect(address) },
                    selected = address == state.defaultAddress,
                    onDelete = { onDelete(address) },
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        SectionTitle("Новый адрес")
        IronField(newAddress, { newAddress = it }, "г. Якутск, ул. Ленина, 15")
        Spacer(Modifier.height(14.dp))
        IronButton("Добавить адрес", {
            if (newAddress.isNotBlank()) {
                onAdd(newAddress.trim())
                newAddress = ""
            }
        })
    }
}

@Composable
fun PaymentScreen(state: AppState, onSelect: (String) -> Unit) {
    SubScreen("Способ оплаты") {
        Text("Выбранный способ будет предлагаться при оформлении заказа", style = IronType.small, color = Iron.colors.textSecondary)
        Spacer(Modifier.height(18.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Payment.all.forEach { method ->
                MenuRow(Icons.Outlined.CreditCard, Payment.title(method), { onSelect(method) }, selected = state.payment == method)
            }
        }
    }
}

private val questions = listOf(
    "Как оформить заказ?" to "Добавьте товары в корзину, нажмите «Оформить заказ», укажите адрес и способ оплаты.",
    "Сколько идёт доставка?" to "По городу — 1–2 дня. Статус заказа виден в разделе «Мои заказы».",
    "Можно ли отменить заказ?" to "Да, пока заказ в обработке — напишите нам на почту поддержки.",
    "Как подобрать спортпит?" to "Смотрите описание и состав в карточке товара, фильтруйте каталог по категориям.",
)

@Composable
fun HelpScreen() {
    val c = Iron.colors
    SubScreen("Помощь") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            questions.forEach { (question, answer) ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(c.card, c.cardEnd)))
                        .padding(16.dp)
                ) {
                    Text(question, style = IronType.body, color = c.text)
                    Spacer(Modifier.height(8.dp))
                    Text(answer, style = IronType.small, color = c.textSecondary)
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("Поддержка: support@ironcore.ru", style = IronType.small, color = c.accent)
    }
}
