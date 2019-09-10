package com.udf.showcase

import android.graphics.Typeface
import trikita.anvil.DSL.sip
import trikita.anvil.DSL.textColor
import trikita.anvil.DSL.textSize
import trikita.anvil.DSL.typeface

object FontStyle {

    fun header() {
        textSize(sip(27f))
        textColor(colorCompat(R.color.black))
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    fun toolbar() {
        textSize(sip(20f))
        textColor(colorCompat(R.color.white))
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    fun normal() {
        textSize(sip(23f))
        textColor(colorCompat(R.color.black))
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }

    fun small() {
        textSize(sip(17f))
        textColor(colorCompat(R.color.black))
        typeface(Typeface.create("sans-serif-medium", Typeface.NORMAL))
    }
}