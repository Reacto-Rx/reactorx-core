package cz.filipproch.reactor.extras.ui.model

import android.graphics.drawable.Drawable
import cz.filipproch.reactor.base.view.ReactorUiModel

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
data class ToolbarUiModel(
        val title: String?,
        val homeAsUpEnabled: Boolean?,
        val homeIndicator: Drawable?
) : ReactorUiModel {
    override fun getType(): Class<*> {
        return ToolbarUiModel::class.java
    }
}