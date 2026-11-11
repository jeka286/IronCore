package ru.ironcore.app.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.ironcore.app.R
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronType
import java.util.Locale

fun formatPrice(value: Double): String =
    String.format(Locale.US, "%,d р", value.toLong()).replace(',', ' ')

@Composable
fun Modifier.glow(radius: Dp = 10.dp, corner: Dp = 16.dp): Modifier {
    val c = Iron.colors
    val color = c.glow.copy(alpha = if (c.isDark) 0.85f else 0.35f).toArgb()
    return drawBehind {
        val paint = Paint().apply {
            this.color = color
            setShadowLayer(radius.toPx(), 0f, 0f, color)
        }
        drawIntoCanvas {
            it.nativeCanvas.drawRoundRect(0f, 0f, size.width, size.height, corner.toPx(), corner.toPx(), paint)
        }
    }
}

@Composable
fun ScreenTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = IronType.title, color = Iron.colors.text, modifier = modifier)
}

@Composable
fun IronField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    password: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null,
) {
    val c = Iron.colors
    val shape = RoundedCornerShape(14.dp)
    var showPassword by remember { mutableStateOf(false) }
    val hidden = password && !showPassword

    Column(modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = IronType.body.copy(color = c.text),
            cursorBrush = SolidColor(c.accent),
            visualTransformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else keyboardType),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(shape)
                .background(Brush.horizontalGradient(listOf(c.fieldStart, c.fieldEnd)))
                .border(1.dp, if (error != null) ErrorColor else c.fieldBorder, shape),
            decorationBox = { input ->
                Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(icon, null, tint = c.text, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(18.dp))
                    }
                    Box(Modifier.weight(1f)) {
                        if (value.isEmpty()) Text(placeholder, style = IronType.body, color = c.placeholder)
                        input()
                    }
                    if (password) {
                        Icon(
                            if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (showPassword) "Скрыть пароль" else "Показать пароль",
                            tint = c.textSecondary,
                            modifier = Modifier.size(24.dp).clickable { showPassword = !showPassword },
                        )
                    }
                }
            },
        )
        if (error != null) {
            Text(error, style = IronType.tiny, color = ErrorColor, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

val ErrorColor = Color(0xFFE5484D)

@Composable
fun SearchButton(onClick: () -> Unit) {
    val c = Iron.colors
    val shape = RoundedCornerShape(14.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(c.fieldStart, c.fieldEnd)))
            .border(1.dp, c.fieldBorder, shape)
            .clickable(onClick = onClick)
            .padding(start = 66.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text("Поиск товаров...", style = IronType.body, color = c.placeholder)
    }
}

@Composable
fun IronButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, loading: Boolean = false) {
    val c = Iron.colors
    Box(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.horizontalGradient(listOf(c.buttonStart, c.buttonEnd, c.buttonStart)))
            .clickable(enabled = !loading, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(color = c.buttonText, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Text(text, style = IronType.small.copy(fontSize = 14.sp), color = c.buttonText)
        }
    }
}

@Composable
fun IronCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable BoxScope.() -> Unit) {
    val c = Iron.colors
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(c.card, c.cardEnd)))
            .border(1.dp, if (c.isDark) Color.Transparent else c.fieldBorder, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content,
    )
}

@Composable
fun ProductImage(image: String?, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) {
    when {
        image == "creatine_dark" ->
            Image(painterResource(R.drawable.product_creatine_dark), null, modifier, contentScale = contentScale)
        image.isNullOrBlank() || !image.startsWith("http") ->
            Image(painterResource(R.drawable.product_creatine_hi), null, modifier, contentScale = contentScale)
        else ->
            AsyncImage(image, null, modifier, contentScale = contentScale, error = painterResource(R.drawable.product_creatine_hi))
    }
}

@Composable
fun ProductThumb(image: String?, size: Dp) {
    Box(
        Modifier
            .size(size)
            .glow(radius = 9.dp, corner = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
    ) {
        ProductImage(image, Modifier.fillMaxSize())
    }
}

@Composable
fun Stepper(value: Int, onMinus: () -> Unit, onPlus: () -> Unit, cell: Dp = 30.dp) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        StepperCell("–", cell, onMinus)
        StepperCell(value.toString(), cell, null)
        StepperCell("+", cell, onPlus)
    }
}

@Composable
private fun StepperCell(text: String, size: Dp, onClick: (() -> Unit)?) {
    val c = Iron.colors
    val shape = RoundedCornerShape(size / 4)
    Box(
        Modifier
            .size(size)
            .glow(radius = 6.dp, corner = size / 4)
            .clip(shape)
            .background(c.stepper)
            .border(1.dp, c.fieldBorder, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = IronType.body.copy(fontSize = (size.value / 2).sp), color = c.text)
    }
}

@Composable
fun DeleteButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Icon(
        Icons.Outlined.Delete,
        contentDescription = "Удалить",
        tint = Iron.colors.textSecondary,
        modifier = modifier.size(28.dp).clickable(onClick = onClick),
    )
}

@Composable
fun StatusLabel(text: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, style = IronType.tiny.copy(fontSize = 10.sp), color = Color.White)
    }
}

@Composable
fun EmptyState(title: String, subtitle: String, action: (@Composable () -> Unit)? = null) {
    val c = Iron.colors
    Column(Modifier.fillMaxWidth().padding(vertical = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = IronType.h3, color = c.text, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = IronType.small, color = c.textSecondary, textAlign = TextAlign.Center)
        if (action != null) {
            Spacer(Modifier.height(20.dp))
            action()
        }
    }
}

@Composable
fun DotsIcon(color: Color) {
    Canvas(Modifier.size(26.dp)) {
        val step = size.width / 3
        for (x in 0..2) for (y in 0..2) {
            drawCircle(
                color = color,
                radius = size.width * 0.06f,
                center = Offset(step * x + step / 2, step * y + step / 2),
                style = Stroke(width = 1.4.dp.toPx()),
            )
        }
    }
}
