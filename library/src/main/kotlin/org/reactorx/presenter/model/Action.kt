package org.reactorx.presenter.model

/**
 * Simple object delivered to [org.reactorx.presenter.Presenter]'s reduce function
 * to modify internal state
 */
@Deprecated("Replaced by org.reactorx.state.model.Action")
interface Action : org.reactorx.state.model.Action