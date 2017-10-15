package org.reactorx.state

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.reactorx.state.model.Action
import org.reactorx.state.model.impl.Init
import org.reactorx.state.model.impl.Void

/**
 * Store for a "state", represented by an object. Enforces unidirectional dataflow
 * by only allowing modifications by dispatching [Action]s to a reducer. Reducer is
 * a pure function that takes current "state" and [Action] as input and returns new "state"
 * as output.
 */
class StateStore<T> private constructor(
        private val reducer: (T, Action) -> T,
        initialState: T,
        private val transformers: List<StateStoreTransformer<Action, Action>>,
        private val middleware: List<Middleware>,
        subscribeImmediately: Boolean = false,
        private val errorCallback: ((Throwable) -> Unit)? = null,
        private val transformersScheduler: Scheduler?,
        @Deprecated(" Hack not to break build with upgrade to newer version")
        private val extraTransformerObservablesObtainer: ((Observable<Action>) -> Array<Observable<out org.reactorx.presenter.model.Action>>)? = null
) {

    /**
     * The current "state" stored in the store
     */
    var currentState: T = initialState
        private set

    private val subject = PublishSubject.create<Action>()

    private val preStateMiddlewareSubject = PublishSubject.create<Action>()
    private val postStateMiddlewareSubject = PublishSubject.create<Action>()

    private val internalPostTransformationSubject = PublishSubject.create<Action>()

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

    private val observable by lazy {
        val source = if (transformersScheduler != null) {
            subject.observeOn(transformersScheduler)
        } else {
            subject
        }

        Observable.merge(
                source.compose(transformer),
                internalPostTransformationSubject
        )
                .startWith(ACTION_INIT)
                .doOnNext { invokePreStateMiddleware(it) }
                .compose(reduceTransformer)
                .doOnNext { (action, _) -> invokePostStateMiddleware(action) }
                .map { (_, newState) -> newState }
                .replay(1)
                .autoConnect()
                .doOnError { errorCallback?.invoke(it) }
    }

    init {
        // Bind StateStore to transformers
        transformers.forEach { it.bindStateStore(this) }
        middleware.forEach { it.transformer.bindStateStore(this) }

        // Initialize middleware streams
        internalSubscriptionsDisposable.add(
                preStateMiddlewareSubject
                        .compose(newMiddlewareTransformer(Middleware.PHASE_BEFORE_STATE_CHANGED))
                        .doOnError { errorCallback?.invoke(it) }
                        .subscribeBy(onNext = internalPostTransformationSubject::onNext)
        )
        internalSubscriptionsDisposable.add(
                postStateMiddlewareSubject
                        .compose(newMiddlewareTransformer(Middleware.PHASE_AFTER_STATE_CHANGED))
                        .doOnError { errorCallback?.invoke(it) }
                        .subscribeBy(onNext = internalPostTransformationSubject::onNext)
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

    /**
     * Stream of "state", invoked whenever the "state" changes
     */
    fun observe(): Observable<T> = observable

    /**
     * Dispatches [input] [Action] to the transformers in the store
     */
    fun dispatch(input: Action) {
        if (internalSubscriptionsDisposable.isDisposed) {
            throw RuntimeException("StateStore is disposed")
        }

        subject.onNext(input)
    }

    /**
     * Dispatches consumed [Action] to the transformers in the store
     */
    fun dispatch(): Consumer<Action> = Consumer { dispatch(it) }

    /**
     * Dispatches [input] [Action] in a [Completable] to the transformers in the store
     */
    fun dispatchAsync(
            input: Action
    ) = Completable.create {
        dispatch(input)

        it.onComplete()
    }

    /**
     * Disposes internal resources in the store, making it unusable
     */
    fun dispose() {
        subject.onComplete()
        internalSubscriptionsDisposable.dispose()
    }

    companion object {
        /**
         * [Action] that is dispatched to [org.reactorx.state.StateStore] internally
         * right after it's creation (instantiation)
         */
        val ACTION_INIT get() = Init

        /**
         * [Action] to cause no mutation on the [org.reactorx.state.StateStore] state
         * (not guaranteed)
         */
        val ACTION_VOID get() = Void
    }

    /**
     * Builder for new [StateStore] instance
     */
    class Builder<T>(
            private val initialState: T,
            func: (Builder<T>.() -> Unit)? = null
    ) {

        private lateinit var reducer: (T, Action) -> T
        private val transformers: MutableList<StateStoreTransformer<Action, Action>> = mutableListOf()
        private val middleware: MutableList<Middleware> = mutableListOf()
        private var subscribeImmediately: Boolean = false
        private var errorCallback: ((Throwable) -> Unit)? = null
        private var transformersScheduler: Scheduler? = null

        @Deprecated("Remains for legacy support reasons")
        private var extraTransformerObservablesObtainer: ((Observable<Action>) -> Array<Observable<out org.reactorx.presenter.model.Action>>)? = null

        init {
            func?.invoke(this)
        }

        /**
         * TODO
         */
        fun enableInputPasstrough(
                filter: ((Action) -> Boolean)? = null
        ): Builder<T> {
            withTransformer(StateStoreTransformer { inputActions, _ ->
                if (filter == null) {
                    inputActions
                } else {
                    inputActions.filter(filter)
                }
            })

            return this
        }

        /**
         * TODO
         */
        fun withReducer(
                reducer: (T, Action) -> T
        ): Builder<T> {
            this.reducer = reducer
            return this
        }

        /**
         * TODO
         */
        fun withTransformer(
                vararg transformers: StateStoreTransformer<Action, Action>
        ): Builder<T> {
            this.transformers.addAll(transformers)
            return this
        }

        /**
         * TODO
         */
        fun withTransformer(
                vararg transformers: ObservableTransformer<Action, Action>
        ) = withTransformers(transformers.map { StateStoreTransformer.from(it) })

        /**
         * TODO
         */
        fun withTransformers(
                transformers: Collection<StateStoreTransformer<Action, Action>>
        ): Builder<T> {
            this.transformers.addAll(transformers)
            return this
        }

        /**
         * TODO
         */
        fun withMiddleware(
                vararg transformers: StateStoreTransformer<Action, Action>,
                phase: Int = Middleware.PHASE_AFTER_STATE_CHANGED
        ): Builder<T> {
            this.middleware.addAll(transformers.map {
                Middleware(it, phase)
            })
            return this
        }

        /**
         * TODO
         */
        fun withMiddleware(
                vararg transformers: ObservableTransformer<Action, Action>,
                phase: Int = Middleware.PHASE_AFTER_STATE_CHANGED
        ) = withMiddlewares(
                transformers = transformers.map { StateStoreTransformer.from(it) },
                phase = phase
        )

        /**
         * TODO
         */
        fun withMiddlewares(
                transformers: Collection<StateStoreTransformer<Action, Action>>,
                phase: Int = Middleware.PHASE_AFTER_STATE_CHANGED
        ): Builder<T> {
            this.middleware.addAll(transformers.map {
                Middleware(it, phase)
            })
            return this
        }

        /**
         * TODO
         */
        fun withScheduler(scheduler: Scheduler): Builder<T> {
            this.transformersScheduler = scheduler
            return this
        }

        /**
         * TODO
         */
        fun subscribeImmediately(): Builder<T> {
            this.subscribeImmediately = true
            return this
        }

        /**
         * TODO
         */
        fun errorCallback(
                callback: (Throwable) -> Unit
        ): Builder<T> {
            this.errorCallback = callback
            return this
        }

        /**
         * TODO
         */
        fun extraTransformerObservablesObtainer(
                action: ((Observable<Action>) -> Array<Observable<out org.reactorx.presenter.model.Action>>)?
        ): Builder<T> {
            this.extraTransformerObservablesObtainer = action
            return this
        }

        /**
         * TODO
         */
        fun build(): StateStore<T> {
            return StateStore(
                    reducer,
                    initialState,
                    transformers,
                    middleware,
                    subscribeImmediately,
                    errorCallback,
                    transformersScheduler,
                    extraTransformerObservablesObtainer
            )
        }

    }

}