package com.udf.showcase.login

import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.cmd.None
import com.factorymarket.rxelm.contract.Update
import com.factorymarket.rxelm.msg.ErrorMsg
import com.factorymarket.rxelm.msg.Init
import com.factorymarket.rxelm.msg.Msg
import org.eclipse.egit.github.core.client.RequestException

fun loginUpdate(msg: Msg, state: LoginState): Update<LoginState> {
    return when (msg) {
        is Init -> Update.update(state.copy(isLoading = true), GetSavedUserCmd)
        is UserCredentialsLoadedMsg ->
            Update.update(state.copy(login = msg.login, pass = msg.pass), LoginCmd(msg.login, msg.pass))
        is LoginResponseMsg -> {
            if (state.saveUser) {
                Update.update(state, SaveUserCredentialsCmd(state.login, state.pass))
            } else {
                Update.update(state, GoToMainCmd)
            }
        }
        is UserCredentialsSavedMsg -> Update.effect(GoToMainCmd)
        is IsSaveCredentialsMsg -> Update.state(state.copy(saveUser = msg.checked))
        is LoginInputMsg -> {
            if (!validateLogin(msg.login))
                Update.state(state.copy(login = msg.login, btnEnabled = false))
            else
                Update.state(state.copy(
                    login = msg.login,
                    loginError = null,
                    btnEnabled = validatePass(state.pass)
                ))
        }
        is PassInputMsg -> {
            Update.state(if (!validatePass(msg.pass))
                state.copy(pass = msg.pass, btnEnabled = false)
            else
                state.copy(pass = msg.pass, btnEnabled = validateLogin(state.login))
            )
        }
        is LoginClickMsg -> {
            if (checkLogin(state.login)) {
                Update.state(state.copy(loginError = "Login is not valid"))
            }
            if (checkPass(state.pass)) {
                Update.state(state.copy(passError = "Password is not valid"))
            }
            Update.update(state.copy(isLoading = true, error = null), LoginCmd(state.login, state.pass))
        }
        is ErrorMsg -> {
            return when (msg.cmd) {
                is GetSavedUserCmd -> Update.state(state.copy(isLoading = false))
                is LoginCmd -> {
                    if (msg.err is RequestException) {
                        Update.state(state.copy(
                            isLoading = false,
                            error = (msg.err as RequestException).error.message
                        ))
                    }
                    Update.state(state.copy(isLoading = false, error = "Error while login"))
                }
                else -> Update.idle()
            }
        }
        else -> Update.idle()
    }
}

private fun validatePass(pass: CharSequence): Boolean {
    return pass.length > 4
}

private fun validateLogin(login: CharSequence): Boolean {
    return login.length > 3
}

private fun checkPass(pass: CharSequence): Boolean {
    return (pass.startsWith("42") || pass == "qwerty")
}

private fun checkLogin(login: CharSequence): Boolean {
    return (login.startsWith("42") || login == "admin")
}