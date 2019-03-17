package com.udf.showcase.login.model

data class LoginModel(
    val login: String = "",
    val loginError: String? = null,
    val pass: String = "",
    val passError: String? = null,
    val saveUser: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val btnEnabled: Boolean = false
)