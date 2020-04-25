package com.udf.showcase.repo.presenter

import com.spotify.mobius.Effects.effects
import com.spotify.mobius.Next
import com.spotify.mobius.Update
import com.udf.showcase.repo.model.*

class RepoUpdate : Update<RepoModel, RepoEvent, RepoEffect> {

    override fun update(model: RepoModel, event: RepoEvent): Next<RepoModel, RepoEffect> {
        return when (event) {
            is RepoLoaded -> Next.next(model.copy(isLoading = false, repository = event.repo))
        }
    }

}