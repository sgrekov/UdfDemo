package com.udf.showcase.login.presenter

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.NewsPublisher
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.udf.showcase.data.IApiService
import com.udf.showcase.data.IAppPrefs
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.observable.ObservableRange
import timber.log.Timber
import javax.inject.Inject

class LoginFeature @Inject constructor(
    private val appPrefs: IAppPrefs,
    private val apiService: IApiService
) : ActorReducerFeature<LoginFeature.LoginWish, LoginFeature.LoginEffect, LoginFeature.LoginState, LoginFeature.News>(
    initialState = LoginState(),
    actor = ActorImpl(apiService, appPrefs),
    bootstrapper = BootStrapperImpl(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
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
        object UserCredentialsLoadedEffect : LoginEffect()
        data class IsSaveCredentialsEffect(val checked: Boolean) : LoginEffect()

        data class LoginResponseEffect(val logged: Boolean) : LoginEffect()

        data class ValidateErrorEffect(val loginFailed: Boolean = false, val passFailed: Boolean = false) :
            LoginEffect()

        object LoginStarted : LoginEffect()
        data class LoginError(val error: Throwable) : LoginEffect()
    }

    sealed class LoginWish {
        data class LoginInputWish(val login: String) : LoginWish()
        data class PassInputWish(val pass: String) : LoginWish()

        object GetSavedUserWish : LoginWish()
        object StartLoginWish : LoginWish()
        data class IsSaveCredentialsWish(val checked: Boolean) : LoginWish()
    }

    sealed class News {
        data class AuthError(val throwable: Throwable) : News()
        object GoToMainScreen : News()
    }

    class BootStrapperImpl : Bootstrapper<LoginWish> {
        override fun invoke(): Observable<LoginWish> = Observable.just(LoginWish.GetSavedUserWish)
    }

    class ActorImpl(val apiService: IApiService, val appPrefs: IAppPrefs) : Actor<LoginState, LoginWish, LoginEffect> {

        override fun invoke(state: LoginState, wish: LoginWish): Observable<LoginEffect> = when (wish) {
            is LoginWish.LoginInputWish -> Observable.just(LoginEffect.LoginInputEffect(wish.login))
            is LoginWish.PassInputWish -> Observable.just(LoginEffect.PassInputEffect(wish.pass))
            is LoginWish.StartLoginWish ->
                try { //TODO: remove this mess
                    when {
                        checkLogin(state.login) -> Observable.just(LoginEffect.ValidateErrorEffect(loginFailed = true) as LoginEffect)
                        checkPass(state.pass) -> Observable.just(LoginEffect.ValidateErrorEffect(passFailed = true) as LoginEffect)
                        else -> apiService.login(state.login, state.pass)
                            .flatMap { Single.just(LoginEffect.LoginResponseEffect(it) as LoginEffect) }
                            .flatMapObservable { effect ->
                                if (state.saveUser) {
                                    appPrefs
                                        .saveUserSavedCredentials(state.login, state.pass)
                                        .flatMapObservable { Observable.just(effect) }
                                } else {
                                    Observable.just(effect)
                                }
                            }
                            .startWith(Observable.just(LoginEffect.LoginStarted))
                            .onErrorReturn { LoginEffect.LoginError(it) }
                            .observeOn(AndroidSchedulers.mainThread())
                    }
                } catch (e: Throwable) {
                    Timber.e(e)
                    Observable.just(LoginEffect.LoginError(e) as LoginEffect)
                }
            LoginWish.GetSavedUserWish -> appPrefs.getUserSavedCredentials()
                .flatMap { (login, pass) ->
                    apiService.login(login, pass)
                }
                .flatMapObservable { Observable.just(LoginEffect.LoginResponseEffect(it) as LoginEffect) }
                .onErrorReturn { LoginEffect.UserCredentialsLoadedEffect }
                .observeOn(AndroidSchedulers.mainThread())
            is LoginWish.IsSaveCredentialsWish -> Observable.just(LoginEffect.IsSaveCredentialsEffect(wish.checked))
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
            is LoginEffect.IsSaveCredentialsEffect -> state.copy(saveUser = effect.checked)
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
            is LoginEffect.ValidateErrorEffect -> when {
                effect.loginFailed -> state.copy(loginError = "Login is not valid")
                effect.passFailed -> state.copy(passError = "Password is not valid")
                else -> state
            }
            is LoginEffect.LoginStarted -> state.copy(isLoading = true, error = null)
            is LoginEffect.LoginResponseEffect -> state.copy(isLoading = false)
            is LoginEffect.LoginError -> state.copy(isLoading = false)
            is LoginEffect.UserCredentialsLoadedEffect -> state.copy(isLoading = false)
        }

        private fun validatePass(pass: CharSequence): Boolean {
            return pass.length > 4
        }

        private fun validateLogin(login: CharSequence): Boolean {
            return login.length > 3
        }

    }


    class NewsPublisherImpl :
        NewsPublisher<LoginFeature.LoginWish, LoginFeature.LoginEffect, LoginFeature.LoginState, LoginFeature.News> {
        override fun invoke(
            wish: LoginFeature.LoginWish,
            effect: LoginFeature.LoginEffect,
            state: LoginFeature.LoginState
        ): LoginFeature.News? = when (effect) {
            is LoginEffect.LoginError -> News.AuthError(effect.error)
            is LoginEffect.LoginResponseEffect -> News.GoToMainScreen
            else -> null
        }
    }
}