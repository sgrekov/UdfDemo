package com.udf.showcase.repolist

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.core.TypedRenderableAdapter
import com.udf.showcase.column
import com.udf.showcase.marginTop
import com.udf.showcase.toolbarWidget
import org.eclipse.egit.github.core.Repository
import timber.log.Timber
import trikita.anvil.BaseDSL
import trikita.anvil.DSL.*
import trikita.anvil.RenderableAdapter
import trikita.anvil.material.MaterialDSL
import trikita.anvil.recyclerview.v7.RecyclerViewv7DSL

class RepoListRenderable(context: Context) : BaseRenderable<RepoListState>(context) {

    lateinit var repoListClickListener: RepoListClickListener
    private var reposAdapter = createAdapter(listOf())

    override fun view() {
        column {
            size(MATCH, MATCH)
            toolbarWidget("Your starred repositories")
            frameLayout {
                size(MATCH, MATCH)
                padding(dip(10))
                textView {
                    size(WRAP, WRAP)
                    layoutGravity(CENTER)
                    visibility(model.reposList.isEmpty() && !model.isLoading)
                    BaseDSL.text("User has no starred repos")
                }
                progressBar {
                    size(WRAP, WRAP)
                    layoutGravity(CENTER)
                    visibility(model.isLoading)
                }

                MaterialDSL.materialButton {
                    size(WRAP, dip(50))
                    BaseDSL.text("Refresh")
                    layoutGravity(START or TOP)
                    onClick {
                        repoListClickListener.refresh()
                    }
                }
                MaterialDSL.materialButton {
                    size(WRAP, dip(50))
                    BaseDSL.text("Cancel")
                    layoutGravity(END or TOP)
                    onClick { repoListClickListener.cancel() }
                }
                RecyclerViewv7DSL.recyclerView {
                    size(MATCH, MATCH)
                    marginTop(dip(50))
                    padding(dip(4))
                    visibility(model.reposList.isNotEmpty())
                    RecyclerViewv7DSL.linearLayoutManager(LinearLayoutManager.VERTICAL)
                    init {
                        RecyclerViewv7DSL.adapter(reposAdapter)
                    }
                }
            }
        }
    }

    override fun updateAdapter() {
        if (reposAdapter.itemCount != model.reposList.size) {
            reposAdapter.items = model.reposList
            reposAdapter.notifyDataSetChanged()
        }
    }

    private fun createAdapter(reposList: List<Repository>): TypedRenderableAdapter<Repository> {
        return TypedRenderableAdapter.withItems(reposList,
            RenderableAdapter.Item { _, item ->
                when (item) {
                    is Repository -> repoListItem(item) { repo ->
                        repoListClickListener.onRepoItemClick(repo)
                    }
                }
            })
    }

    private fun repoListItem(item: Repository, r: (Repository) -> Unit) {
        Timber.tag("updateAdapter").d("repoListItem name:${item.name}")
        frameLayout {
            size(MATCH, dip(50))
            textView {
                size(WRAP, WRAP)
                layoutGravity(START or BaseDSL.CENTER_VERTICAL)
                text(item.name)
            }
            textView {
                size(WRAP, WRAP)
                layoutGravity(END or BaseDSL.CENTER_VERTICAL)
                text("watchers:" + item.watchers)
            }
            onClick {
                r(item)
            }
        }
    }

}

interface RepoListClickListener {
    fun refresh()
    fun cancel()
    fun onRepoItemClick(repo: Repository)

}