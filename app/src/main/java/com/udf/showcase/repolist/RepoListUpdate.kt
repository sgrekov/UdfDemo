package com.udf.showcase.repolist

import com.factorymarket.rxelm.cmd.CancelByClassCmd
import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.cmd.None
import com.factorymarket.rxelm.msg.Init
import com.factorymarket.rxelm.msg.Msg

fun repoListUpdate(msg: Msg, state: RepoListState): Pair<RepoListState, Cmd> = when (msg) {
    is Init -> state.copy(isLoading = true) to LoadReposCmd(state.userName)
    is ReposLoadedMsg -> state.copy(isLoading = false, reposList = msg.reposList) to None
    is CancelMsg -> state.copy(isLoading = false) to CancelByClassCmd(cmdClass = LoadReposCmd::class)
    is RefreshMsg -> state.copy(isLoading = true, reposList = listOf()) to LoadReposCmd(
        state.userName
    )
    else -> state to None
}