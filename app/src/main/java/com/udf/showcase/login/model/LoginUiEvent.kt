package com.udf.showcase.login.model

sealed class LoginUiEvent {
    data class LoginEvent(val login: String) : LoginUiEvent()
    data class PassEvent(val pass: String) : LoginUiEvent()
    object LoginClickEvent : LoginUiEvent()
    data class SaveLoginCheckBoxEvent(val checked : Boolean) : LoginUiEvent()
}