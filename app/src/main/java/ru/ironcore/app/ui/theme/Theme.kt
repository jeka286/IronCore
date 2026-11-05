package ru.ironcore.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import ru.ironcore.app.R

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class IronColors(
    val isDark: Boolean,
    val background: Color,
    val fieldStart: Color,
    val fieldEnd: Color,
    val fieldBorder: Color,
    val card: Color,
    val cardEnd: Color,
    val tile: Color,
    val inner: Color,
    val text: Color,
    val textSecondary: Color,
    val placeholder: Color,
    val accent: Color,
    val glow: Color,
    val buttonStart: Color,
    val buttonEnd: Color,
    val buttonText: Color,
    val navBar: Color,
    val navBorder: Color,
    val navInactive: Color,
    val navActive: Color,
    val stepper: Color,
    val chip: Color,
    val danger: Color,
    val dangerText: Color,
)

val DarkColors = IronColors(
    isDark = true,
    background = Color(0xFF000000),
    fieldStart = Color(0xFF22072F),
    fieldEnd = Color(0xFF13031C),
    fieldBorder = Color(0xFF2B0C3C),
    card = Color(0xFF1C0428),
    cardEnd = Color(0xFF13021C),
    tile = Color(0xFF1C0529),
    inner = Color(0xFF110B1C),
    text = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF9F99A8),
    placeholder = Color(0xFFA197AD),
    accent = Color(0xFFA61BC4),
    glow = Color(0xFF8B2CF5),
    buttonStart = Color(0xFF3A0868),
    buttonEnd = Color(0xFF4A0A83),
    buttonText = Color(0xFFEDE6F5),
    navBar = Color(0xE6141117),
    navBorder = Color(0xFF29242F),
    navInactive = Color(0xFFA6A1AB),
    navActive = Color(0xFFFFFFFF),
    stepper = Color(0xFF130B1D),
    chip = Color(0xFF3D0A6E),
    danger = Color(0xFF6E0000),
    dangerText = Color(0xFFE3B5B5),
)

val LightColors = IronColors(
    isDark = false,
    background = Color(0xFFF7F3FB),
    fieldStart = Color(0xFFEBDDF7),
    fieldEnd = Color(0xFFF3EBFA),
    fieldBorder = Color(0xFFDCC8EE),
    card = Color(0xFFEFE3F9),
    cardEnd = Color(0xFFF6EFFC),
    tile = Color(0xFFEADCF6),
    inner = Color(0xFFFFFFFF),
    text = Color(0xFF14091C),
    textSecondary = Color(0xFF6C6377),
    placeholder = Color(0xFF8A7F97),
    accent = Color(0xFF9715B4),
    glow = Color(0xFFA35BFF),
    buttonStart = Color(0xFF55119A),
    buttonEnd = Color(0xFF6C1BC0),
    buttonText = Color(0xFFFFFFFF),
    navBar = Color(0xF2FFFFFF),
    navBorder = Color(0xFFE2D6EE),
    navInactive = Color(0xFF7B7287),
    navActive = Color(0xFF6C1BC0),
    stepper = Color(0xFFFFFFFF),
    chip = Color(0xFF5E14A8),
    danger = Color(0xFFB00020),
    dangerText = Color(0xFFFFFFFF),
)

val LocalIronColors = staticCompositionLocalOf { DarkColors }

object Iron {
    val colors: IronColors
        @Composable get() = LocalIronColors.current
}

val DaysOne = FontFamily(Font(R.font.days_one))

object IronType {
    val title = TextStyle(fontFamily = DaysOne, fontSize = 28.sp, lineHeight = 34.sp)
    val h1 = TextStyle(fontFamily = DaysOne, fontSize = 32.sp, lineHeight = 38.sp)
    val h2 = TextStyle(fontFamily = DaysOne, fontSize = 22.sp, lineHeight = 28.sp)
    val h3 = TextStyle(fontFamily = DaysOne, fontSize = 18.sp, lineHeight = 24.sp)
    val body = TextStyle(fontFamily = DaysOne, fontSize = 15.sp, lineHeight = 20.sp)
    val small = TextStyle(fontFamily = DaysOne, fontSize = 13.sp, lineHeight = 17.sp)
    val tiny = TextStyle(fontFamily = DaysOne, fontSize = 11.sp, lineHeight = 14.sp)
}

@Composable
fun isDarkTheme(mode: ThemeMode): Boolean = when (mode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun IronTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val dark = isDarkTheme(mode)
    CompositionLocalProvider(LocalIronColors provides if (dark) DarkColors else LightColors) {
        MaterialTheme(
            colorScheme = if (dark) darkColorScheme() else lightColorScheme(),
            content = content,
        )
    }
}
