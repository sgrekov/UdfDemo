package com.udf.showcase.main.model

import org.eclipse.egit.github.core.Repository

data class MainModel(
    val isLoading: Boolean = true,
    val userName: String,
    val reposList: List<Repository> = listOf()
)

sealed class MainEffect
data class LoadReposEffect(val userName: String, val cancel : Boolean = false) : MainEffect()

sealed class MainEvent
data class ReposLoadedEvent(val reposList: List<Repository>) : MainEvent()
object CancelEvent : MainEvent()
object RefreshEvent : MainEvent()