package org.reactorx.presenter

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * @author Filip Prochazka (@filipproch)
 */
abstract class PresenterFactory<out P: Presenter<*>> : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(clazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return newInstance().apply {
            onPostCreated()
        } as T
    }

    abstract fun newInstance(): P

}