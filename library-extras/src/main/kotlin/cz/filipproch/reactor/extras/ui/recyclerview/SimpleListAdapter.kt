package cz.filipproch.reactor.extras.ui.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
@Deprecated("Depracated in favor of ReactorRecyclerListAdapter",
    ReplaceWith(
            "SimpleRecyclerListAdapter",
            "cz.filipproch.reactor.extras.ui.recyclerview.adapter.SimpleRecyclerListAdapter"
    )
)
class SimpleListAdapter<T>(val itemLayoutRes: Int, val viewBinder: DataViewBinder<T>) : RecyclerView.Adapter<SimpleListAdapter.SimpleViewHolder>() {

    private val data = mutableListOf<T>()

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        viewBinder.bindData(holder.itemView, data[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return SimpleViewHolder(
                LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false))
    }

    fun updateData(): Consumer<List<T>> {
        return Consumer {
            synchronized(data) {
                this.data.clear()
                this.data.addAll(it)
                notifyDataSetChanged()
            }
        }
    }

    class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface DataViewBinder<in T> {
        fun bindData(view: View, data: T)
    }

}