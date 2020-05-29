package com.udf.showcase

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.udf.showcase.FontStyle.toolbar
import dev.inkremental.Inkremental
import dev.inkremental.dsl.android.Size
import dev.inkremental.dsl.android.Size.*
import dev.inkremental.dsl.android.init
import dev.inkremental.dsl.android.size
import dev.inkremental.dsl.android.text
import dev.inkremental.dsl.android.view.ViewGroupScope
import dev.inkremental.dsl.android.view.ViewScope
import dev.inkremental.dsl.android.widget.*
import dev.inkremental.dsl.androidx.core.CompatViewScope
import dev.inkremental.withId

inline fun <T : View> ViewScope.initWith(crossinline viewInit: T.() -> Unit) {
    init {
        val v = Inkremental.currentView<T>()
        v?.viewInit()
    }
}


fun ViewScope.textInputEditHack(id: Int, f: TextViewScope.() -> Unit) {
    initWith<TextInputLayout> {
        addView(
            TextInputEditText(this.context).also { it.id = id },
            this.childCount,
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        withId(id) {
            f(TextViewScope)
        }
    }
}

fun box(r: ViewScope.() -> Unit) {
    frameLayout {
        size(MATCH, MATCH)
        r()
    }
}

fun column(stretched : Boolean = true, r: ViewGroupScope.() -> Unit) {
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

fun textWrapped(text : String?, r : TextViewScope.() -> Unit) {
    textView {
        size(WRAP, WRAP)
        text(text)
        r()
    }
}

fun textWrapped(@StringRes textRes : Int, r : () -> Unit) {
    textView {
        size(WRAP, WRAP)
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


fun toolbarWidget(title: String) {
    toolbar {
        size(MATCH, WRAP)
        CompatViewScope.backgroundColorCompat(R.color.colorPrimary)
//        elevation()

        frameLayout {
            size(MATCH, WRAP)
            textView {
                toolbar()
                text(title)
            }
        }
    }
}
