package com.udf.showcase.repo

import android.content.Context
import com.factorymarket.rxelm.cmd.Cmd
import com.factorymarket.rxelm.contract.Update
import com.factorymarket.rxelm.msg.Msg
import com.udf.showcase.core.BaseFragment
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.data.IApiService
import com.udf.showcase.noEffect
import io.reactivex.Single
import org.eclipse.egit.github.core.RepositoryId
import javax.inject.Inject

class RepoFragment : BaseFragment<RepoState>() {

    companion object {
        const val REPO_ID_KEY = "repo_id_key"
    }

    @Inject lateinit var apiService: IApiService

    override fun createRenderable(context: Context): BaseRenderable<RepoState> =
        RepoRenderable(context)

    override fun setupDI() {
        getActivityComponent()
            .inject(this)
    }

    override fun initialState(): RepoState =
        RepoState(
            openRepoId = arguments?.getString(REPO_ID_KEY)!!,
            isLoading = true
        )

    override fun call(cmd: Cmd): Single<Msg> = when (cmd) {
        is LoadRepo -> apiService.getRepo(RepositoryId.createFromUrl(cmd.id)).map {
            RepoLoaded(
                it
            )
        }
        else -> noEffect()
    }

    override fun update(msg: Msg, state: RepoState): Update<RepoState> = repoUpdate(msg, state)

}