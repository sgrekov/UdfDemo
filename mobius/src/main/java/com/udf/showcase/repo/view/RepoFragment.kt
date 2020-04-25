package com.udf.showcase.repo.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import com.spotify.mobius.*
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.rx2.RxConnectables
import com.spotify.mobius.rx2.RxMobius
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.data.GitHubService
import com.udf.showcase.data.IApiService
import com.udf.showcase.repo.model.*
import com.udf.showcase.repo.presenter.RepoUpdate
import com.udf.showcase.show
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.RepositoryId
import javax.inject.Inject

class RepoFragment : BaseFragment(), Connectable<RepoModel, RepoEvent> {

    companion object {
        const val REPO_ID_KEY = "repo_id_key"
    }

    @BindView(R.id.tvRepoName) lateinit var tvRepoName: TextView
    @BindView(R.id.tvRepoDescr) lateinit var tvRepoDescr: TextView
    @BindView(R.id.tvOwner) lateinit var tvRepoOwner: TextView
    @BindView(R.id.pbLoading) lateinit var pbLoading: ProgressBar

    @Inject lateinit var api: IApiService

    var rxEffectHandler = RxMobius.subtypeEffectHandler<RepoEffect, RepoEvent>()
        .addTransformer(LoadRepo::class.java, this::handleRepoLoadEffect)
        .build()

    lateinit var loopFactory: MobiusLoop.Factory<RepoModel, RepoEvent, RepoEffect>
    lateinit var controller: MobiusLoop.Controller<RepoModel, RepoEvent>

    lateinit var initialModel: RepoModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent()
            .inject(this)

        val openRepoId = arguments?.getString(REPO_ID_KEY) ?: ""

        initialModel = RepoModel(isLoading = true, openRepoId = openRepoId)

        loopFactory = RxMobius.loop(RepoUpdate(), rxEffectHandler)
            .init {
                First.first(initialModel, setOf(LoadRepo(openRepoId)))
            }
            .logger(AndroidLogger.tag<RepoModel, RepoEvent, RepoEffect>("my_app"))

        controller = MobiusAndroid.controller(loopFactory, initialModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller.connect(this)
    }

    override fun connect(output: Consumer<RepoEvent>): Connection<RepoModel> {
        return object : Connection<RepoModel> {

            override fun accept(model: RepoModel) {
                render(model)
            }

            override fun dispose() {}
        }
    }

    fun render(model: RepoModel) {
        showLoading(model.isLoading)
        showRepo(model.repository)
    }

    override fun getLayoutRes(): Int = R.layout.repo_layout

    fun handleRepoLoadEffect(request: Observable<LoadRepo>): Observable<RepoEvent> {
        return request.flatMap { effect ->
            api.getRepo(RepositoryId.createFromUrl(effect.id))
                .map { RepoLoaded(it) }
                .toObservable()
        }
    }

    override fun onResume() {
        super.onResume()
        controller.start()
    }

    override fun onPause() {
        controller.stop()
        super.onPause()
    }

    override fun onDestroyView() {
        controller.disconnect()
        super.onDestroyView()
    }


    fun showLoading(loading: Boolean) {
        pbLoading.show(loading)
    }

    fun showRepo(repo: Repository?) {
        repo?.let {
            tvRepoName.show()
            tvRepoDescr.show()
            tvRepoOwner.show()

            tvRepoName.text = repo.name
            tvRepoDescr.text = repo.description
            tvRepoOwner.text = repo.owner.login
        } ?: run {
            tvRepoName.show(false)
            tvRepoDescr.show(false)
            tvRepoOwner.show(false)
        }
    }

}