package com.udf.showcase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.Unbinder
import com.factorymarket.rxelm.contract.RenderableComponent
import com.factorymarket.rxelm.contract.State
import com.factorymarket.rxelm.program.Program
import com.factorymarket.rxelm.program.ProgramBuilder
import com.udf.showcase.di.ActivityComponent
import com.udf.showcase.repolist.view.MainActivity
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseFragment2<S : State> : Fragment(), RenderableComponent<S> {

    lateinit var unbinder: Unbinder
    var viewDisposables: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var programBuilder: ProgramBuilder
    lateinit var program: Program<S>
    lateinit var renderable : BaseRenderable<S>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDI()
        program = programBuilder.build(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        program.run(initialState())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        renderable = createRenderable(container!!.context)
        renderable.model = initialState()
        return renderable
    }

    abstract fun createRenderable(context: Context): BaseRenderable<S>

    override fun render(state: S) {
        renderable.render(state)
    }

    abstract fun setupDI()

    abstract fun initialState(): S

    override fun onDestroyView() {
        super.onDestroyView()
        if (!viewDisposables.isDisposed) {
            viewDisposables.dispose()
            viewDisposables = CompositeDisposable()
        }
//        unbinder.unbind()
    }

    fun getActivityComponent(): ActivityComponent {
        return (activity as? MainActivity)?.activityComponent!!
    }

    override fun onDestroy() {
        super.onDestroy()
        program.stop()
    }
}