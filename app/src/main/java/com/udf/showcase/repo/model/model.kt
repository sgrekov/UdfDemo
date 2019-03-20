package com.udf.showcase.repo.model

import org.eclipse.egit.github.core.Repository

data class RepoState(
        val openRepoId: String,
        val repository: Repository? = null,
        val isLoading: Boolean = false)

//sealed class RepoMsg : Msg()
//
//object InitRepo : RepoMsg()
//data class RepoLoaded(val repo: Repository) : RepoMsg()
//
//sealed class RepoCmd : Cmd()
//data class LoadRepo(val id: String) : RepoCmd()