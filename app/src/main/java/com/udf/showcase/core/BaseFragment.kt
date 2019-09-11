package com.udf.showcase.core

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.factorymarket.rxelm.contract.RenderableComponent
import com.factorymarket.rxelm.contract.State
import com.factorymarket.rxelm.msg.Msg
import com.factorymarket.rxelm.program.Program
import com.factorymarket.rxelm.program.ProgramBuilder
import com.udf.showcase.MainActivity
import com.udf.showcase.di.ActivityComponent
import javax.inject.Inject

object ResumeMsg : Msg()

abstract class BaseFragment<S : State> : Fragment(), RenderableComponent<S> {

    @Inject lateinit var programBuilder: ProgramBuilder
    lateinit var program: Program<S>
    var renderable: BaseRenderable<S>? = null
    var started = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDI()
        program = programBuilder.build(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (renderable == null) {
            renderable = createRenderable(container!!.context)
            renderable?.model = initialState()
        }
        return renderable
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!started) {
            program.run(initialState())
            started = true
        } else {
            program.accept(ResumeMsg)
        }
    }

    abstract fun createRenderable(context: Context): BaseRenderable<S>

    override fun render(state: S) {
        renderable?.render(state)
    }

    abstract fun setupDI()

    abstract fun initialState(): S

    fun getActivityComponent(): ActivityComponent {
        return (activity as? MainActivity)?.activityComponent!!
    }

    override fun onDestroy() {
        super.onDestroy()
        program.stop()
    }
}