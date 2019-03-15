package com.udf.showcase

import android.app.Application
import com.udf.showcase.data.GitHubService
import com.udf.showcase.di.AppComponent
import com.udf.showcase.di.DaggerAppComponent
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SampleApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException) {
                Timber.e(throwable.cause)
            }
        }

        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()

        Timber.plant(Timber.DebugTree())
    }
}
