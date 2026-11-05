package ru.ironcore.app.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.ironcore.app.R
import ru.ironcore.app.ui.components.IronButton
import ru.ironcore.app.ui.components.IronField
import ru.ironcore.app.ui.components.glow
import ru.ironcore.app.ui.theme.Iron
import ru.ironcore.app.ui.theme.IronType

@Composable
fun SplashScreen(ready: Boolean, onFinish: () -> Unit) {
    LaunchedEffect(ready) {
        if (ready) {
            delay(1400)
            onFinish()
        }
    }
    Image(
        painterResource(R.drawable.splash_bg),
        contentDescription = "IRONCORE",
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentScale = ContentScale.Crop,
    )
}

@Composable
private fun AuthLayout(content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Iron.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp),
    ) {
        content()
    }
}

@Composable
fun RegisterScreen(busy: Boolean, onRegister: (String, String, String) -> Unit, onLogin: () -> Unit) {
    val c = Iron.colors
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    AuthLayout {
        Spacer(Modifier.height(170.dp))
        Text("Создать аккаунт", style = IronType.h1.copy(fontSize = 30.sp), color = c.text)
        Spacer(Modifier.height(14.dp))
        Text("Присоединяйся к IRONCORE", style = IronType.body, color = c.textSecondary)
        Spacer(Modifier.height(12.dp))
        IronField(name, { name = it }, "Имя", icon = Icons.Outlined.Person)
        Spacer(Modifier.height(12.dp))
        IronField(email, { email = it }, "Email", icon = Icons.Outlined.Email, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        IronField(password, { password = it }, "Пароль", icon = Icons.Outlined.LockOpen, password = true)
        Spacer(Modifier.height(24.dp))
        IronButton("Создать аккаунт", { onRegister(name, email, password) }, loading = busy)
        Spacer(Modifier.height(36.dp))
        Text(
            buildAnnotatedString {
                append("Регистрируясь, вы соглашаетесь\nс ")
                withStyle(SpanStyle(color = c.accent)) { append("Условиями пользования") }
                append("\nи ")
                withStyle(SpanStyle(color = c.accent)) { append("Политикой конфиденциальности") }
            },
            style = IronType.small,
            color = c.text,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(150.dp))
        Row(Modifier.fillMaxWidth().padding(bottom = 40.dp), horizontalArrangement = Arrangement.Center) {
            Text("У вас есть аккаунт?", style = IronType.body.copy(fontSize = 16.sp), color = c.textSecondary)
            Spacer(Modifier.width(16.dp))
            Text("Войти", style = IronType.body, color = c.accent, modifier = Modifier.clickable(onClick = onLogin))
        }
    }
}

@Composable
fun LoginScreen(
    busy: Boolean,
    onLogin: (String, String, Boolean) -> Unit,
    onForgotPassword: (String) -> Unit,
    onSocialLogin: () -> Unit,
    onRegister: () -> Unit,
) {
    val c = Iron.colors
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var remember by rememberSaveable { mutableStateOf(true) }

    AuthLayout {
        Spacer(Modifier.height(150.dp))
        Text(
            buildAnnotatedString {
                append("Добро пожаловать\nв ")
                withStyle(SpanStyle(color = c.accent)) { append("IRONCORE") }
            },
            style = IronType.h1.copy(fontSize = 30.sp, lineHeight = 40.sp),
            color = c.text,
        )
        Spacer(Modifier.height(8.dp))
        Text("Твой прогресс начинается здесь", style = IronType.body, color = c.textSecondary)
        Spacer(Modifier.height(48.dp))
        IronField(email, { email = it }, "Email", icon = Icons.Outlined.Email, keyboardType = KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        IronField(password, { password = it }, "Пароль", icon = Icons.Outlined.LockOpen, password = true)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(remember) { remember = !remember }
            Spacer(Modifier.width(14.dp))
            Text("Запомнить меня", style = IronType.small, color = c.text)
            Spacer(Modifier.weight(1f))
            Text(
                "Забыли пароль?",
                style = IronType.small,
                color = c.accent,
                modifier = Modifier.clickable { onForgotPassword(email) },
            )
        }
        Spacer(Modifier.height(28.dp))
        IronButton("Войти", { onLogin(email, password, remember) }, loading = busy)
        Spacer(Modifier.height(30.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f).height(2.dp).background(c.buttonEnd))
            Text("или", style = IronType.small, color = c.text, modifier = Modifier.padding(horizontal = 8.dp))
            Box(Modifier.weight(1f).height(2.dp).background(c.buttonEnd))
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SocialButton(R.drawable.ic_apple, tint = c.text, onClick = onSocialLogin, modifier = Modifier.weight(1f))
            SocialButton(R.drawable.ic_gplay, tint = null, onClick = onSocialLogin, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(48.dp))
        Row(Modifier.fillMaxWidth().padding(bottom = 40.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Нет аккаунта?",
                style = IronType.body.copy(fontSize = 16.sp),
                color = c.textSecondary,
                modifier = Modifier.padding(start = 20.dp),
            )
            Text(
                "Зарегистрироваться",
                style = IronType.small,
                color = c.accent,
                modifier = Modifier.clickable(onClick = onRegister),
            )
        }
    }
}

@Composable
private fun Checkbox(checked: Boolean, onClick: () -> Unit) {
    val c = Iron.colors
    val shape = RoundedCornerShape(6.dp)
    Box(
        Modifier
            .size(22.dp)
            .glow(radius = 5.dp, corner = 6.dp)
            .clip(shape)
            .background(if (checked) c.buttonEnd else c.stepper)
            .border(1.dp, c.fieldBorder, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) Icon(Icons.Outlined.Check, null, tint = c.buttonText, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun SocialButton(icon: Int, tint: Color?, onClick: () -> Unit, modifier: Modifier) {
    val c = Iron.colors
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .height(54.dp)
            .glow(radius = 8.dp, corner = 12.dp)
            .clip(shape)
            .background(if (c.isDark) Color(0xFF0E0C1C) else c.inner)
            .border(1.dp, c.glow, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            colorFilter = tint?.let { ColorFilter.tint(it) },
        )
    }
}
