package cz.filipproch.reactor.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.base.view.ReactorView
import cz.filipproch.reactor.ui.events.*
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/**
 * [AppCompatActivity] implementation of the [ReactorView]
 */
abstract class ReactorCompatActivity<T : IReactorTranslator> :
        AppCompatActivity(),
        ReactorView<T> {

    var reactorViewHelper: ReactorViewHelper<T>? = null
        private set

    private val activityEventsSubject = PublishSubject.create<ReactorUiEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reactorViewHelper = ReactorViewHelper(this)

        onCreateLayout()

        reactorViewHelper?.onReadyToRegisterEmitters()

        if (savedInstanceState != null) {
            onUiRestored(savedInstanceState)
        } else {
            onUiCreated()
        }

        onPostUiCreated()
        onUiReady()

        dispatch(ViewCreatedEvent(savedInstanceState))
    }

    override fun onEmittersInit() {
        registerEmitter(activityEventsSubject)
    }

    override fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>) {
    }

    override fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>) {
    }

    override fun dispatch(event: ReactorUiEvent) {
        activityEventsSubject.onNext(event)
    }

    override fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        reactorViewHelper?.registerEmitter(emitter)
    }

    override fun <T> Observable<T>.consumeOnUi(receiverAction: Consumer<T>) {
        reactorViewHelper?.receiveUpdatesOnUi(this, receiverAction)
    }

    fun <T> Observable<T>.consumeOnUi(action: (T) -> Unit) {
        consumeOnUi(Consumer<T> {
            action.invoke(it)
        })
    }

    override fun onStart() {
        super.onStart()
        reactorViewHelper?.bindTranslatorWithView(
                ReactorTranslatorHelper.getTranslatorFromFragment(supportFragmentManager, translatorFactory)
        )

        dispatch(ViewStartedEvent)
    }

    override fun onResume() {
        super.onResume()
        dispatch(ViewResumedEvent)
    }

    override fun onPause() {
        super.onPause()
        dispatch(ViewPausedEvent)
    }

    override fun onStop() {
        super.onStop()
        dispatch(ViewStoppedEvent)

        reactorViewHelper?.onViewNotUsable()
    }

    override fun onDestroy() {
        super.onDestroy()

        reactorViewHelper?.onViewDestroyed()
    }

    /*
        ReactorCompatActivity specific
     */

    /**
     * Called from [onCreate] is savedInstanceState is null
     */
    open fun onUiCreated() {
    }

    /**
     * Called from [onCreate] is savedInstanceState is not null
     */
    open fun onUiRestored(savedInstanceState: Bundle) {
    }

    /**
     * Called from [onCreate] after either [onUiCreated] or [onUiRestored] has been called
     *
     * This method is useful to set [android.view.View] listeners or other stuff that doesn't survive activity recreation
     */
    @Deprecated("This method was deprecated due to ambiguous name", ReplaceWith(
            "onUiReady"
    ))
    open fun onPostUiCreated() {
    }

    /**
     * Called from [onCreate] after either [onUiCreated] or [onUiRestored] has been called
     *
     * This method is useful to set [android.view.View] listeners or other stuff that doesn't survive activity recreation
     */
    open fun onUiReady() {

    }

    @Deprecated("This method is not part of the Reactor architecture and was moved to the 'extras' module",
            ReplaceWith(""),
            DeprecationLevel.ERROR)
    open fun getLayoutResId(): Int {
        return -1
    }

    /**
     * Called to create the layout for the view
     */
    abstract fun onCreateLayout()

}
