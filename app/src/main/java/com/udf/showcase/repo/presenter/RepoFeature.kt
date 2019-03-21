package com.udf.showcase.repo.presenter

import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.feature.ActorReducerFeature
import com.udf.showcase.data.IApiService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.RepositoryId
import javax.inject.Inject
import javax.inject.Named


class RepoFeature @Inject constructor(
    @Named("repo_id") private val repoId: String,
    private val apiService: IApiService
) : ActorReducerFeature<RepoFeature.RepoWish, RepoFeature.RepoEffect, RepoFeature.RepoState, Nothing>(
    initialState = RepoState(openRepoId = repoId),
    actor = ActorImpl(apiService),
    bootstrapper = BootStrapperImpl(repoId),
    reducer = ReducerImpl()
) {

    data class RepoState(
        val openRepoId: String,
        val repository: Repository? = null,
        val isLoading: Boolean = false
    )

    sealed class RepoEffect {
        object StartLoading : RepoEffect()
        data class RepoLoaded(val repository: Repository) : RepoEffect()
    }

    sealed class RepoWish {
        data class LoadRepo(val id: String) : RepoWish()
    }

    class BootStrapperImpl(val repoId: String) : Bootstrapper<RepoWish> {
        override fun invoke(): Observable<RepoWish> = Observable.just(RepoWish.LoadRepo(repoId))
    }

    class ActorImpl(val apiService: IApiService) : Actor<RepoState, RepoWish, RepoEffect> {

        override fun invoke(state: RepoState, wish: RepoWish): Observable<RepoEffect> = when (wish) {
            is RepoWish.LoadRepo -> apiService.getRepo(RepositoryId.createFromUrl(wish.id))
                .flatMapObservable { Observable.just(RepoEffect.RepoLoaded(it) as RepoEffect) }
                .startWith(Observable.just(RepoEffect.StartLoading))
                .observeOn(AndroidSchedulers.mainThread())
        }

    }

    class ReducerImpl : Reducer<RepoState, RepoEffect> {
        override fun invoke(state: RepoState, effect: RepoEffect): RepoState = when (effect) {
            RepoEffect.StartLoading -> state.copy(isLoading = true)
            is RepoEffect.RepoLoaded -> state.copy(isLoading = false, repository = effect.repository)
        }
    }

}