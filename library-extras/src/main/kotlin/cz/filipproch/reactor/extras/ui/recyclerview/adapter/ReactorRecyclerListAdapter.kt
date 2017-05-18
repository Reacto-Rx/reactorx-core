package cz.filipproch.reactor.extras.ui.recyclerview.adapter

import android.support.v7.widget.RecyclerView

/**
 * TODO: add description
 */
abstract class ReactorRecyclerListAdapter<T, VH : RecyclerView.ViewHolder> : ReactorRecyclerDataAdapter<T, VH>() {

    protected val list: MutableList<T> = mutableListOf()

    fun updateData(newList: List<T>?) {
        synchronized(list) {
            list.clear()
            if (newList != null) {
                list.addAll(newList)
            }
            notifyDataSetChanged()
        }
    }

    override fun getItem(position: Int): T {
        return list[position]
    }

}