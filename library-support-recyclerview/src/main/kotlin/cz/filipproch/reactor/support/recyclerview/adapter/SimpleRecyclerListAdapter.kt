package cz.filipproch.reactor.extras.ui.recyclerview.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * TODO: add description
 */
open class SimpleRecyclerListAdapter<T> : ReactorRecyclerListAdapter<T, SimpleRecyclerListAdapter.Holder> {

    private var itemLayoutResId: Int
    private var binder: Binder<T>

    constructor(@LayoutRes itemLayoutResId: Int, binder: Binder<T>) : super() {
        this.itemLayoutResId = itemLayoutResId
        this.binder = binder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context)
                .inflate(itemLayoutResId, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        binder.bindItem(position, holder.itemView, getItem(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view)

    abstract class Binder<in T> {
        open fun bindItem(position: Int, view: View, item: T) {
            this.bindItem(view, item)
        }
        open fun bindItem(view: View, item: T) {}
    }

}