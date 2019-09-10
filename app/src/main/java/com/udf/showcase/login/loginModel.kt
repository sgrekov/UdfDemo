package com.udf.showcase.login

import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.contract.State
import com.factorymarket.rxelm.msg.Msg

data class LoginState(
    val login: String = "",
    val loginError: String? = null,
    val pass: String = "",
    val passError: String? = null,
    val saveUser: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val btnEnabled: Boolean = false
) : State()

data class UserCredentialsLoadedMsg(val login: String, val pass: String) : Msg()
class UserCredentialsSavedMsg : Msg()
data class LoginInputMsg(val login: String) : Msg()
data class PassInputMsg(val pass: String) : Msg()
data class IsSaveCredentialsMsg(val checked: Boolean) : Msg()
data class LoginResponseMsg(val logged: Boolean) : Msg()
class LoginClickMsg : Msg()

object GetSavedUserCmd : Cmd()
data class SaveUserCredentialsCmd(val login: String, val pass: String) : Cmd()
data class LoginCmd(val login: String, val pass: String) : Cmd()
object GoToMainCmd : Cmd()
