package com.udf.showcase.login

import android.content.Context
import android.text.InputType
import androidx.core.view.ViewCompat
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.R
import com.udf.showcase.column
import com.udf.showcase.marginTop
import com.udf.showcase.textInputEditHack
import com.udf.showcase.toolbarWidget
import com.udf.showcase.verticalLayout
import trikita.anvil.BaseDSL.MATCH
import trikita.anvil.BaseDSL.size
import trikita.anvil.DSL
import trikita.anvil.DSL.*
import trikita.anvil.material.MaterialDSL


class LoginRenderable(context: Context) : BaseRenderable<LoginState>(context) {

    val emailId = ViewCompat.generateViewId()
    val passwordId = ViewCompat.generateViewId()

    lateinit var loginClickListener: LoginClickListener

    override fun view() {
        verticalLayout {
            toolbarWidget("Unidirectional Dataflow Showcase")
            frameLayout {
                size(MATCH, MATCH)
                padding(dip(30), dip(30))
                column {
                    MaterialDSL.textInputLayout {
                        DSL.size(DSL.MATCH, WRAP)
                        hint(context.getString(R.string.e_mail_address))
                        MaterialDSL.error(model.loginError)
                        textInputEditHack(emailId) {
                            inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                            maxLines(1)
                            onTextChanged {
                                loginClickListener.onLoginChanged(it.toString())
                            }
                        }
                    }
                    MaterialDSL.textInputLayout {
                        DSL.size(DSL.MATCH, WRAP)
                        hint(context.getString(R.string.password))
                        marginTop(dip(20))
                        MaterialDSL.passwordVisibilityToggleEnabled(true)
                        MaterialDSL.error(model.passError)
                        textInputEditHack(passwordId) {
                            inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            maxLines(1)
                            onTextChanged {
                                loginClickListener.onPasswordChanged(it.toString())
                            }
                        }
                    }
                    frameLayout {
                        size(MATCH, WRAP)
                        MaterialDSL.materialButton {
                            size(WRAP, WRAP)
                            marginTop(dip(10))
                            layoutGravity(START)
                            text("Login")
                            enabled(model.btnEnabled)
                            onClick {
                                loginClickListener.onLoginClicked()
                            }
                        }
                        checkBox {
                            size(WRAP, WRAP)
                            text("Save login")
                            layoutGravity(END or CENTER_VERTICAL)
                            onCheckedChange { _, isChecked: Boolean ->
                                loginClickListener.onSaveLoginCheckClick(isChecked)
                            }
                        }
                    }
                    progressBar {
                        marginTop(dip(10))
                        layoutGravity(CENTER_HORIZONTAL)
                        visibility(model.isLoading)
                    }
                    textView {
                        size(WRAP, WRAP)
                        text(model.error)
                        visibility(model.error != null)
                    }

                }
            }
        }
    }
}

interface LoginClickListener {
    fun onLoginChanged(login: String)
    fun onPasswordChanged(password: String)
    fun onLoginClicked()
    fun onSaveLoginCheckClick(checked: Boolean)

}