package cz.filipproch.reactor.extras.ui.views.events

import android.content.Intent
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO: add description
 */
class ActivityResultEvent(
        val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
) : ReactorUiEvent