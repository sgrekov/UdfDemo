package com.udf.showcase.login.presenter

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.udf.showcase.data.IApiService
import com.udf.showcase.data.IAppPrefs
import io.reactivex.Observable
import javax.inject.Inject

class LoginFeature @Inject constructor(
    private val appPrefs: IAppPrefs,
    private val apiService: IApiService
) : ActorReducerFeature<LoginFeature.LoginWish, LoginFeature.LoginEffect, LoginFeature.LoginState, Nothing>(
    initialState = LoginState(),
//        bootstrapper = BootStrapperImpl(),
    actor = ActorImpl(apiService, appPrefs),
    reducer = ReducerImpl()
) {

    data class LoginState(
        val login: String = "",
        val loginError: String? = null,
        val pass: String = "",
        val passError: String? = null,
        val saveUser: Boolean = false,
        val isLoading: Boolean = true,
        val error: String? = null,
        val btnEnabled: Boolean = false
    )

    sealed class LoginEffect {
        data class LoginInputEffect(val login: String) : LoginEffect()
        data class PassInputEffect(val pass: String) : LoginEffect()
        //        data class UserCredentialsLoadedEffect(val login: String, val pass: String) : LoginEffect()
//        class UserCredentialsSavedEffect : LoginEffect()
//        data class IsSaveCredentialsEffect(val checked: Boolean) : LoginEffect()
        data class LoginResponseEffect(val logged: Boolean) : LoginEffect()

        class LoginClickEffect : LoginEffect()
        data class ValidateErrorEffect(val loginFailed: Boolean = false, val passFailed: Boolean = false) :
            LoginEffect()

        object LoginStarted : LoginEffect()
        data class LoginError(val error : Throwable) : LoginEffect()
    }

    sealed class LoginWish {
        data class LoginInputWish(val login: String) : LoginWish()
        data class PassInputWish(val pass: String) : LoginWish()

        //        object GetSavedUserWish : LoginWish()
//        data class SaveUserCredentialsWish(val login: String, val pass: String) : LoginWish()
        data class StartLoginWish(val login: String, val pass: String) : LoginWish()

        object GoToMainWish : LoginWish()
    }

    sealed class News {
        data class ErrorExecutingRequest(val throwable: Throwable) : News()
    }

//    class BootStrapperImpl : Bootstrapper<Wish> {
//        override fun invoke(): Observable<Wish> = Observable.just(LoadNewImage)
//    }

    class ActorImpl(val apiService: IApiService, val appPrefs: IAppPrefs) : Actor<LoginState, LoginWish, LoginEffect> {

        override fun invoke(state: LoginState, wish: LoginWish): Observable<LoginEffect> = when (wish) {
            is LoginWish.LoginInputWish -> Observable.just(LoginEffect.LoginInputEffect(wish.login))
            is LoginWish.PassInputWish -> Observable.just(LoginEffect.PassInputEffect(wish.pass))
            is LoginWish.StartLoginWish -> when {
                !checkLogin(wish.login) -> Observable.just(LoginEffect.ValidateErrorEffect(loginFailed = true) as LoginEffect)
                !checkPass(wish.pass) -> Observable.just(LoginEffect.ValidateErrorEffect(passFailed = true) as LoginEffect)
                else -> apiService.login(wish.login, wish.pass)
                    .toObservable()
                    .map { logged -> LoginEffect.LoginResponseEffect(logged) as LoginEffect }
                    .startWith(Observable.just(LoginEffect.LoginStarted) as LoginEffect)
                    .onErrorReturn { LoginEffect.LoginError(it) }
            }
//            LoginWish.GetSavedUserWish -> appPrefs.getUserSavedCredentials()
//                .toObservable()
//                .map { (login, pass) -> LoginEffect.UserCredentialsLoadedEffect(login, pass) }
//            is LoginWish.SaveUserCredentialsWish -> appPrefs.saveUserSavedCredentials(wish.login, wish.pass)
//                .toObservable()
//                .map { LoginEffect.UserCredentialsSavedEffect() }
            LoginWish.GoToMainWish -> TODO()

        }

        private fun checkPass(pass: CharSequence): Boolean {
            return (pass.startsWith("42") || pass == "qwerty")
        }

        private fun checkLogin(login: CharSequence): Boolean {
            return (login.startsWith("42") || login == "admin")
        }

    }

    class ReducerImpl : Reducer<LoginState, LoginEffect> {
        override fun invoke(state: LoginState, effect: LoginEffect): LoginState = when (effect) {
//            is Init -> state.copy(isLoading = true) to GetSavedUserCmd
//            is LoginEffect.UserCredentialsLoadedEffect ->
//                state.copy(login = effect.login, pass = effect.pass) to LoginCmd(msg.login, msg.pass)
            is LoginEffect.LoginResponseEffect -> {
                if (state.saveUser) {
                    state to SaveUserCredentialsCmd(state.login, state.pass)
                } else {
                    state to GoToMainCmd
                }
            }
//            is LoginEffect.UserCredentialsSavedEffect -> state to GoToMainCmd
//            is LoginEffect.IsSaveCredentialsEffect -> Pair(state.copy(saveUser = msg.checked), None)
            is LoginEffect.LoginInputEffect -> {
                if (!validateLogin(effect.login)) {
                    state.copy(login = effect.login, btnEnabled = false)
                } else state.copy(login = effect.login, loginError = null, btnEnabled = validatePass(state.pass))
            }
            is LoginEffect.PassInputEffect -> {
                if (!validatePass(effect.pass)) {
                    state.copy(pass = effect.pass, btnEnabled = false)
                } else state.copy(pass = effect.pass, btnEnabled = validateLogin(state.login))
            }
            is LoginEffect.LoginClickEffect -> {
//                state . copy (loginError =
//                "Login is not valid")
//            }
//            if (checkPass(state.pass)) {
//                state.copy(passError = "Password is not valid")
//            }
                state.copy(isLoading = true, error = null)
            }
        }

        private fun validatePass(pass: CharSequence): Boolean {
            return pass.length > 4
        }

        private fun validateLogin(login: CharSequence): Boolean {
            return login.length > 3
        }
    }

//    class NewsPublisherImpl : NewsPublisher<Wish, LoginEffect, State, News> {
//        override fun invoke(wish: Wish, effect: LoginEffect, state: State): News? = when (effect) {
//            is ErrorLoading -> News.ErrorExecutingRequest(effect.throwable)
//            else -> null
//        }
//    }


}