package org.reactorx.state.model.impl

import org.reactorx.state.model.Action

/**
 * [Action] that is dispatched to [org.reactorx.state.StateStore] internally
 * right after it's creation (instantiation)
 */
object Init : Action