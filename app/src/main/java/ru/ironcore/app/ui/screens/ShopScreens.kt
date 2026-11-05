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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ironcore.app.AppState
import ru.ironcore.app.R
import ru.ironcore.app.data.Category
import ru.ironcore.app.data.Product
import ru.ironcore.app.ui.components.EmptyState
import ru.ironcore.app.ui.components.IronButton
import ru.ironcore.app.ui.components.IronCard
import ru.ironcore.app.ui.components.IronField
import ru.ironcore.app.ui.components.ProductImage
import ru.ironcore.app.ui.components.ProductThumb
import ru.ironcore.app.ui.components.ScreenTitle
import ru.ironcore.app.ui.components.SearchButton
import ru.ironcore.app.ui.components.Stepper
import ru.ironcore.app.ui.components.formatPrice
import ru.ironcore.app.ui.components.glow
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronType
import java.util.Locale

val BottomBarSpace = 110.dp

@Composable
fun HomeScreen(
    state: AppState,
    onSearch: () -> Unit,
    onCategory: (Long) -> Unit,
    onProduct: (Long) -> Unit,
    onFavorite: (Long) -> Unit,
) {
    val c = Iron.colors
    val name = state.profile?.name ?: "друг"
    val popular = state.products.sortedByDescending { it.rating ?: 0.0 }.take(6)

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(44.dp))
        Column(Modifier.padding(start = 12.dp)) {
            Text("IRONCORE", style = IronType.h1.copy(fontSize = 30.sp), color = c.accent)
            Spacer(Modifier.height(14.dp))
            Text("Привет $name !", style = IronType.h2.copy(fontSize = 25.sp), color = c.text)
            Spacer(Modifier.height(6.dp))
            Text("Готов к новым результатам ?", style = IronType.body, color = c.textSecondary)
        }
        Spacer(Modifier.height(22.dp))
        SearchButton(onSearch)
        Spacer(Modifier.height(16.dp))
        Image(
            painterResource(R.drawable.banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.7f)
                .glow(radius = 10.dp, corner = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, c.glow, RoundedCornerShape(12.dp))
                .clickable { state.products.find { it.name.startsWith("Креатин") }?.let { onProduct(it.id) } },
        )
        Spacer(Modifier.height(22.dp))
        Text("Категории", style = IronType.h3.copy(fontSize = 20.sp), color = c.text)
        Spacer(Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            items(state.categories) { CategoryTile(it) { onCategory(it.id) } }
        }
        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Популярные товары", style = IronType.h3.copy(fontSize = 20.sp), color = c.text, modifier = Modifier.weight(1f))
            Text("Смотреть все", style = IronType.small, color = c.accent, modifier = Modifier.clickable(onClick = onSearch))
        }
        Spacer(Modifier.height(16.dp))
        popular.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                row.forEach { product ->
                    PopularCard(
                        product = product,
                        isFavorite = product.id in state.favorites,
                        onClick = { onProduct(product.id) },
                        onFavorite = { onFavorite(product.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        Spacer(Modifier.height(BottomBarSpace))
    }
}

@Composable
private fun CategoryTile(category: Category, onClick: () -> Unit) {
    val c = Iron.colors
    Column(Modifier.width(78.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(78.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(c.tile)
                .border(1.dp, if (c.isDark) Color.Transparent else c.fieldBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(category.name.take(1), style = IronType.h2, color = c.accent)
        }
        Spacer(Modifier.height(6.dp))
        Text(category.name, style = IronType.tiny.copy(fontSize = 10.sp), color = c.textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun PopularCard(product: Product, isFavorite: Boolean, onClick: () -> Unit, onFavorite: () -> Unit, modifier: Modifier) {
    val c = Iron.colors
    IronCard(modifier.height(210.dp), onClick = onClick) {
        Column(Modifier.padding(12.dp)) {
            ProductImage(
                product.image,
                Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.Black),
            )
            Spacer(Modifier.height(8.dp))
            Text(product.name, style = IronType.small, color = c.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(formatPrice(product.price), style = IronType.small, color = c.accent)
        }
        FavoriteButton(isFavorite, onFavorite, Modifier.align(Alignment.TopEnd).padding(14.dp), onImage = true)
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, onImage: Boolean = false) {
    val c = Iron.colors
    Icon(
        if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = "Избранное",
        tint = when {
            isFavorite -> c.accent
            onImage -> Color.White
            else -> c.text
        },
        modifier = modifier.size(30.dp).clickable(onClick = onClick),
    )
}

@Composable
fun CatalogScreen(
    state: AppState,
    startCategory: Long?,
    onProduct: (Long) -> Unit,
    onFavorite: (Long) -> Unit,
) {
    val c = Iron.colors
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(startCategory) }
    var showFilter by rememberSaveable { mutableStateOf(startCategory != null) }
    val products = state.products.filter {
        (category == null || it.categoryId == category) && it.name.contains(query.trim(), ignoreCase = true)
    }

    LazyColumn(
        Modifier.fillMaxSize().background(c.background).statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 40.dp, bottom = BottomBarSpace),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { ScreenTitle("Каталог") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IronField(query, { query = it }, "Поиск товаров...", Modifier.weight(1f), icon = Icons.Outlined.Search)
                Box(
                    Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(c.fieldStart, c.fieldEnd)))
                        .border(1.dp, if (showFilter) c.glow else c.fieldBorder, RoundedCornerShape(14.dp))
                        .clickable { showFilter = !showFilter },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Menu, "Фильтр", tint = c.text, modifier = Modifier.size(28.dp))
                }
            }
        }
        if (showFilter) item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Chip("Все", category == null) { category = null } }
                items(state.categories) { Chip(it.name, category == it.id) { category = it.id } }
            }
        }
        if (products.isEmpty()) item { EmptyState("Ничего не найдено", "Попробуйте изменить запрос или категорию") }
        items(products, key = { it.id }) { product ->
            ProductRow(product, product.id in state.favorites, { onProduct(product.id) }, { onFavorite(product.id) })
        }
    }
}

