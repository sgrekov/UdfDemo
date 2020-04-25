package com.udf.showcase.login.model

sealed class LoginEvent
object LoginInit : LoginEvent()
data class UserCredentialsLoadedEvent(val login: String, val pass: String) : LoginEvent()
data class UserCredentialsErrorEvent(val err: Throwable? = null) : LoginEvent()
object UserCredentialsSavedEvent : LoginEvent()
data class LoginInputEvent(val login: String) : LoginEvent()
data class PassInputEvent(val pass: String) : LoginEvent()
data class IsSaveCredentialsEvent(val checked: Boolean) : LoginEvent()
data class LoginResponseEvent(val logged: Boolean) : LoginEvent()
data class LoginResponseErrorEvent(val err: Throwable? = null) : LoginEvent()
object LoginClickEvent : LoginEvent()
data class NetworkStateEvent(val connected : Boolean) : LoginEvent()