package cz.filipproch.reactor.ui

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorLoader
import cz.filipproch.reactor.base.view.*
import cz.filipproch.reactor.ui.events.*
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ReactorActivity<T : ReactorTranslator> :
        AppCompatActivity(),
        ReactorView<T>,
        LoaderManager.LoaderCallbacks<T> {

    private val TRANSLATOR_LOADER_ID = 1

    private lateinit var reactorViewHelper: ReactorViewHelper<T>

    private val activityEventsSubject = PublishSubject.create<ReactorUiEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reactorViewHelper = ReactorViewHelper(this)

        setContentView(getLayoutResId())
        initUi()

        reactorViewHelper.onViewCreated()
        supportLoaderManager.initLoader(TRANSLATOR_LOADER_ID, null, this)

        if (savedInstanceState == null) {
            activityEventsSubject.onNext(ViewCreatedEvent)
        } else {
            activityEventsSubject.onNext(ViewRestoredEvent(savedInstanceState))
        }
    }

    override fun onEmittersInit() {
        registerEmitter(activityEventsSubject)
    }

    override fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>) {
    }

    override fun onConnectActionChannel(actionStream: Observable<out ReactorUiAction>) {
    }

    override fun dispatch(event: ReactorUiEvent) {
        activityEventsSubject.onNext(event)
    }

    open fun initUi() {}

    abstract fun getLayoutResId(): Int

    override fun onStart() {
        super.onStart()
        activityEventsSubject.onNext(ViewAttachedEvent)
    }

    override fun onStop() {
        super.onStop()
        activityEventsSubject.onNext(ViewDetachedEvent)
    }

    override fun onDestroy() {
        super.onDestroy()
        activityEventsSubject.onNext(ViewDestroyedEvent)
        reactorViewHelper.onViewDestroyed()
    }

    override fun onLoadFinished(loader: Loader<T>?, data: T) {
        reactorViewHelper.onTranslatorAttached(data)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<T> {
        return TranslatorLoader(this, translatorFactory)
    }

    override fun onLoaderReset(loader: Loader<T>?) {
        reactorViewHelper.onTranslatorDetached()
    }

    override fun registerEmitter(emitter: Observable<out ReactorUiEvent>) {
        reactorViewHelper.registerEmitter(emitter)
    }

    override fun <T> receiveUpdatesOnUi(observable: Observable<T>, receiverAction: Consumer<T>) {
        reactorViewHelper.receiveUpdatesOnUi(observable, receiverAction)
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
        reactorViewHelper.receiveUpdatesOnUi(this, receiverAction)
    }

    fun <T> Observable<T>.consumeOnUi(action: (T) -> Unit) {
        consumeOnUi(Consumer<T> {
            action.invoke(it)
        })
    }

    override fun <M : ReactorUiModel, T> Observable<M>.mapToUi(consumer: Consumer<T>, mapper: ConsumerMapper<M, T>) {
        reactorViewHelper.receiveUpdatesOnUi(this.map { mapper.mapModelToUi(it) }, consumer)
    }

    fun <M : ReactorUiModel, T> Observable<M>.mapToUi(consumer: Consumer<T>, mapper: (M) -> T) {
        this.mapToUi(consumer, object : ConsumerMapper<M, T> {
            override fun mapModelToUi(model: M): T {
                return mapper.invoke(model)
            }
        })
    }

}
