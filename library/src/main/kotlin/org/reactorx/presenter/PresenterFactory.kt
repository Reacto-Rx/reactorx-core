package org.reactorx.presenter

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

/**
 * Factory class for [Presenter]
 */
abstract class PresenterFactory<out P: Presenter<*>> : ViewModelProvider.Factory {

    override final fun <T : ViewModel> create(clazz: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return newInstance().apply {
            onPostCreated()
        } as T
    }

    abstract fun newInstance(): P

}