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
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.main.di.MainModule
import com.udf.showcase.main.presenter.MainFeature
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class MainFragment : BaseFragment(), IMainView {

    @Inject lateinit var feature: MainFeature
    @BindView(R.id.repos_list) lateinit var reposList: RecyclerView
    @BindView(R.id.repos_progress) lateinit var progressBar: ProgressBar
    @BindView(R.id.error_text) lateinit var errorText: TextView
    @BindView(R.id.refresh) lateinit var refreshBtn: Button
    @BindView(R.id.cancel) lateinit var cancelBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent()
            .plusMainComponent(MainModule(this))
            .inject(this)
    }


    override fun getLayoutRes(): Int = R.layout.repos_list_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reposList.layoutManager = LinearLayoutManager(activity)

        refreshBtn.setOnClickListener {
            feature.refresh()
        }

        cancelBtn.setOnClickListener {
            feature.cancel()
        }

        feature.init(null)
        feature.render()
    }

    override fun onDestroy() {
        super.onDestroy()
        feature.destroy()
    }

    override fun setTitle(title: String) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun setErrorText(errorText: String) {
        this.errorText.text = errorText
    }

    override fun showErrorText(show : Boolean) {
        errorText.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setRepos(reposList: List<Repository>) {
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
                feature.onRepoItemClick(repos[position])
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