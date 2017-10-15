package org.reactorx.state

import org.reactorx.state.model.Action

/**
 * [Action] stream transformation, with option to determine when
 * it is invoked.
 */
data class Middleware(
        val transformer: StateStoreTransformer<Action, Action>,
        val phase: Int
) {
    companion object {
        /**
         * Invoked before the [StateStore] reducer function is applied
         */
        const val PHASE_BEFORE_STATE_CHANGED = 1

        /**
         * Invoked after the [StateStore] reducer function is applied
         */
        const val PHASE_AFTER_STATE_CHANGED = 2
    }
}