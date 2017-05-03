package cz.filipproch.reactor.extras.ui.recyclerview.adapter

import android.support.v7.widget.RecyclerView
import cz.filipproch.reactor.extras.ui.recyclerview.event.AdapterItemClickedEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ReactorRecyclerDataAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    private val itemClickSubject = PublishSubject.create<AdapterItemClickedEvent<T>>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickSubject.onNext(
                    AdapterItemClickedEvent(position, getItem(position)))
        }
    }

    abstract fun getItem(position: Int): T

    fun itemClicks(): Observable<AdapterItemClickedEvent<T>> {
        return itemClickSubject
    }

}