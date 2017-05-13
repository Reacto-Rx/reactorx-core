package cz.filipproch.reactor.extras.ui.recyclerview

import android.view.View

@Deprecated("Depracated in favor of AdapterItemClickedEvent")
interface RecyclerItemClickListener {

    fun onClick(view: View, position: Int)

    fun onLongClick(view: View, position: Int)

}
