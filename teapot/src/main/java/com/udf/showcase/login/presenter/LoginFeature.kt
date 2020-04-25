package com.udf.showcase.login.presenter

import com.udf.showcase.navigation.Navigator
import com.udf.showcase.data.IApiService
import com.udf.showcase.data.IAppPrefs
import com.udf.showcase.inView
import com.udf.showcase.login.model.GetSavedUserCmd
import com.udf.showcase.login.model.GoToMainCmd
import com.udf.showcase.login.model.IsSaveCredentialsMsg
import com.udf.showcase.login.model.LoginClickMsg
import com.udf.showcase.login.model.LoginCmd
import com.udf.showcase.login.model.LoginInputMsg
import com.udf.showcase.login.model.LoginResponseMsg
import com.udf.showcase.login.model.LoginState
import com.udf.showcase.login.model.PassInputMsg
import com.udf.showcase.login.model.SaveUserCredentialsCmd
import com.udf.showcase.login.model.UserCredentialsLoadedMsg
import com.udf.showcase.login.model.UserCredentialsSavedMsg
import com.udf.showcase.login.view.ILoginView
import dev.teapot.cmd.Cmd
import dev.teapot.contract.Renderable
import dev.teapot.contract.RxFeature
import dev.teapot.contract.Update
import dev.teapot.msg.ErrorMsg
import dev.teapot.msg.Idle
import dev.teapot.msg.Init
import dev.teapot.msg.Msg
import dev.teapot.program.Program
import dev.teapot.program.ProgramBuilder
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.eclipse.egit.github.core.client.RequestException
import javax.inject.Inject

class LoginFeature @Inject constructor(
    private val loginView: ILoginView,
    programBuilder: ProgramBuilder,
    private val appPrefs: IAppPrefs,
    private val apiService: IApiService,
    private val navigator: Navigator
) : RxFeature<LoginState>, Renderable<LoginState> {

    private val program: Program<LoginState> = programBuilder.build(this)

    fun init() {
        program.run(initialState = LoginState())
    }

    override fun update(msg: Msg, state: LoginState): Update<LoginState> {
        return when (msg) {
            is Init -> Update.update(state.copy(isLoading = true), GetSavedUserCmd)
            is UserCredentialsLoadedMsg ->
                Update.update(
                    state.copy(login = msg.login, pass = msg.pass),
                    LoginCmd(msg.login, msg.pass)
                )
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
                    Update.state(
                        state.copy(
                            login = msg.login,
                            loginError = null,
                            btnEnabled = validatePass(state.pass)
                        )
                    )
            }
            is PassInputMsg -> {
                if (!validatePass(msg.pass))
                    Update.state(state.copy(pass = msg.pass, btnEnabled = false))
                else Update.state(
                    state.copy(
                        pass = msg.pass,
                        btnEnabled = validateLogin(state.login)
                    )
                )
            }
            is LoginClickMsg -> {
                if (checkLogin(state.login)) {
                    Update.state(state.copy(loginError = "Login is not valid"))
                }
                if (checkPass(state.pass)) {
                    Update.state(state.copy(passError = "Password is not valid"))
                }
                Update.update(
                    state.copy(isLoading = true, error = null),
                    LoginCmd(state.login, state.pass)
                )
            }
            is ErrorMsg -> {
                return when (msg.cmd) {
                    is GetSavedUserCmd -> Update.state(state.copy(isLoading = false))
                    is LoginCmd -> {
                        if (msg.err is RequestException) {
                            Update.state(
                                state.copy(
                                    isLoading = false,
                                    error = (msg.err as RequestException).error.message
                                )
                            )
                        }
                        Update.state(state.copy(isLoading = false, error = "Error while login"))
                    }
                    else -> Update.idle()
                }
            }
            else -> Update.idle()
        }
    }

    override fun render(state: LoginState) {
        state.apply {
            loginView.setProgress(isLoading)
            loginView.setEnableLoginBtn(btnEnabled)
            loginView.setError(error)
            loginView.showLoginError(loginError)
            loginView.showPasswordError(passError)
        }
    }

    fun render() {
        program.render()
    }

    override fun call(cmd: Cmd): Single<Msg> {
        return when (cmd) {
            is GetSavedUserCmd -> appPrefs.getUserSavedCredentials()
                .map { (login, pass) -> UserCredentialsLoadedMsg(login, pass) }
            is SaveUserCredentialsCmd -> appPrefs.saveUserSavedCredentials(cmd.login, cmd.pass)
                .map { UserCredentialsSavedMsg() }
            is LoginCmd -> apiService.login(cmd.login, cmd.pass)
                .map { logged -> LoginResponseMsg(logged) }
            is GoToMainCmd -> {
                inView {
                    navigator.goToMainScreen()
                }
            }
            else -> Single.just(Idle)
        }
    }

    fun loginBtnClick() {
        program.accept(LoginClickMsg())
    }

    fun onSaveCredentialsCheck(checked: Boolean) {
        program.accept(IsSaveCredentialsMsg(checked))
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

    fun addLoginInput(logintextViewText: Observable<CharSequence>): Disposable {
        return logintextViewText.skip(1).subscribe { login ->
            program.accept(LoginInputMsg(login.toString()))
        }
    }

    fun addPasswordInput(passValueObservable: Observable<CharSequence>): Disposable {
        return passValueObservable.skip(1).subscribe { pass ->
            program.accept(PassInputMsg(pass.toString()))
        }
    }

    fun destroy() {
        program.stop()
    }
}
