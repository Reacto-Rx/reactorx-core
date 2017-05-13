package cz.filipproch.reactor.extras.ui.views.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.extras.ui.iface.AndroidLayoutView
import cz.filipproch.reactor.extras.ui.views.events.ActivityResultEvent
import cz.filipproch.reactor.ui.ReactorFragment

/**
 * TODO: add description
 */
abstract class ExtendedReactorFragment<T : IReactorTranslator> :
        ReactorFragment<T>(),
        AndroidLayoutView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutResourceId, container, false)
        onBindViews(view)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dispatch(ActivityResultEvent(requestCode, resultCode, data))
    }

}