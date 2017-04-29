package cz.filipproch.reactor.extras.ui.iface

import android.view.View

/**
 * Interface for [cz.filipproch.reactor.base.view.ReactorView] that is using
 * Android Layout XML for it's layout
 *
 * @author Filip Prochazka (@filipproch)
 */
interface AndroidLayoutView {

    /**
     * Should return Android Layout resource ID, that will be used for the View
     */
    val layoutResourceId: Int

    /**
     * Can be used with libraries like ButterKnife to bind your [View]s to fields
     */
    fun onBindViews(contentView: View) {}

}