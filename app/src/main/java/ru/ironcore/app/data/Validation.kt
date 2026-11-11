package ru.ironcore.app.data

private val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

fun isValidEmail(email: String): Boolean = emailPattern.matches(email.trim())

fun emailError(email: String): String? =
    if (email.isBlank() || isValidEmail(email)) null else "Введите email полностью, например name@mail.ru"
