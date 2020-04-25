package com.udf.showcase.main.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import com.badoo.mvicore.android.AndroidBinderLifecycle
import com.badoo.mvicore.binder.Binder
import com.badoo.mvicore.binder.using
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.main.model.RepoListUiEvents
import com.udf.showcase.main.model.RepoListUiEventsToWish
import com.udf.showcase.main.presenter.RepoListFeature
import com.udf.showcase.navigation.Navigator
import io.reactivex.functions.Consumer
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class MainFragment : BaseFragment<RepoListUiEvents>(), Consumer<RepoListFeature.RepoListState> {


    @BindView(R.id.repos_list) lateinit var reposList: RecyclerView
    @BindView(R.id.repos_progress) lateinit var progressBar: ProgressBar
    @BindView(R.id.error_text) lateinit var errorText: TextView
    @BindView(R.id.refresh) lateinit var refreshBtn: Button
    @BindView(R.id.cancel) lateinit var cancelBtn: Button

    @Inject lateinit var repoListFeature: RepoListFeature
    @Inject lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent()
            .inject(this)
    }


    override fun getLayoutRes(): Int = R.layout.repos_list_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reposList.layoutManager = LinearLayoutManager(activity)

        refreshBtn.setOnClickListener {
            onNext(RepoListUiEvents.Refresh)
        }

        cancelBtn.setOnClickListener {
            onNext(RepoListUiEvents.Cancel)
        }

        val binder = Binder(AndroidBinderLifecycle(lifecycle))
        binder.bind(this to repoListFeature using RepoListUiEventsToWish())
        binder.bind(repoListFeature to this)

    }

    override fun accept(state: RepoListFeature.RepoListState?) {
        state?.apply {
            setTitle(state.userName + "'s starred repos")

            if (isLoading) {
                showErrorText(false)
                if (reposList.isEmpty()) {
                    showProgress()
                }
            } else {
                hideProgress()
                if (reposList.isEmpty()) {
                    setErrorText("User has no starred repos")
                    showErrorText(true)
                }
            }
            setRepos(reposList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repoListFeature.dispose()
    }

    fun setTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    fun setErrorText(errorText: String) {
        this.errorText.text = errorText
    }

    fun showErrorText(show: Boolean) {
        errorText.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setRepos(reposList: List<Repository>) {
        this.reposList.adapter = ReposAdapter(reposList, layoutInflater)
    }

    private inner class ReposAdapter(private val repos: List<Repository>, private val inflater: LayoutInflater) :
        RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
            return RepoViewHolder(inflater.inflate(R.layout.repos_list_item_layout, parent, false))
        }

        override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
            holder.bind(repos[position])
            holder.itemView.setOnClickListener {
                navigator.goToRepo(repos[position])
            }
        }

        override fun getItemCount(): Int {
            return repos.size
        }

        internal inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var repoName: TextView = itemView.findViewById(R.id.repo_name) as TextView
            var repoStarsCount: TextView = itemView.findViewById(R.id.repo_stars_count) as TextView

            fun bind(repository: Repository) {
                repoName.text = repository.name
                repoStarsCount.text = "watchers:" + repository.watchers
            }
        }
    }


}