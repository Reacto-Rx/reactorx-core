package org.reactorx.state

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import org.reactorx.state.model.Action
import org.reactorx.state.model.impl.Init
import org.reactorx.state.model.impl.Void

/**
 * @author Filip Prochazka (@filipproch)
 */
class StateStore<T>(
        private val reducer: (T, Action) -> T,
        initialState: T,
        private val transformers: List<ObservableTransformer<Action, Action>>,
        private val middleware: List<Middleware>,
        subscribeImmediately: Boolean = false,
        private val errorCallback: ((Throwable) -> Unit)? = null,
        /* Hack not to break build with upgrade to newer version, TODO: remove */
        private val extraTransformerObservablesObtainer: ((Observable<Action>) -> Array<Observable<out org.reactorx.presenter.model.Action>>)? = null
) {

    private val subject = PublishSubject.create<Action>()
    var currentState: T = initialState
        private set

    private val preStateMiddlewareSubject = PublishSubject.create<Action>()
    private val postStateMiddlewareSubject = PublishSubject.create<Action>()

    private val internalSubscriptionsDisposable: CompositeDisposable = CompositeDisposable()

    private val transformer = ObservableTransformer<Action, Action> { stream ->
        stream.publish { sharedStream ->
            var observables: List<Observable<out Action>> = transformers.map {
                sharedStream.compose(it)
                        .doOnError { errorCallback?.invoke(it) }
            }

            extraTransformerObservablesObtainer?.let {
                observables = observables.plus(it.invoke(sharedStream))
            }

            Observable.mergeDelayError(observables)
        }
    }

    private val reduceTransformer = ObservableTransformer<Action, Pair<Action, T>> { actions ->
        actions.scan(Pair(ACTION_VOID, initialState), { stateWrapper: Pair<Action, T>, action: Action ->
            Pair(action, internalReducer(stateWrapper.second, action))
        })
    }

    private val observable = subject
            .compose(transformer)
            .startWith(ACTION_INIT)
            .doOnNext { invokePreStateMiddleware(it) }
            .compose(reduceTransformer)
            .doOnNext { (action, _) -> invokePostStateMiddleware(action) }
            .map { (_, newState) -> newState }
            .replay(1)
            .autoConnect()
            .doOnError { errorCallback?.invoke(it) }

    init {
        // Initialize middleware streams
        internalSubscriptionsDisposable.add(
                preStateMiddlewareSubject
                        .compose(newMiddlewareTransformer(Middleware.PHASE_BEFORE_STATE_CHANGED))
                        .doOnError { errorCallback?.invoke(it) }
                        .subscribe(dispatch())
        )
        internalSubscriptionsDisposable.add(
                postStateMiddlewareSubject
                        .compose(newMiddlewareTransformer(Middleware.PHASE_AFTER_STATE_CHANGED))
                        .doOnError { errorCallback?.invoke(it) }
                        .subscribe(dispatch())
        )

        // If configured, immediately accept dispatched actions (don't wait for observe() to be called)
        if (subscribeImmediately) {
            internalSubscriptionsDisposable.add(observable.subscribe())
        }
    }

    private fun invokePreStateMiddleware(
            action: Action
    ) {
        preStateMiddlewareSubject.onNext(action)
    }

    private fun invokePostStateMiddleware(
            action: Action
    ) {
        postStateMiddlewareSubject.onNext(action)
    }

    private fun internalReducer(
            state: T,
            action: Action
    ): T {
        currentState = reducer.invoke(state, action)
        return currentState
    }

    private fun newMiddlewareTransformer(
            phase: Int
    ): ObservableTransformer<Action, Action> {
        return ObservableTransformer { actions ->
            actions.publish { sharedStream ->
                Observable.mergeDelayError(
                        middleware.filter { it.phase == phase }
                                .map { it.transformer }
                                .map { sharedStream.compose(it) }
                )
            }
        }
    }

    fun observe(): Observable<T> = observable

    fun dispatch(input: Action) {
        if (internalSubscriptionsDisposable.isDisposed) {
            throw RuntimeException("StateStore is disposed")
        }

        subject.onNext(input)
    }

    fun dispatch(): Consumer<Action> = Consumer { dispatch(it) }

    fun dispatchAsync(
            input: Action
    ) = Completable.create {
        dispatch(input)

        it.onComplete()
    }

    fun dispose() {
        subject.onComplete()
        internalSubscriptionsDisposable.dispose()
    }

    companion object {
        val ACTION_INIT get() = Init()
        val ACTION_VOID get() = Void()
    }

    class Builder<T>(
            private val initialState: T
    ) {

        private lateinit var reducer: (T, Action) -> T
        private val transformers: MutableList<ObservableTransformer<Action, Action>> = mutableListOf()
        private val middleware: MutableList<Middleware> = mutableListOf()
        private var subscribeImmediately: Boolean = false
        private var errorCallback: ((Throwable) -> Unit)? = null

        private var extraTransformerObservablesObtainer: ((Observable<Action>) -> Array<Observable<out org.reactorx.presenter.model.Action>>)? = null

        fun enableInputPasstrough(
                filter: ((Action) -> Boolean)? = null
        ): Builder<T> {
            withTransformer(ObservableTransformer { inputActions ->
                if (filter == null) {
                    inputActions
                } else {
                    inputActions.filter(filter)
                }
            })

            return this
        }

        fun withReducer(
                reducer: (T, Action) -> T
        ): Builder<T> {
            this.reducer = reducer
            return this
        }

        fun withTransformer(
                vararg transformers: ObservableTransformer<Action, Action>
        ): Builder<T> {
            this.transformers.addAll(transformers)
            return this
        }

        fun withTransformers(
                transformers: Collection<ObservableTransformer<Action, Action>>
        ): Builder<T> {
            this.transformers.addAll(transformers)
            return this
        }

        fun withMiddleware(
                vararg transformers: ObservableTransformer<Action, Action>,
                phase: Int = Middleware.PHASE_AFTER_STATE_CHANGED
        ): Builder<T> {
            this.middleware.addAll(transformers.map {
                Middleware(it, phase)
            })
            return this
        }

        fun withMiddlewares(
                transformers: Collection<ObservableTransformer<Action, Action>>,
                phase: Int = Middleware.PHASE_AFTER_STATE_CHANGED
        ): Builder<T> {
            this.middleware.addAll(transformers.map {
                Middleware(it, phase)
            })
            return this
        }

        fun subscribeImmediately(): Builder<T> {
            this.subscribeImmediately = true
            return this
        }

        fun errorCallback(
                callback: (Throwable) -> Unit
        ): Builder<T> {
            this.errorCallback = callback
            return this
        }

        fun extraTransformerObservablesObtainer(
                action: ((Observable<Action>) -> Array<Observable<out org.reactorx.presenter.model.Action>>)?
        ): Builder<T> {
            this.extraTransformerObservablesObtainer = action
            return this
        }

        fun build(): StateStore<T> {
            return StateStore(
                    reducer,
                    initialState,
                    transformers,
                    middleware,
                    subscribeImmediately,
                    errorCallback,
                    extraTransformerObservablesObtainer
            )
        }

    }

}