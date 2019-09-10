package com.udf.showcase.repo

import android.content.Context
import com.udf.showcase.core.BaseRenderable
import com.udf.showcase.FontStyle
import com.udf.showcase.column
import com.udf.showcase.marginTop
import com.udf.showcase.toolbarWidget
import com.udf.showcase.verticalLayout
import trikita.anvil.BaseDSL
import trikita.anvil.DSL.*

class RepoRenderable(c: Context) : BaseRenderable<RepoState>(c) {

    override fun view() {
        column {
            size(MATCH, MATCH)

            toolbarWidget(if (model.isLoading) "Loading.." else model.repository?.name ?: "")

            progressBar {
                size(WRAP, WRAP)
                BaseDSL.layoutGravity(BaseDSL.CENTER)
                visibility(model.isLoading)
            }

            verticalLayout {
                margin(dip(8))
                model.repository?.let { repo ->
                    textView {
                        FontStyle.header()
                        text("Owner : ${repo.owner.login}")
                    }
                    textView {
                        marginTop(dip(5))
                        FontStyle.small()
                        text(" ${repo.watchers} watchers")
                    }
                    textView {
                        marginTop(dip(5))
                        FontStyle.small()
                        text(repo.description)
                    }
                }

            }
        }
    }
}