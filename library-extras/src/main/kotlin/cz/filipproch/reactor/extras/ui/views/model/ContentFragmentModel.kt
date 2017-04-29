package cz.filipproch.reactor.extras.ui.views.model

import android.support.v4.app.Fragment
import cz.filipproch.reactor.base.view.ReactorUiModel

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class ContentFragmentModel(val fragment: Fragment?) : ReactorUiModel {
    override fun getType(): Class<*> {
        return ContentFragmentModel::class.java
    }
}