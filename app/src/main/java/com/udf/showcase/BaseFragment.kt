package com.udf.showcase

import androidx.fragment.app.Fragment
import butterknife.Unbinder
import com.udf.showcase.di.ActivityComponent
import com.udf.showcase.repolist.view.MainActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {

    lateinit var unbinder: Unbinder
    var viewDisposables: CompositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()
        if (!viewDisposables.isDisposed) {
            viewDisposables.dispose()
            viewDisposables = CompositeDisposable()
        }
        unbinder.unbind()
    }

    fun getActivityComponent(): ActivityComponent {
        return (activity as? MainActivity)?.activityComponent!!
    }
}