package cz.filipproch.reactor.extras.ui.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * TODO: add description

 * @author Filip Prochazka (filip.prochazka@ubnt.com)
 */
class RecyclerTouchListener(context: Context, recycler: RecyclerView, private val listener: RecyclerItemClickListener) : RecyclerView.OnItemTouchListener {

    private val gestureDetector: GestureDetector

    init {

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recycler.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    listener.onLongClick(child, recycler.getChildAdapterPosition(child))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && gestureDetector.onTouchEvent(e)) {
            listener.onClick(child, rv.getChildAdapterPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}
