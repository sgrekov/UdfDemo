package com.udf.showcase.repo.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import com.badoo.mvicore.android.AndroidBinderLifecycle
import com.badoo.mvicore.binder.Binder
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.repo.di.RepoModule
import com.udf.showcase.repo.presenter.RepoFeature
import com.udf.showcase.show
import io.reactivex.functions.Consumer
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class RepoFragment : BaseFragment<Unit>(), Consumer<RepoFeature.RepoState> {

    companion object {
        const val REPO_ID_KEY = "repo_id_key"
    }

    @Inject lateinit var feature: RepoFeature

    @BindView(R.id.tvRepoName) lateinit var tvRepoName: TextView
    @BindView(R.id.tvRepoDescr) lateinit var tvRepoDescr: TextView
    @BindView(R.id.tvOwner) lateinit var tvRepoOwner: TextView
    @BindView(R.id.pbLoading) lateinit var pbLoading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent()
            .plusRepoComponent(RepoModule(arguments?.getString(REPO_ID_KEY) ?: ""))
            .inject(this)
    }

    override fun getLayoutRes(): Int = R.layout.repo_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binder = Binder(AndroidBinderLifecycle(lifecycle))
        binder.bind(feature to this)
    }

    override fun accept(state: RepoFeature.RepoState) {
        showLoading(state.isLoading)
        showRepo(state.repository)
    }

    fun showLoading(loading: Boolean) {
        pbLoading.show(loading)
    }

    fun showRepo(repo: Repository?) {
        val exists = repo != null

        tvRepoName.show(exists)
        tvRepoDescr.show(exists)
        tvRepoOwner.show(exists)

        repo?.let {
            tvRepoName.text = repo.name
            tvRepoDescr.text = repo.description
            tvRepoOwner.text = repo.owner.login
        }
    }

}