package com.udf.showcase.repolist

import dev.teapot.cmd.CancelByClassCmd
import dev.teapot.cmd.Cmd
import dev.teapot.cmd.None
import dev.teapot.contract.Update
import dev.teapot.msg.Init
import dev.teapot.msg.Msg
import com.udf.showcase.core.ResumeMsg

fun repoListUpdate(msg: Msg, state: RepoListState): Update<RepoListState> = when (msg) {
    is Init -> Update.update(state.copy(isLoading = true), LoadReposCmd(state.userName))
    is ReposLoadedMsg -> Update.state(state.copy(isLoading = false, reposList = msg.reposList))
    is CancelMsg -> Update.update(state.copy(isLoading = false), CancelByClassCmd(cmdClass = LoadReposCmd::class))
    is RefreshMsg -> Update.update(state.copy(isLoading = true, reposList = listOf()), LoadReposCmd(
        state.userName
    ))
    is ResumeMsg -> Update.state(state.copy())
    else -> Update.idle()
}