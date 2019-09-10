package com.udf.showcase.repolist.view

import android.content.Context
import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.msg.Msg
import com.udf.showcase.BaseFragment2
import com.udf.showcase.BaseRenderable
import com.udf.showcase.data.IApiService
import com.udf.showcase.repolist.CancelMsg
import com.udf.showcase.repolist.LoadReposCmd
import com.udf.showcase.repolist.RepoListState
import com.udf.showcase.repolist.RefreshMsg
import com.udf.showcase.repolist.ReposLoadedMsg
import com.udf.showcase.repolist.repoListUpdate
import com.udf.showcase.navigation.Navigator
import com.udf.showcase.noEffect
import io.reactivex.Single
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class RepoListFragment : BaseFragment2<RepoListState>(), RepoListClickListener {

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

    override fun update(msg: Msg, state: RepoListState): Pair<RepoListState, Cmd> =
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