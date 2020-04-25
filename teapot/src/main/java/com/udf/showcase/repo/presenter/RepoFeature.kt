package com.udf.showcase.repo.presenter

import dev.teapot.cmd.Cmd
import dev.teapot.cmd.None
import dev.teapot.contract.Renderable
import dev.teapot.msg.Idle
import dev.teapot.msg.Msg
import dev.teapot.program.Program
import dev.teapot.program.ProgramBuilder
import com.udf.showcase.data.IApiService
import com.udf.showcase.repo.model.InitRepo
import com.udf.showcase.repo.model.LoadRepo
import com.udf.showcase.repo.model.RepoLoaded
import com.udf.showcase.repo.model.RepoState
import com.udf.showcase.repo.view.IRepoView
import dev.teapot.contract.RxFeature
import dev.teapot.contract.Update
import io.reactivex.Single
import org.eclipse.egit.github.core.RepositoryId
import javax.inject.Inject
import javax.inject.Named

class RepoFeature @Inject constructor(
        private val view: IRepoView,
        @Named("repo_id") private val repoId: String,
        programBuilder: ProgramBuilder,
        private val apiService: IApiService
) : RxFeature<RepoState>, Renderable<RepoState> {

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

    override fun update(msg: Msg, state: RepoState): Update<RepoState> {
        return when (msg) {
            is InitRepo -> Update.update(state.copy(isLoading = true), LoadRepo(state.openRepoId))
            is RepoLoaded -> Update.state(state.copy(isLoading = false, repository = msg.repo))
            else -> Update.idle()
        }
    }

    fun destroy() {
        program.stop()
    }


}