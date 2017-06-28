package cz.filipproch.reactor.common.views.model

import android.support.v4.app.Fragment
import cz.filipproch.reactor.base.view.UiModel

/**
 * TODO: add description
 */
class ContentFragmentModel(
        val fragment: Fragment?
) : UiModel {
    override fun getType(): Class<*> {
        return ContentFragmentModel::class.java
    }
}