package com.udf.showcase.repo.presenter

import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.cmd.None
import com.factorymarket.rxelm.contract.Component
import com.factorymarket.rxelm.contract.Renderable
import com.factorymarket.rxelm.msg.Idle
import com.factorymarket.rxelm.msg.Msg
import com.factorymarket.rxelm.program.Program
import com.factorymarket.rxelm.program.ProgramBuilder
import com.udf.showcase.data.IApiService
import com.udf.showcase.repo.model.InitRepo
import com.udf.showcase.repo.model.LoadRepo
import com.udf.showcase.repo.model.RepoLoaded
import com.udf.showcase.repo.model.RepoState
import com.udf.showcase.repo.view.IRepoView
import io.reactivex.Single
import org.eclipse.egit.github.core.RepositoryId
import javax.inject.Inject
import javax.inject.Named

class RepoPresenter @Inject constructor(
        private val view: IRepoView,
        @Named("repo_id") private val repoId: String,
        programBuilder: ProgramBuilder,
        private val apiService: IApiService
) : Component<RepoState>, Renderable<RepoState> {

    override fun render(state: RepoState) {
        view.showLoading(state.isLoading)
        state.repository?.let(view::showRepo)
    }

    private val program: Program<RepoState> = programBuilder.build(this)

    fun init() {
        program.run(initialState = initialState(), initialMsg = InitRepo)
    }

    fun initialState(): RepoState {
        return RepoState(openRepoId = repoId, isLoading = true)
    }

    override fun call(cmd: Cmd): Single<Msg> {
        return when (cmd) {
            is LoadRepo -> apiService.getRepo(RepositoryId.createFromUrl(cmd.id)).map { RepoLoaded(it) }
            else -> Single.just(Idle)
        }
    }

    override fun update(msg: Msg, state: RepoState): Pair<RepoState, Cmd> {
        return when (msg) {
            is InitRepo -> state.copy(isLoading = true) to LoadRepo(state.openRepoId)
            is RepoLoaded -> state.copy(isLoading = false, repository = msg.repo) to None
            else -> state to None
        }
    }

    fun destroy() {
        program.stop()
    }


}