package com.udf.showcase.core

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import dev.inkremental.RenderableAdapter
import dev.inkremental.dsl.androidx.recyclerview.RenderableRecyclerViewAdapter


abstract class TypedRenderableAdapter<T> : RenderableRecyclerViewAdapter() {
    lateinit var items: List<T>

    companion object {

        fun <T> withItems(
            items: List<T>,
            r: (Int, T) -> Unit,
            horizontal: Boolean = false
        ): TypedRenderableAdapter<T> {
            val adapter = object : TypedRenderableAdapter<T>() {
                init {
                    this.setHasStableIds(false)
                }

                override fun getItemCount(): Int {
                    return this.items.size
                }

                override fun getItemViewType(pos: Int): Int {
                    val item: Any? = this.items[pos]
                    return if (item == null) 0 else item::class.java.hashCode()
                }

                override fun view(holder: RecyclerView.ViewHolder?) {
                    val i = holder?.layoutPosition!!
                    r(i, this.items[i])
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MountHolder {
                    return if (horizontal) {
                        val root = FrameLayout(parent.context)
                        root.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        return MountHolder(root)
                    } else {
                        return super.onCreateViewHolder(parent, viewType)
                    }
                }

//                override fun view(holder: RecyclerView.ViewHolder?) {
//                    TODO("Not yet implemented")
//                }

            }
            adapter.items = items
            return adapter
        }
    }
}