package com.udf.showcase.core

import android.content.Context
import android.widget.FrameLayout
import dev.inkremental.Inkremental
import dev.inkremental.RenderableView
import dev.teapot.contract.State

abstract class BaseRenderable<S : State>(context: Context) : RenderableView(context) {

    lateinit var model : S

    fun render(s: S) {
        model = s
        updateAdapter()
        Inkremental.render(this)
    }

    open fun updateAdapter() {
        //override when have dynamic adapters in renderable
    }

}