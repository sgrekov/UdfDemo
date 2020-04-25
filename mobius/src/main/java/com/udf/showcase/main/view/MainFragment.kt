package com.udf.showcase.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.jakewharton.rxbinding3.view.clicks
import com.spotify.mobius.First.first
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.android.MobiusAndroid
import com.spotify.mobius.rx2.RxConnectables
import com.spotify.mobius.rx2.RxMobius
import com.udf.showcase.BaseFragment
import com.udf.showcase.R
import com.udf.showcase.data.IApiService
import com.udf.showcase.main.model.*
import com.udf.showcase.navigation.Navigator
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.eclipse.egit.github.core.Repository
import javax.inject.Inject

class MainFragment : BaseFragment() {

    @BindView(R.id.repos_list) lateinit var reposList: RecyclerView
    @BindView(R.id.repos_progress) lateinit var progressBar: ProgressBar
    @BindView(R.id.error_text) lateinit var errorText: TextView
    @BindView(R.id.refresh) lateinit var refreshBtn: Button
    @BindView(R.id.cancel) lateinit var cancelBtn: Button

    @Inject lateinit var api: IApiService
    @Inject lateinit var navigator: Navigator

    var rxEffectHandler = RxMobius.subtypeEffectHandler<MainEffect, MainEvent>()
        .addTransformer(LoadReposEffect::class.java, this::handleLoadRepos)
        .build()

    lateinit var loopFactory: MobiusLoop.Factory<MainModel, MainEvent, MainEffect>
    lateinit var controller: MobiusLoop.Controller<MainModel, MainEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent()
            .inject(this)

        loopFactory = RxMobius
            .loop(MainUpdate(), rxEffectHandler)
            .init {
                if (it.reposList.isNotEmpty()) {
                    first(it)
                } else {
                    first(MainModel(userName = api.getUserName()), setOf(LoadReposEffect(api.getUserName())))
                }
            }
            .logger(AndroidLogger.tag<MainModel, MainEvent, MainEffect>("my_app"))

        controller = MobiusAndroid.controller(
            loopFactory,
            MainModel(userName = api.getUserName())
        )

    }

    override fun getLayoutRes(): Int = R.layout.repos_list_layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reposList.layoutManager = LinearLayoutManager(activity)

        controller.connect(RxConnectables.fromTransformer(this::connectViews))
    }

    fun connectViews(models: Observable<MainModel>): Observable<MainEvent> {
        val disposable = models
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { render(it) }

        val refreshBtnClick = refreshBtn.clicks()
            .map { RefreshEvent as MainEvent }

        val cancelBtnClick = cancelBtn.clicks()
            .map { CancelEvent as MainEvent }

        return Observable
            .merge(listOf(refreshBtnClick, cancelBtnClick))
            .doOnDispose(disposable::dispose)
    }

    fun render(state: MainModel) {
        state.apply {
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

    fun handleLoadRepos(request: Observable<LoadReposEffect>): Observable<MainEvent> {
        return request.flatMap { effect ->
            if (effect.cancel) {
                Single.never<MainEvent>()
            } else {
                api.getStarredRepos(effect.userName).map { repos ->
                    ReposLoadedEvent(
                        repos
                    )
                }
            }.toObservable()
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
        super.onDestroyView()
        controller.disconnect()
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