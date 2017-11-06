package org.reactorx2.state.model.impl

import org.reactorx2.state.model.Action

/**
 * [Action] that is dispatched to [org.reactorx2.state.StateStore] internally
 * right after it's creation (instantiation)
 */
object Init : Action