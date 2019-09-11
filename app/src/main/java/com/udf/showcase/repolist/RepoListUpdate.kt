package com.udf.showcase.repolist

import com.factorymarket.rxelm.cmd.CancelByClassCmd
import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.cmd.None
import com.factorymarket.rxelm.contract.Update
import com.factorymarket.rxelm.msg.Init
import com.factorymarket.rxelm.msg.Msg
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