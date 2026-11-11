package ru.ironcore.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronType

enum class Tab(val route: String, val label: String, val icon: ImageVector?) {
    HOME("home", "Главная", Icons.Outlined.Home),
    CATALOG("catalog", "Каталог", null),
    CART("cart", "Корзина", Icons.Outlined.ShoppingCart),
    FAVORITES("favorites", "Избранное", Icons.Outlined.FavoriteBorder),
    PROFILE("profile", "Профиль", Icons.Outlined.Person),
}

@Composable
fun BottomBar(current: Tab?, cartCount: Int, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val c = Iron.colors
    val shape = RoundedCornerShape(22.dp)
    Row(
        modifier
            .navigationBarsPadding()
            .padding(6.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(c.navBar)
            .border(1.dp, c.navBorder, shape)
            .padding(vertical = 8.dp),
    ) {
        Tab.entries.forEach { tab ->
            val color = if (tab == current) c.navActive else c.navInactive
            Column(
                Modifier.weight(1f).clickable { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box {
                    if (tab.icon != null) {
                        Icon(tab.icon, tab.label, tint = color, modifier = Modifier.size(28.dp))
                    } else {
                        DotsIcon(color)
                    }
                    if (tab == Tab.CART && cartCount > 0) {
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(c.accent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(cartCount.toString(), style = IronType.tiny.copy(fontSize = 8.sp), color = c.buttonText)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(tab.label, style = IronType.tiny.copy(fontSize = 10.sp), color = color, maxLines = 1)
            }
        }
    }
}
