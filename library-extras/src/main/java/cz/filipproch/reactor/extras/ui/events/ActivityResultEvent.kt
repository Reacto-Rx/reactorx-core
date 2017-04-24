package cz.filipproch.reactor.extras.ui.events

import android.content.Intent
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class ActivityResultEvent(val requestCode: Int, val resultCode: Int, val data: Intent?) : ReactorUiEvent