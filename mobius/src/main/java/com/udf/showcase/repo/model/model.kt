package com.udf.showcase.repo.model

import org.eclipse.egit.github.core.Repository

data class RepoModel(
        val openRepoId: String,
        val repository: Repository? = null,
        val isLoading: Boolean = false)

sealed class RepoEvent

data class RepoLoaded(val repo: Repository) : RepoEvent()

sealed class RepoEffect
data class LoadRepo(val id: String) : RepoEffect()