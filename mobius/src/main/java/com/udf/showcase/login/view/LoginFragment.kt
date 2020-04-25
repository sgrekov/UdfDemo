package com.udf.showcase.login.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.spotify.mobius.EventSource
import com.spotify.mobius.First
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.rx2.RxConnectables
import com.spotify.mobius.rx2.RxEventSources
import com.spotify.mobius.rx2.RxMobius
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.data.IApiService
import com.udf.showcase.data.IAppPrefs
import com.udf.showcase.login.model.*
import com.udf.showcase.navigation.Navigator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class LoginFragment : BaseFragment() {

    @BindView(R.id.login_til) lateinit var loginInput: TextInputLayout
    @BindView(R.id.login) lateinit var loginText: TextInputEditText
    @BindView(R.id.password_til) lateinit var passwordInput: TextInputLayout
    @BindView(R.id.password) lateinit var passwordText: TextInputEditText
    @BindView(R.id.login_btn) lateinit var loginBtn: Button
    @BindView(R.id.error) lateinit var errorTxt: TextView
    @BindView(R.id.login_progress) lateinit var loginProgress: ProgressBar
    @BindView(R.id.save_credentials_cb) lateinit var saveCredentialsCb: CheckBox

    @Inject lateinit var prefs: IAppPrefs
    @Inject lateinit var api: IApiService
    @Inject lateinit var navigator: Navigator

    var rxEffectHandler =
        RxMobius.subtypeEffectHandler<LoginEffect, LoginEvent>()
            .addTransformer(GetSavedUserEffect::class.java, this::handleGetUserSavedCredentials)
            .addTransformer(SaveUserCredentialsEffect::class.java, this::handleSaveUserSavedCredentials)
            .addTransformer(LoginRequestEffect::class.java, this::handleLoginRequest)
            .addAction(GoToMainEffect::class.java, this::handleNavigateToMainScreen, AndroidSchedulers.mainThread())
            .build()

    val networkObservable: Observable<LoginEvent> = Observable.just(NetworkStateEvent(true))

    val eventSource: EventSource<LoginEvent> = RxEventSources.fromObservables(networkObservable)

    var loopFactory: MobiusLoop.Factory<LoginModel, LoginEvent, LoginEffect> =
        RxMobius
            .loop(LoginUpdate(), rxEffectHandler)
            .init {
                First.first(LoginModel(), setOf(GetSavedUserEffect))
            }
            .eventSource(eventSource)
            .logger(AndroidLogger.tag<LoginModel, LoginEvent, LoginEffect>("my_app"))

    private val controller: MobiusLoop.Controller<LoginModel, LoginEvent> =
        MobiusAndroid.controller(loopFactory, LoginModel())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent()
            .inject(this)
    }

    override fun getLayoutRes(): Int = R.layout.login_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.connect(RxConnectables.fromTransformer(this::connectViews))
    }

    fun connectViews(models: Observable<LoginModel>): Observable<LoginEvent> {
        val disposable = models
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { render(it) }

        val loginBtnClick = loginBtn.clicks()
            .map { LoginClickEvent as LoginEvent }
        val loginText = loginText.textChanges()
            .map { LoginInputEvent(it.toString()) as LoginEvent }
        val passText = passwordText.textChanges()
            .map { PassInputEvent(it.toString()) as LoginEvent }
        val saveCreds = saveCredentialsCb.checkedChanges()
            .map { IsSaveCredentialsEvent(it) }

        return Observable
            .merge(listOf(loginBtnClick, loginText, passText, saveCreds))
            .doOnDispose(disposable::dispose)
    }

    fun render(state: LoginModel) {
        state.apply {
            setProgress(isLoading)
            setEnableLoginBtn(btnEnabled)
            setError(error)
            showLoginError(loginError)
            showPasswordError(passError)
        }
    }

    fun handleGetUserSavedCredentials(request: Observable<GetSavedUserEffect>): Observable<LoginEvent> {
        return prefs.getUserSavedCredentials()
            .map { (login, pass) ->
                UserCredentialsLoadedEvent(
                    login,
                    pass
                ) as LoginEvent
            }
            .toObservable()
            .onErrorReturn { return@onErrorReturn UserCredentialsErrorEvent(err = it) }
    }

    fun handleSaveUserSavedCredentials(request: Observable<SaveUserCredentialsEffect>): Observable<LoginEvent> {

        return request.flatMap { effect ->
            prefs.saveUserSavedCredentials(effect.login, effect.pass)
                .map { saved -> UserCredentialsSavedEvent }.toObservable()
        }
    }

    fun handleLoginRequest(request: Observable<LoginRequestEffect>): Observable<LoginEvent> {

        return request.flatMap { effect ->
            api.login(effect.login, effect.pass)
                .map { logged -> LoginResponseEvent(logged) as LoginEvent }
                .onErrorReturn { LoginResponseErrorEvent(it) }
                .toObservable()
        }
    }

    fun handleNavigateToMainScreen() {
        navigator.goToMainScreen()
    }

    override fun onResume() {
        super.onResume()
        controller.start()
    }

    override fun onPause() {
        controller.stop()
        super.onPause()
    }

    override fun onDestroyView() {
        controller.disconnect()
        super.onDestroyView()
    }


    fun setProgress(show: Boolean) {
        loginProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun showPasswordError(errorText: String?) {
        errorText?.let {
            passwordInput.error = errorText
        } ?: run {
            passwordInput.error = ""
        }
    }

    fun showLoginError(errorText: String?) {
        errorText?.let {
            loginInput.error = errorText
        } ?: run {
            loginInput.error = ""
        }
    }

    fun setError(error: String?) {
        error?.let {
            errorTxt.visibility = View.VISIBLE
            errorTxt.text = error
        } ?: run {
            errorTxt.visibility = View.GONE
        }
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        imm?.hideSoftInputFromWindow(loginText.windowToken, 0)
    }

    fun setEnableLoginBtn(enabled: Boolean) {
        loginBtn.isEnabled = enabled
    }


}
