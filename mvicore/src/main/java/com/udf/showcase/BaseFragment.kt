package com.udf.showcase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import butterknife.ButterKnife
import butterknife.Unbinder
import com.udf.showcase.main.di.ActivityComponent
import com.udf.showcase.main.view.MainActivity
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseFragment<T> : Fragment(), ObservableSource<T> {

    lateinit var unbinder: Unbinder
    var viewDisposables: CompositeDisposable = CompositeDisposable()

    private val source = PublishSubject.create<T>()

    protected fun onNext(t: T) {
        source.onNext(t)
    }

    override fun subscribe(observer: Observer<in T>) {
        source.subscribe(observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutRes(), container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    abstract fun getLayoutRes(): Int

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