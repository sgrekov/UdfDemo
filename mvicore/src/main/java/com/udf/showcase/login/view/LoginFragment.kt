package com.udf.showcase.login.view

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import butterknife.BindView
import com.badoo.mvicore.android.AndroidBinderLifecycle
import com.badoo.mvicore.binder.Binder
import com.badoo.mvicore.binder.using
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.widget.textChanges
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.login.model.LoginUiEvent
import com.udf.showcase.login.model.LoginUiEventToWish
import com.udf.showcase.login.presenter.LoginFeature
import com.udf.showcase.navigation.Navigator
import io.reactivex.functions.Consumer
import timber.log.Timber
import javax.inject.Inject

class LoginFragment : BaseFragment<LoginUiEvent>(), Consumer<LoginFeature.LoginState> {

    val newsListener = Consumer<LoginFeature.News> { n ->
        n?.let { news ->
            when (news) {
                is LoginFeature.News.AuthError -> {
                    Toast.makeText(context, news.throwable.message, Toast.LENGTH_SHORT).show()
                }
                is LoginFeature.News.GoToMainScreen -> navigator.goToMainScreen()
            }
        }
    }

    @Inject lateinit var loginFeature: LoginFeature
    @Inject lateinit var navigator: Navigator

    @BindView(R.id.login_til) lateinit var loginInput: TextInputLayout
    @BindView(R.id.login) lateinit var loginText: TextInputEditText
    @BindView(R.id.password_til) lateinit var passwordInput: TextInputLayout
    @BindView(R.id.password) lateinit var passwordText: TextInputEditText
    @BindView(R.id.login_btn) lateinit var loginBtn: Button
    @BindView(R.id.error) lateinit var errorTxt: TextView
    @BindView(R.id.login_progress) lateinit var loginProgress: ProgressBar
    @BindView(R.id.save_credentials_cb) lateinit var saveCredentialsCb: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent()
            .inject(this)
    }

    override fun getLayoutRes(): Int = R.layout.login_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDisposables.add(
            loginText.textChanges().doOnNext { onNext(LoginUiEvent.LoginEvent(it.toString())) }.subscribe()
        )
        viewDisposables.add(
            passwordText.textChanges().doOnNext { onNext(LoginUiEvent.PassEvent(it.toString())) }.subscribe()
        )
        loginBtn.setOnClickListener { onNext(LoginUiEvent.LoginClickEvent) }
        saveCredentialsCb.setOnCheckedChangeListener { buttonView, isChecked ->
            hideKeyboard()
            onNext(LoginUiEvent.SaveLoginCheckBoxEvent(isChecked))
        }

        val binder = Binder(AndroidBinderLifecycle(lifecycle))
        binder.bind(this to loginFeature using LoginUiEventToWish())
        binder.bind(loginFeature to this)
        binder.bind(loginFeature.news to newsListener)
    }

    override fun accept(state: LoginFeature.LoginState?) {
        state?.let {
            Timber.d("login state: $state")
            setProgress(it.isLoading)
            setEnableLoginBtn(it.btnEnabled)
            setError(it.error)
            showLoginError(it.loginError)
            showPasswordError(it.passError)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginFeature.dispose()
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