@Composable
fun FavoritesScreen(state: AppState, onProduct: (Long) -> Unit, onFavorite: (Long) -> Unit) {
    val products = state.products.filter { it.id in state.favorites }
    LazyColumn(
        Modifier.fillMaxSize().background(Iron.colors.background).statusBarsPadding(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 40.dp, bottom = BottomBarSpace),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { ScreenTitle("Избранное") }
        if (products.isEmpty()) item { EmptyState("Здесь пока пусто", "Нажмите ♡ на товаре, чтобы добавить его в избранное") }
        items(products, key = { it.id }) { product ->
            ProductRow(product, true, { onProduct(product.id) }, { onFavorite(product.id) })
        }
    }
}

@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    val c = Iron.colors
    Text(
        text,
        style = IronType.small,
        color = if (selected) Color.White else c.text,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) c.chip else c.tile)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
    )
}

@Composable
private fun ProductRow(product: Product, isFavorite: Boolean, onClick: () -> Unit, onFavorite: () -> Unit) {
    val c = Iron.colors
    IronCard(Modifier.fillMaxWidth().height(146.dp), onClick = onClick) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            ProductThumb(product.image, 112.dp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f).padding(end = 30.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(product.name, style = IronType.body, color = c.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(product.weights.firstOrNull().orEmpty(), style = IronType.small, color = c.textSecondary)
                Text(formatPrice(product.price), style = IronType.body, color = c.text)
                Rating(product.rating, IronType.tiny)
            }
        }
        FavoriteButton(isFavorite, onFavorite, Modifier.align(Alignment.TopEnd).padding(12.dp))
    }
}

@Composable
private fun Rating(rating: Double?, style: TextStyle, suffix: String = "") {
    val c = Iron.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.StarOutline, null, tint = c.textSecondary, modifier = Modifier.size(style.fontSize.value.dp + 4.dp))
        Spacer(Modifier.width(4.dp))
        Text(String.format(Locale.US, "%.2f", rating ?: 0.0) + suffix, style = style, color = c.textSecondary)
    }
}

@Composable
fun ProductScreen(product: Product?, onAddToCart: (Product, Int, String?) -> Unit) {
    val c = Iron.colors
    if (product == null) {
        Box(Modifier.fillMaxSize().background(c.background))
        return
    }
    var weight by remember(product.id) { mutableStateOf(product.weights.lastOrNull()) }
    var quantity by remember(product.id) { mutableIntStateOf(1) }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        ProductImage(product.image, Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(20.dp)).background(Color.Black))
        Spacer(Modifier.height(22.dp))
        Text(product.name, style = IronType.h2.copy(fontSize = 26.sp), color = c.text)
        Spacer(Modifier.height(8.dp))
        Rating(product.rating, IronType.body, " (${product.reviewsCount} отзывов)")
        Spacer(Modifier.height(18.dp))
        Text(formatPrice(product.price), style = IronType.h2.copy(fontSize = 28.sp), color = c.text)
        product.oldPrice?.let {
            Text(formatPrice(it), style = IronType.small.copy(textDecoration = TextDecoration.LineThrough), color = c.textSecondary)
        }
        Spacer(Modifier.height(12.dp))
        Text(product.description.orEmpty(), style = IronType.body.copy(fontSize = 16.sp, lineHeight = 21.sp), color = c.textSecondary)
        if (product.weights.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Вес", style = IronType.body, color = c.text)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                product.weights.forEach { option ->
                    WeightButton(option, option == weight, Modifier.weight(1f)) { weight = option }
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Количество", style = IronType.body, color = c.text)
        Spacer(Modifier.height(10.dp))
        Stepper(quantity, { if (quantity > 1) quantity-- }, { quantity++ }, cell = 50.dp)
        Spacer(Modifier.height(24.dp))
        IronButton("Добавить в корзину", { onAddToCart(product, quantity, weight) })
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun WeightButton(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val c = Iron.colors
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .height(46.dp)
            .glow(radius = 8.dp, corner = 23.dp)
            .clip(shape)
            .background(if (selected) c.buttonEnd else c.stepper)
            .border(1.dp, if (selected) c.glow else c.fieldBorder, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = IronType.h3, color = if (selected) c.buttonText else c.text)
    }
}
