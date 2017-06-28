package cz.filipproch.reactor.common.views.model

import android.graphics.drawable.Drawable
import cz.filipproch.reactor.base.view.UiModel

/**
 * TODO: add description
 */
data class ToolbarUiModel(
        val title: String?,
        val homeAsUpEnabled: Boolean?,
        val homeIndicator: Drawable?
) : UiModel {
    override fun getType(): Class<*> {
        return ToolbarUiModel::class.java
    }
}