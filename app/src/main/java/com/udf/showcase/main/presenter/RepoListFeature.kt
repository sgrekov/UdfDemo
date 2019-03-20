package com.udf.showcase.main.presenter

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.udf.showcase.data.IApiService
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.android.schedulers.AndroidSchedulers
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject


class RepoListFeature @Inject constructor(
    private val apiService: IApiService
) : ActorReducerFeature<RepoListFeature.RepoListWish, RepoListFeature.RepoListEffect, RepoListFeature.RepoListState, Nothing>(
    initialState = RepoListState(userName = apiService.getUserName()),
    actor = ActorImpl(apiService),
    bootstrapper = BootStrapperImpl(),
    reducer = ReducerImpl()
) {

    data class RepoListState(
        val isLoading: Boolean = false,
        val userName: String,
        val reposList: List<Repository> = listOf()
    )


    sealed class RepoListWish {
        object LoadRepoList : RepoListWish()
        object Cancel : RepoListWish()
    }

    sealed class RepoListEffect {
        data class ReposLoaded(val reposList: List<Repository>) : RepoListEffect()
        object StartLoad : RepoListEffect()
    }

    class BootStrapperImpl : Bootstrapper<RepoListWish> {
        override fun invoke(): Observable<RepoListWish> = Observable.just(RepoListWish.LoadRepoList)
    }

    class ActorImpl(val apiService: IApiService) : Actor<RepoListState, RepoListWish, RepoListEffect> {

        override fun invoke(state: RepoListState, wish: RepoListWish): Observable<RepoListEffect> = when (wish) {
            RepoListWish.LoadRepoList -> apiService.getStarredRepos(state.userName)
                .flatMapObservable { Observable.just(RepoListEffect.ReposLoaded(it) as RepoListEffect) }
                .startWith(just(RepoListEffect.StartLoad))
                .observeOn(AndroidSchedulers.mainThread())
            RepoListWish.Cancel -> TODO()
        }

    }

    class ReducerImpl : Reducer<RepoListState, RepoListEffect> {
        override fun invoke(state: RepoListState, effect: RepoListEffect): RepoListState = when (effect) {
            is RepoListEffect.ReposLoaded -> state.copy(isLoading = false, reposList = effect.reposList)
            RepoListEffect.StartLoad -> state.copy(isLoading = true, reposList = listOf())
        }

    }

}