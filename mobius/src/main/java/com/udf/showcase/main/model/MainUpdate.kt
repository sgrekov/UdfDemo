package com.udf.showcase.main.model

import com.spotify.mobius.Effects.effects
import com.spotify.mobius.Next
import com.spotify.mobius.Update

class MainUpdate : Update<MainModel, MainEvent, MainEffect> {
    override fun update(model: MainModel, event: MainEvent): Next<MainModel, MainEffect> {
        return when (event) {
            is ReposLoadedEvent -> Next.next(model.copy(isLoading = false, reposList = event.reposList))
            CancelEvent -> Next.next(
                model.copy(isLoading = false, reposList = listOf()),
                effects(LoadReposEffect(model.userName, cancel = true))
            )
            RefreshEvent -> Next.next(
                model.copy(isLoading = true, reposList = listOf()),
                effects(LoadReposEffect(model.userName))
            )
        }
    }
}