package com.udf.showcase.main.presenter

import dev.teapot.cmd.CancelByClassCmd
import dev.teapot.cmd.Cmd
import dev.teapot.cmd.None
import dev.teapot.msg.Idle
import dev.teapot.msg.Init
import dev.teapot.msg.Msg
import dev.teapot.program.Program
import dev.teapot.program.ProgramBuilder
import com.udf.showcase.data.IApiService
import com.udf.showcase.main.model.CancelMsg
import com.udf.showcase.main.model.LoadReposCmd
import com.udf.showcase.main.model.MainState
import com.udf.showcase.main.model.RefreshMsg
import com.udf.showcase.main.model.ReposLoadedMsg
import com.udf.showcase.main.view.IMainView
import com.udf.showcase.navigation.Navigator
import dev.teapot.contract.Renderable
import dev.teapot.contract.RxFeature
import dev.teapot.contract.Update
import io.reactivex.Single
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class MainFeature @Inject constructor(
    val view: IMainView,
    programBuilder: ProgramBuilder,
    private val service: IApiService,
    private val navigator: Navigator
) : RxFeature<MainState>, Renderable<MainState> {

    private val program: Program<MainState> = programBuilder.build(this)

    fun init(initialState: MainState?) {
        program.run(initialState ?: MainState(userName = service.getUserName()))
    }

    override fun update(msg: Msg, state: MainState): Update<MainState> {
        return when (msg) {
            is Init -> Update.update(state.copy(isLoading = true), LoadReposCmd(state.userName))
            is ReposLoadedMsg -> Update.state(
                state.copy(
                    isLoading = false,
                    reposList = msg.reposList
                )
            )
            is CancelMsg -> Update.update(
                state.copy(isLoading = false),
                CancelByClassCmd(cmdClass = LoadReposCmd::class)
            )
            is RefreshMsg -> Update.update(
                state.copy(isLoading = true, reposList = listOf()),
                LoadReposCmd(state.userName)
            )
            else -> Update.idle()
        }
    }

    fun render() {
        program.render()
    }

    override fun render(state: MainState) {
        state.apply {
            view.setTitle(state.userName + "'s starred repos")

            if (isLoading) {
                view.showErrorText(false)
                if (reposList.isEmpty()) {
                    view.showProgress()
                }
            } else {
                view.hideProgress()
                if (reposList.isEmpty()) {
                    view.setErrorText("User has no starred repos")
                    view.showErrorText(true)
                }
            }
            view.setRepos(reposList)
        }
    }

    override fun call(cmd: Cmd): Single<Msg> {
        return when (cmd) {
            is LoadReposCmd -> service.getStarredRepos(cmd.userName)
                    .map { repos -> ReposLoadedMsg(repos) }
            else -> Single.just(Idle)
        }
    }

    fun destroy() {
        program.stop()
    }

    fun refresh() {
        program.accept(RefreshMsg)
    }

    fun cancel() {
        program.accept(CancelMsg)
    }

    fun onRepoItemClick(repository: Repository) {
        navigator.goToRepo(repository)
    }

}
