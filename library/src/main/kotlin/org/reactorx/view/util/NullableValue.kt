package org.reactorx.view.util

/**
 * @author Filip Prochazka (@filipproch)
 */
data class NullableValue<out T: Any?>(
        val value: T
)