package org.reactorx.state

import io.reactivex.ObservableTransformer
import org.reactorx.state.model.Action

/**
 * @author Filip Prochazka (@filipproch)
 */
data class Middleware(
        val transformer: ObservableTransformer<Action, Action>,
        val phase: Int
) {
    companion object {
        const val PHASE_BEFORE_STATE_CHANGED = 1
        const val PHASE_AFTER_STATE_CHANGED = 2
    }
}