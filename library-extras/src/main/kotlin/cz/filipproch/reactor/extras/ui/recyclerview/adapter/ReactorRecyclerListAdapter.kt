package cz.filipproch.reactor.extras.ui.recyclerview.adapter

import android.support.v7.widget.RecyclerView

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ReactorRecyclerListAdapter<T, VH : RecyclerView.ViewHolder> : ReactorRecyclerDataAdapter<T, VH>() {

    protected val list: MutableList<T> = mutableListOf()

    fun updateData(newList: MutableList<T>) {
        synchronized(list) {
            list.clear()
            list.addAll(newList)
            notifyDataSetChanged()
        }
    }

    override fun getItem(position: Int): T {
        return list[position]
    }

}