package com.udf.showcase.login

import android.content.Context
import android.text.InputType
import androidx.core.view.ViewCompat
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.R
import com.udf.showcase.box
import com.udf.showcase.column
import com.udf.showcase.textInputEditHack
import com.udf.showcase.textWrapped
import com.udf.showcase.toolbarWidget
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.Size.*
import dev.inkremental.dsl.android.widget.checkBox
import dev.inkremental.dsl.android.widget.frameLayout
import dev.inkremental.dsl.android.widget.progressBar
import dev.inkremental.dsl.google.android.material.button.materialButton
import dev.inkremental.dsl.google.android.material.textfield.textInputLayout


class LoginRenderable(context: Context) : BaseRenderable<LoginState>(context) {

    val emailId = ViewCompat.generateViewId()
    val passwordId = ViewCompat.generateViewId()

    lateinit var loginClickListener: LoginClickListener

    override val renderable = {
        column {
            toolbarWidget("Unidirectional Dataflow Showcase")
            box {
                padding(30.dp, 30.dp)

                column(true) {
                    textInputLayout {
                        size(MATCH, WRAP)
                        hint(context.getString(R.string.e_mail_address))
                        error(model.loginError)
                        textInputEditHack(emailId) {
                            inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                            maxLines(1)
                            onTextChanged {
                                loginClickListener.onLoginChanged(it.toString())
                            }
                        }
                    }
                    textInputLayout {
                        size(MATCH, WRAP)
                        hint(context.getString(R.string.password))
                        margin(top = 20.dp)
//                        passwordVisibilityToggleEnabled(true)
                        error(model.passError)
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
                        materialButton {
                            size(WRAP, WRAP)
                            margin(top = 10.dp)
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
                        margin(10.dp)
                        layoutGravity(CENTER_HORIZONTAL)
                        visibility(model.isLoading)
                    }
                    textWrapped(model.error) {
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