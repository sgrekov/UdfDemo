package com.udf.showcase.repolist

import android.content.Context
import dev.teapot.cmd.Cmd
import dev.teapot.contract.Update
import dev.teapot.msg.Msg
import com.udf.showcase.core.BaseFragment
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.data.IApiService
import com.udf.showcase.navigation.Navigator
import com.udf.showcase.noEffect
import io.reactivex.Single
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class RepoListFragment : BaseFragment<RepoListState>(),
    RepoListClickListener {

    @Inject lateinit var service: IApiService
    @Inject lateinit var navigator: Navigator

    override fun createRenderable(context: Context): BaseRenderable<RepoListState> {
        val renderable = RepoListRenderable(context)
        renderable.repoListClickListener = this
        return renderable
    }

    override fun setupDI() {
        getActivityComponent()
            .inject(this)
    }

    override fun initialState(): RepoListState =
        RepoListState(userName = service.getUserName())

    override fun call(cmd: Cmd): Single<Msg> = when (cmd) {
        is LoadReposCmd -> service.getStarredRepos(cmd.userName)
        .map { repos -> ReposLoadedMsg(repos) }
        else -> noEffect()
    }

    override fun update(msg: Msg, state: RepoListState): Update<RepoListState> =
        repoListUpdate(msg, state)

    override fun refresh() {
        program.accept(RefreshMsg)
    }

    override fun cancel() {
        program.accept(CancelMsg)
    }

    override fun onRepoItemClick(repo: Repository) {
        navigator.goToRepo(repo)
    }


}