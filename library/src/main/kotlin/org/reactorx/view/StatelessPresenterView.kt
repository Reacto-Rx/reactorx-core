package org.reactorx.view

import org.reactorx.presenter.StatelessPresenter

/**
 * TODO
 */
interface StatelessPresenterView<P : StatelessPresenter> : PresenterView<Unit, P>