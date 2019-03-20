package com.udf.showcase.login.model

import com.udf.showcase.login.presenter.LoginFeature

class LoginUiEventToWish : (LoginUiEvent) -> LoginFeature.LoginWish? {

    override fun invoke(event: LoginUiEvent): LoginFeature.LoginWish? {
        return when (event) {
            is LoginUiEvent.LoginEvent -> LoginFeature.LoginWish.LoginInputWish(event.login)
            is LoginUiEvent.PassEvent -> LoginFeature.LoginWish.PassInputWish(event.pass)
            LoginUiEvent.LoginClickEvent -> LoginFeature.LoginWish.StartLoginWish
            is LoginUiEvent.SaveLoginCheckBoxEvent -> LoginFeature.LoginWish.IsSaveCredentialsWish(event.checked)
        }
    }
}