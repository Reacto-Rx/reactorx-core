package cz.filipproch.reactor.util

import android.support.test.espresso.IdlingResource

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
class ReactorViewIdlingResource : IdlingResource {

    private var viewInitializationCompleted = false
    private var callback: IdlingResource.ResourceCallback? = null

    fun viewInitializationCompleted() {
        viewInitializationCompleted = true
        callback?.onTransitionToIdle()
    }

    override fun getName(): String {
        return ReactorViewIdlingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        return viewInitializationCompleted
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }
}