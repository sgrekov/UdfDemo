package com.udf.showcase.main.di

import com.udf.showcase.login.view.ILoginView
import com.udf.showcase.login.view.LoginFragment
import com.udf.showcase.main.view.IMainView
import com.udf.showcase.main.view.MainFragment
import dagger.Module
import dagger.Provides
import dagger.Subcomponent


@Subcomponent(modules = [MainModule::class])
interface MainComponent {

    fun inject(mainFragment: MainFragment)

}

@Module
class MainModule(private val mainFragment: MainFragment) {

    @Provides
    fun mainView(): IMainView = mainFragment

}