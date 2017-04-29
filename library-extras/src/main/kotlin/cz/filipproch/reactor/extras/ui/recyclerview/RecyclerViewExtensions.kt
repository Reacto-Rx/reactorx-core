package cz.filipproch.reactor.extras.ui.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import cz.filipproch.reactor.extras.ui.recyclerview.event.RecyclerItemClickedEvent
import io.reactivex.Observable

fun RecyclerView.itemClicks(): Observable<RecyclerItemClickedEvent> {
    return Observable.create {
        val listener = object : RecyclerItemClickListener {
            override fun onClick(view: View, position: Int) {
                it.onNext(RecyclerItemClickedEvent.clicked(position))
            }

            override fun onLongClick(view: View, position: Int) {
                it.onNext(RecyclerItemClickedEvent.longClicked(position))
            }
        }
        val touchListener = RecyclerTouchListener(context, this, listener)
        addOnItemTouchListener(touchListener)

        it.setCancellable { removeOnItemTouchListener(touchListener) }
    }
}