package com.udf.showcase.repo.presenter


import com.udf.showcase.data.IApiService
import com.udf.showcase.repo.model.RepoState
import com.udf.showcase.repo.view.IRepoView
import io.reactivex.Single
import org.eclipse.egit.github.core.RepositoryId
import javax.inject.Inject
import javax.inject.Named

class RepoPresenter @Inject constructor(
        private val view: IRepoView,
        @Named("repo_id") private val repoId: String,
        private val apiService: IApiService
) {

//    override fun render(state: RepoState) {
//        view.showLoading(state.isLoading)
//        state.repository?.let(view::showRepo)
//    }

    fun init() {
//        program.run(initialState = initialState(), initialMsg = InitRepo)
    }

    fun initialState(): RepoState {
        return RepoState(openRepoId = repoId, isLoading = true)
    }

//    override fun call(cmd: Cmd): Single<Msg> {
//        return when (cmd) {
//            is LoadRepo -> apiService.getRepo(RepositoryId.createFromUrl(cmd.id)).map { RepoLoaded(it) }
//            else -> Single.just(Idle)
//        }
//    }
//
//    override fun update(msg: Msg, state: RepoState): Pair<RepoState, Cmd> {
//        return when (msg) {
//            is InitRepo -> state.copy(isLoading = true) to LoadRepo(state.openRepoId)
//            is RepoLoaded -> state.copy(isLoading = false, repository = msg.repo) to None
//            else -> state to None
//        }
//    }
//
//    fun destroy() {
//        program.stop()
//    }


}