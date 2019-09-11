package com.udf.showcase.repo

import com.factorymarket.rxelm.contract.Update
import com.factorymarket.rxelm.msg.Init
import com.factorymarket.rxelm.msg.Msg

fun repoUpdate(msg: Msg, state: RepoState): Update<RepoState> = when (msg) {
    is Init -> Update.update(state.copy(isLoading = true), LoadRepo(
        state.openRepoId
    ))
    is RepoLoaded -> Update.state(state.copy(isLoading = false, repository = msg.repo))
    else -> Update.idle()
}