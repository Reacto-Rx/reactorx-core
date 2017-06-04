package cz.filipproch.reactor.common.views.model

import android.support.v4.app.Fragment
import cz.filipproch.reactor.base.view.ReactorUiModel

/**
 * TODO: add description
 */
class ContentFragmentModel(
        val fragment: Fragment?
) : ReactorUiModel {
    override fun getType(): Class<*> {
        return ContentFragmentModel::class.java
    }
}