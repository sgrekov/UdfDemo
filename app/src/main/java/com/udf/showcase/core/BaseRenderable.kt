package com.udf.showcase.core

import android.content.Context
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.widget.FrameLayout
import com.factorymarket.rxelm.contract.State
import timber.log.Timber
import trikita.anvil.Anvil
import trikita.anvil.RenderableView

abstract class BaseRenderable<S : State>(context: Context) : FrameLayout(context), Anvil.Renderable {

    lateinit var model : S

    fun render(s: S) {
        model = s
        updateAdapter()
        Anvil.render(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Anvil.mount<BaseRenderable<*>>(this, this)
    }

    open fun updateAdapter() {
        //override when have dynamic adapters in renderable
    }

}