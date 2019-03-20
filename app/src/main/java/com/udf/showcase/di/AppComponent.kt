package com.udf.showcase.di

import android.app.Application
import android.content.Context
import com.udf.showcase.SampleApp
import com.udf.showcase.data.AppPrefs
import com.udf.showcase.data.GitHubService
import com.udf.showcase.data.IApiService
import com.udf.showcase.data.IAppPrefs
import com.udf.showcase.main.di.ActivityComponent
import com.udf.showcase.main.di.ActivityModule
import com.udf.showcase.main.di.MainComponent
import com.udf.showcase.main.di.MainModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppComponent.AppModule::class]
)
interface AppComponent {

    fun plusActivityComponent(module: ActivityModule): ActivityComponent

    @Component.Builder
    interface Builder {

        fun build(): AppComponent

        @BindsInstance
        fun application(app: SampleApp): Builder
    }

    @Module
    class AppModule {

        @Provides
        @Singleton
        fun provideContext(app: SampleApp): Context = app

        @Provides
        @Singleton
        fun githubService(): IApiService {
            return GitHubService(Schedulers.io())
        }

        @Provides
        @Singleton
        fun appPrefs(context: Context): IAppPrefs {
            return AppPrefs(context.getSharedPreferences("prefs", Context.MODE_PRIVATE))
        }

    }

}