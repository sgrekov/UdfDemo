package com.udf.showcase.login.model

sealed class LoginEffect
object GetSavedUserEffect : LoginEffect()
data class SaveUserCredentialsEffect(val login: String, val pass: String) : LoginEffect()
data class LoginRequestEffect(val login: String, val pass: String) : LoginEffect()
object GoToMainEffect : LoginEffect()