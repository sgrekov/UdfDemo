package com.udf.showcase.main.di

import android.support.v4.app.FragmentActivity
import com.udf.showcase.login.view.LoginFragment
import com.udf.showcase.navigation.AndroidNavigator
import com.udf.showcase.navigation.Navigator
import com.udf.showcase.repo.di.RepoComponent
import com.udf.showcase.repo.di.RepoModule
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {
    fun plusMainComponent(module: MainModule): MainComponent
    fun plusRepoComponent(module: RepoModule): RepoComponent
    fun inject(loginFragment: LoginFragment)

}

@Module
class ActivityModule(private val activity: FragmentActivity) {

    @Provides
    fun navigator(): Navigator = AndroidNavigator(activity)

}