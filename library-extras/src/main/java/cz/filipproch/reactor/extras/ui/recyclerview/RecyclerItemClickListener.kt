package cz.filipproch.reactor.extras.ui.recyclerview

import android.view.View

/**
 * TODO: add description

 * @author Filip Prochazka (filip.prochazka@ubnt.com)
 */
interface RecyclerItemClickListener {

    fun onClick(view: View, position: Int)

    fun onLongClick(view: View, position: Int)

}
