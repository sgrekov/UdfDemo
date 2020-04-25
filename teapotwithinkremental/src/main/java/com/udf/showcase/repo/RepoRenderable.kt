package com.udf.showcase.repo

import android.content.Context
import com.udf.showcase.FontStyle.header
import com.udf.showcase.FontStyle.small
import com.udf.showcase.column
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.textWrapped
import com.udf.showcase.toolbarWidget
import dev.inkremental.dsl.android.*
import dev.inkremental.dsl.android.Size.WRAP
import dev.inkremental.dsl.android.widget.progressBar

class RepoRenderable(c: Context) : BaseRenderable<RepoState>(c) {

    override var renderable = {
        column {

            toolbarWidget(if (model.isLoading) "Loading.." else model.repository?.name ?: "")

            progressBar {
                size(WRAP, WRAP)
                layoutGravity(CENTER)
                visibility(model.isLoading)
            }

            column {
                margin(8.dp)
                model.repository?.let { repo ->

                    textWrapped("Owner : ${repo.owner.login}") {
                        header()
                    }
                    textWrapped(" ${repo.watchers} watchers") {
                        margin(5.dp)
                        small()
                    }
                    textWrapped(repo.description) {
                        margin(top = 5.dp)
                        small()
                    }
                }

            }
        }
    }
}