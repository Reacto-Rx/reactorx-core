package cz.filipproch.reactor.util

import android.app.Activity
import android.support.test.rule.ActivityTestRule

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class CustomActivityTestRule<T : Activity>(clazz: Class<T>) : ActivityTestRule<T>(clazz) {

    private var currentActivity: T? = null

    @Suppress("UNCHECKED_CAST")
    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        currentActivity = getResumedActivityInstance() as T
    }

    override fun afterActivityFinished() {
        super.afterActivityFinished()
        waitForActivityToFinish(checkNotNull(currentActivity))
    }

}