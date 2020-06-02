package com.udf.showcase.repolist

import android.content.Context
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.udf.showcase.box
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.core.TypedRenderableAdapter
import com.udf.showcase.column
import com.udf.showcase.textWrapped
import com.udf.showcase.toolbarWidget
import com.udf.showcase.R
import dev.inkremental.RenderableAdapter
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.Size.*
import dev.inkremental.dsl.android.widget.frameLayout
import dev.inkremental.dsl.android.widget.progressBar
import dev.inkremental.dsl.androidx.recyclerview.adapter
import dev.inkremental.dsl.androidx.recyclerview.linearLayoutManager
import dev.inkremental.dsl.androidx.recyclerview.widget.recyclerView
import dev.inkremental.dsl.google.android.material.button.materialButton
import org.eclipse.egit.github.core.Repository

class RepoListRenderable(context: Context) : BaseRenderable<RepoListState>(context) {

    lateinit var repoListClickListener: RepoListClickListener
    private var reposAdapter = createAdapter(listOf())

    var listId = ViewCompat.generateViewId()

    override var renderable =  {
        column {
            size(MATCH, MATCH)
            toolbarWidget("Your starred repositories")
            box {
                padding(10.dp)
                textWrapped(R.string.no_repos){
                    layoutGravity(CENTER)
                    visibility(model.reposList.isEmpty() && !model.isLoading)
                }
                progressBar {
                    size(WRAP, WRAP)
                    layoutGravity(CENTER)
                    visibility(model.isLoading)
                }

                materialButton {
                    size(WRAP, 50.sizeDp)
                    text("Refresh")
                    layoutGravity(START or TOP)
                    onClick {
                        repoListClickListener.refresh()
                    }
                }
                materialButton {
                    size(WRAP, 50.sizeDp)
                    text("Cancel")
                    layoutGravity(END or TOP)
                    onClick { repoListClickListener.cancel() }
                }
                recyclerView {
                    id(listId)
                    size(MATCH, MATCH)
                    margin(top = 50.dp)
                    padding(4.dp)
                    visibility(model.reposList.isNotEmpty())
                    linearLayoutManager(LinearLayoutManager.VERTICAL)
                    init {
                        adapter(reposAdapter)
                    }
                }
            }
        }
    }

    override fun updateAdapter() {
        if (reposAdapter.items !== model.reposList) {
            reposAdapter.items = model.reposList
            reposAdapter.notifyDataSetChanged()
        }
    }

    private fun createAdapter(reposList: List<Repository>): TypedRenderableAdapter<Repository> {
        return TypedRenderableAdapter.withItems(reposList,
            { _, item ->
                when (item) {
                    is Repository -> repoListItem(item) { repo ->
                        repoListClickListener.onRepoItemClick(repo)
                    }
                }
            })
    }

    private fun repoListItem(item: Repository, r: (Repository) -> Unit) {
        frameLayout {
            size(MATCH, 50.sizeDp)
            textWrapped(item.name){
                layoutGravity(START or CENTER_VERTICAL)
            }
            textWrapped("watchers:" + item.watchers) {
                layoutGravity(END or CENTER_VERTICAL)
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