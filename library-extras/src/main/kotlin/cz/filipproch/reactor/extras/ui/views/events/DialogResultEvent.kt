package cz.filipproch.reactor.extras.ui.views.events

import android.os.Bundle
import cz.filipproch.reactor.base.view.ReactorUiEvent

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class DialogResultEvent(val requestCode: Int, val resultCode: Int, val extras: Bundle? = null) : ReactorUiEvent