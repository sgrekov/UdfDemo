package com.udf.showcase.core

import android.content.Context
import com.factorymarket.rxelm.contract.State
import trikita.anvil.Anvil
import trikita.anvil.RenderableView

abstract class BaseRenderable<S : State>(context: Context) : RenderableView(context) {

    lateinit var model : S

    fun render(s: S) {
        model = s
        updateAdapter()
        Anvil.render(this)
    }

    open fun updateAdapter() {
        //override when have dynamic adapters in renderable
    }

}