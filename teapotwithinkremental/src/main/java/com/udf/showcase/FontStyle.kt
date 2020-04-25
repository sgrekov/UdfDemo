package com.udf.showcase

import android.graphics.Typeface
import dev.inkremental.dsl.android.textSize
import dev.inkremental.dsl.android.widget.TextViewScope
import dev.inkremental.dsl.androidx.core.CompatTextViewScope

object FontStyle {

    fun TextViewScope.header() {
        textSize(27f.sp)
        CompatTextViewScope.textColorCompat(R.color.black)
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    fun TextViewScope.toolbar() {
        textSize(20f.sp)
        CompatTextViewScope.textColorCompat(R.color.white)
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    fun TextViewScope.normal() {
        textSize(23f.sp)
        CompatTextViewScope.textColorCompat(R.color.black)
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    fun TextViewScope.small() {
        textSize(17f.sp)
        CompatTextViewScope.textColorCompat(R.color.black)
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }
}