package com.udf.showcase

import android.view.View
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import trikita.anvil.Anvil
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

fun verticalLayout(r: () -> Unit) {
    linearLayout {
        size(MATCH, WRAP)
        orientation(LinearLayout.VERTICAL)
        r()
    }
}

fun horizontalLayout(r: () -> Unit) {
    linearLayout {
        size(MATCH, WRAP)
        orientation(LinearLayout.HORIZONTAL)
        r()
    }
}

fun column(r: () -> Unit) {
    linearLayout {
        orientation(LinearLayout.VERTICAL)
        r()
    }
}

fun row(r: () -> Unit) {
    linearLayout {
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