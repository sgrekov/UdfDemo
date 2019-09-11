package com.udf.showcase.repo

import android.content.Context
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.FontStyle
import com.udf.showcase.column
import com.udf.showcase.marginTop
import com.udf.showcase.textWrapped
import com.udf.showcase.toolbarWidget
import trikita.anvil.BaseDSL
import trikita.anvil.DSL.*

class RepoRenderable(c: Context) : BaseRenderable<RepoState>(c) {

    override fun view() {
        column {

            toolbarWidget(if (model.isLoading) "Loading.." else model.repository?.name ?: "")

            progressBar {
                size(WRAP, WRAP)
                layoutGravity(BaseDSL.CENTER)
                visibility(model.isLoading)
            }

            column {
                margin(dip(8))
                model.repository?.let { repo ->

                    textWrapped("Owner : ${repo.owner.login}") {
                        FontStyle.header()
                    }
                    textWrapped(" ${repo.watchers} watchers") {
                        marginTop(dip(5))
                        FontStyle.small()
                    }
                    textWrapped(repo.description) {
                        marginTop(dip(5))
                        FontStyle.small()
                    }
                }

            }
        }
    }
}