package org.reactorx.state.model.impl

import org.reactorx.state.model.Action

/**
 * [Action] to cause no mutation on the [org.reactorx.state.StateStore] state
 * (not guaranteed)
 */
object Void : Action