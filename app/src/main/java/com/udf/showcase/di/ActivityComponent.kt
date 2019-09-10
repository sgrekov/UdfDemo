package com.udf.showcase.di

import androidx.fragment.app.FragmentActivity
import com.udf.showcase.login.LoginFragment
import com.udf.showcase.repolist.view.RepoListFragment
import com.udf.showcase.navigation.AndroidNavigator
import com.udf.showcase.navigation.Navigator
import com.udf.showcase.repo.di.RepoComponent
import com.udf.showcase.repo.di.RepoModule
import dagger.Module
import dagger.Provides
import dagger.Subcomponent

@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun plusRepoComponent(module: RepoModule): RepoComponent
    fun inject(loginFragment: LoginFragment)
    fun inject(repoListFragment: RepoListFragment)

}

@Module
class ActivityModule(private val activity: FragmentActivity) {

    @Provides
    fun navigator(): Navigator = AndroidNavigator(activity)

}