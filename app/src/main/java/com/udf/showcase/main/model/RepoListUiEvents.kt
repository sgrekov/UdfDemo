package com.udf.showcase.main.model

import com.udf.showcase.main.presenter.RepoListFeature

sealed class RepoListUiEvents {
    object Refresh : RepoListUiEvents()
    object Cancel : RepoListUiEvents()
}

class RepoListUiEventsToWish : (RepoListUiEvents) -> RepoListFeature.RepoListWish? {

    override fun invoke(event: RepoListUiEvents): RepoListFeature.RepoListWish? {
        return when (event) {
            RepoListUiEvents.Refresh -> RepoListFeature.RepoListWish.LoadRepoList
            RepoListUiEvents.Cancel -> RepoListFeature.RepoListWish.Cancel
        }
    }
}