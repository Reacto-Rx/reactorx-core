package cz.filipproch.reactor.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.base.view.ReactorView
import cz.filipproch.reactor.ui.events.*
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/**
 * [Fragment] implementation of [ReactorView]
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ReactorFragment<out T : ReactorTranslator> :
        Fragment(),
        ReactorView<T> {

    private val TRANSLATOR_LOADER_ID = 1

    private var reactorViewHelper: ReactorViewHelper<T>? = null

    private val activityEventsSubject = PublishSubject.create<ReactorUiEvent>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dispatch(ViewCreatedEvent(savedInstanceState))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reactorViewHelper = ReactorViewHelper(this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            onUiRestored(savedInstanceState)
        } else {
            onUiCreated()
        }

        onPostUiCreated()
    }

    override fun onStart() {
        super.onStart()
        reactorViewHelper?.bindTranslatorWithView(
                ReactorTranslatorHelper.getTranslatorFromFragment(childFragmentManager, translatorFactory)
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
        dispatch(ViewDestroyedEvent)
        reactorViewHelper?.onViewDestroyed()
    }

    override fun onEmittersInit() {
        registerEmitter(activityEventsSubject)
    }

    override fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>) {
    }

    override fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>) {
    }

    override fun onConnectActionChannel(actionStream: Observable<out ReactorUiAction>) {
    }

    override fun onConnectActionStream(actionStream: Observable<out ReactorUiAction>) {
    }

    override fun dispatch(event: ReactorUiEvent) {
        activityEventsSubject.onNext(event)
    }

    override fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        reactorViewHelper?.registerEmitter(emitter)
    }

    override fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>) {
        reactorViewHelper?.receiveUpdatesOnUi(observable, receiverAction)
    }

    @Deprecated("Replaced with extension function consumeOnUi", ReplaceWith(
            "receiver.consumeOnUi(action)"
    ))
    fun <T : ReactorUiModel> receiveUpdatesOnUi(receiver: Observable<T>, action: (T) -> Unit) {
        receiveUpdatesOnUi(receiver, Consumer<T> {
            action.invoke(it)
        })
    }

    override fun <T> Observable<T>.consumeOnUi(receiverAction: Consumer<T>) {
        reactorViewHelper?.receiveUpdatesOnUi(this, receiverAction)
    }

    fun <T> Observable<T>.consumeOnUi(action: (T) -> Unit) {
        consumeOnUi(Consumer<T> {
            action.invoke(it)
        })
    }

    /*
        ReactorFragment specific
     */

    /**
     * Called from [onViewCreated] is savedInstanceState is null
     */
    open fun onUiCreated() {
    }

    /**
     * Called from [onViewCreated] is savedInstanceState is not null
     */
    open fun onUiRestored(savedInstanceState: Bundle) {
    }

    /**
     * Called from [onViewCreated] after either [onUiCreated] or [onUiRestored] has been called
     *
     * This method is useful to set [android.view.View] listeners or other stuff that doesn't survive activity recreation
     */
    @Deprecated("This method was deprecated due to ambiguous name", ReplaceWith(
            "onUiReady"
    ))
    open fun onPostUiCreated() {
    }

    /**
     * Called from [onViewCreated] after either [onUiCreated] or [onUiRestored] has been called
     *
     * This method is useful to set [android.view.View] listeners or other stuff that doesn't survive activity recreation
     */
    open fun onUiReady() {
    }

    @Deprecated("This method is not part of the Reactor architecture and was moved to the 'extras' module")
    open fun getLayoutResId(): Int {
        return -1
    }

    @Deprecated("This method is not part of the Reactor architecture and was moved to the 'extras' module")
    open fun bindViews(view: View) {
    }

}