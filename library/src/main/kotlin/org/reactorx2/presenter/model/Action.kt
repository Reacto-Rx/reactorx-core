package org.reactorx2.presenter.model

/**
 * Simple object delivered to [org.reactorx2.presenter.Presenter]'s reduce function
 * to modify internal state
 */
@Deprecated("Replaced by org.reactorx.state.model.Action")
interface Action : org.reactorx2.state.model.Action