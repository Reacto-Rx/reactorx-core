package cz.filipproch.reactor.extras.ui.views.actions

import android.app.Activity
import cz.filipproch.reactor.base.view.ReactorUiAction

object FinishActivityAction : ReactorUiAction

class FinishActivityWithResultAction(val resultCode: Int) : ReactorUiAction

class StartActivityAction(val activity: Class<out Activity>): ReactorUiAction

class StartActivityForResultAction(val activity: Class<out Activity>, val requestCode: Int): ReactorUiAction