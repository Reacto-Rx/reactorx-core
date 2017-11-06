package org.reactorx2.state.util

import org.reactorx2.state.StateStore

/**
 * Util method to cast generic [StateStore] to one with specific type
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> StateStore<*>.castTo(): StateStore<T> {
    if (this.currentState !is T) {
        throw IllegalStateException("Invalid StateStore type cast")
    }
    return this as StateStore<T>
}