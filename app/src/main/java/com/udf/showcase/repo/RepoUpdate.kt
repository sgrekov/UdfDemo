package com.udf.showcase.repo

import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.cmd.None
import com.factorymarket.rxelm.msg.Init
import com.factorymarket.rxelm.msg.Msg

fun repoUpdate(msg: Msg, state: RepoState): Pair<RepoState, Cmd> = when (msg) {
    is Init -> state.copy(isLoading = true) to LoadRepo(
        state.openRepoId
    )
    is RepoLoaded -> state.copy(isLoading = false, repository = msg.repo) to None
    else -> state to None
}