package com.udf.showcase

import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import trikita.anvil.Anvil
import trikita.anvil.BaseDSL
import trikita.anvil.DSL
import trikita.anvil.DSL.*

inline fun <T : View> initWith(crossinline init: T.() -> Unit) {
    init {
        val v = Anvil.currentView<T>()
        v.init()
    }
}


fun textInputEditHack(id: Int, f: () -> Unit) {
    initWith<TextInputLayout> {
        addView(
            TextInputEditText(this.context).also { it.id = id },
            this.childCount,
            LinearLayout.LayoutParams(MATCH, WRAP)
        )
    }

    withId(id) {
        f()
    }
}

fun box(r: () -> Unit) {
    frameLayout {
        size(MATCH, MATCH)
        r()
    }
}

fun column(stretched : Boolean = true, r: () -> Unit) {
    linearLayout {
        if (stretched) {
            size(MATCH, WRAP)
        } else {
            size(WRAP, WRAP)
        }
        orientation(LinearLayout.VERTICAL)
        r()
    }
}

fun textWrapped(text : String?, r : () -> Unit) {
    textView {
        BaseDSL.size(WRAP, WRAP)
        text(text)
        r()
    }
}

fun textWrapped(@StringRes textRes : Int, r : () -> Unit) {
    textView {
        BaseDSL.size(WRAP, WRAP)
        text(textRes)
        r()
    }
}

fun row(stretched : Boolean = false, r: () -> Unit) {
    linearLayout {
        if (stretched) {
            size(MATCH, WRAP)
        } else {
            size(WRAP, WRAP)
        }
        orientation(LinearLayout.HORIZONTAL)
        r()
    }
}

fun marginTop(top: Int) {
    margin(0, top, 0, 0)
}

fun marginLeft(left: Int) {
    margin(left, 0, 0, 0)
}

fun marginBottom(bottom: Int) {
    margin(0, 0, 0, bottom)
}

fun marginRight(right: Int) {
    margin(0, 0, right, 0)
}

fun toolbarWidget(title: String) {
    toolbar {
        size(BaseDSL.MATCH, WRAP)
        backgroundColor(colorCompat(R.color.colorPrimary))
        elevation(dip(5).toFloat())

        frameLayout {
            size(MATCH, WRAP)
            textView {
                FontStyle.toolbar()
                text(title)
            }
        }
    }
}

fun colorCompat(@ColorRes colorRes: Int): Int {
    return ResourcesCompat.getColor(BaseDSL.R(), colorRes, null)
}