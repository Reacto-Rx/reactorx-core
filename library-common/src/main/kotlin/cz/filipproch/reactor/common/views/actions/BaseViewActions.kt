package cz.filipproch.reactor.common.views.actions

import android.app.Activity
import cz.filipproch.reactor.base.view.ReactorUiAction

/**
 * TODO
 */
object FinishActivityAction : ReactorUiAction

/**
 * TODO
 */
class FinishActivityWithResultAction(
        val resultCode: Int
) : ReactorUiAction

/**
 * TODO
 */
class StartActivityAction(
        val activity: Class<out Activity>
) : ReactorUiAction

/**
 * TODO
 */
class StartActivityForResultAction(
        val activity: Class<out Activity>,
        val requestCode: Int
) : ReactorUiAction