package cz.filipproch.reactor.common.views.events

import android.content.Intent
import cz.filipproch.reactor.base.view.UiEvent

/**
 * TODO: add description
 */
class ActivityResultEvent(
        val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
) : UiEvent