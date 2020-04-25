package com.udf.showcase.repo

import dev.teapot.contract.Update
import dev.teapot.msg.Init
import dev.teapot.msg.Msg

fun repoUpdate(msg: Msg, state: RepoState): Update<RepoState> = when (msg) {
    is Init -> Update.update(state.copy(isLoading = true), LoadRepo(
        state.openRepoId
    ))
    is RepoLoaded -> Update.state(state.copy(isLoading = false, repository = msg.repo))
    else -> Update.idle()
}