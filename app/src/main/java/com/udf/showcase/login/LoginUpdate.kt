package com.udf.showcase.login

import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.cmd.None
import com.factorymarket.rxelm.msg.ErrorMsg
import com.factorymarket.rxelm.msg.Init
import com.factorymarket.rxelm.msg.Msg
import org.eclipse.egit.github.core.client.RequestException

fun loginUpdate(msg: Msg, state: LoginState): Pair<LoginState, Cmd> {
    return when (msg) {
        is Init -> state.copy(isLoading = true) to GetSavedUserCmd
        is UserCredentialsLoadedMsg ->
            state.copy(login = msg.login, pass = msg.pass) to LoginCmd(msg.login, msg.pass)
        is LoginResponseMsg -> {
            if (state.saveUser) {
                state to SaveUserCredentialsCmd(state.login, state.pass)
            } else {
                state to GoToMainCmd
            }
        }
        is UserCredentialsSavedMsg -> state to GoToMainCmd
        is IsSaveCredentialsMsg -> Pair(state.copy(saveUser = msg.checked), None)
        is LoginInputMsg -> {
            if (!validateLogin(msg.login))
                state.copy(login = msg.login, btnEnabled = false) to None
            else
                state.copy(
                    login = msg.login,
                    loginError = null,
                    btnEnabled = validatePass(state.pass)
                ) to None
        }
        is PassInputMsg -> {
            if (!validatePass(msg.pass))
                state.copy(pass = msg.pass, btnEnabled = false) to None
            else
                state.copy(pass = msg.pass, btnEnabled = validateLogin(state.login)) to None
        }
        is LoginClickMsg -> {
            if (checkLogin(state.login)) {
                state.copy(loginError = "Login is not valid") to None
            }
            if (checkPass(state.pass)) {
                state.copy(passError = "Password is not valid") to None
            }
            state.copy(isLoading = true, error = null) to LoginCmd(state.login, state.pass)
        }
        is ErrorMsg -> {
            return when (msg.cmd) {
                is GetSavedUserCmd -> state.copy(isLoading = false) to None
                is LoginCmd -> {
                    if (msg.err is RequestException) {
                        state.copy(
                            isLoading = false,
                            error = (msg.err as RequestException).error.message
                        ) to None
                    }
                    state.copy(isLoading = false, error = "Error while login") to None
                }
                else -> state to None
            }
        }
        else -> state to None
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