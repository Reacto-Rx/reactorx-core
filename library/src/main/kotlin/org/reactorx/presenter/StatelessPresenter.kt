package org.reactorx.presenter

import io.reactivex.Observable
import org.reactorx.state.StateStoreTransformer
import org.reactorx.state.model.Action
import org.reactorx.view.model.UiEvent

/**
 * @author Filip Prochazka (@filipproch)
 */
abstract class StatelessPresenter : Presenter<Unit>() {

    final override val initialState = Unit

    final override val transformers: Array<StateStoreTransformer<Action, Action>>
        get() = super.transformers

    final override val middleware: Array<StateStoreTransformer<Action, Action>>
        get() = super.middleware

    final override fun onCreateStreams(shared: Observable<out UiEvent>): Array<Observable<out org.reactorx.presenter.model.Action>> {
        return super.onCreateStreams(shared)
    }

    final override fun reduceState(previousState: Unit, action: Action) {
        super.reduceState(previousState, action)
    }

    final override fun stateReducer(previousState: Unit, action: org.reactorx.presenter.model.Action) {
        super.stateReducer(previousState, action)
    }

}