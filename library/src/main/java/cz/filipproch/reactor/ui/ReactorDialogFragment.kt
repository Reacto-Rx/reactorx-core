package cz.filipproch.reactor.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorLoader
import cz.filipproch.reactor.base.view.ReactorUiAction
import cz.filipproch.reactor.base.view.ReactorUiEvent
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.base.view.ReactorView
import cz.filipproch.reactor.ui.events.*
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ReactorDialogFragment<T : ReactorTranslator> :
        DialogFragment(),
        ReactorView<T>,
        LoaderManager.LoaderCallbacks<T> {

    private val TRANSLATOR_LOADER_ID = 1

    private lateinit var reactorViewHelper: ReactorViewHelper<T>

    private val activityEventsSubject = PublishSubject.create<ReactorUiEvent>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loaderManager.initLoader(TRANSLATOR_LOADER_ID, null, this)

        if (savedInstanceState == null) {
            activityEventsSubject.onNext(ViewCreatedEvent)
        } else {
            activityEventsSubject.onNext(ViewRestoredEvent(savedInstanceState))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = onCreateDialogView(LayoutInflater.from(context))
        bindViews(view)

        val builder = AlertDialog.Builder(context)
                .setView(view)

        interceptDialogBuilder(builder)

        val dialog = builder.create()
        dialog.setOnShowListener { dispatch(DialogShownEvent) }
        dialog.setOnCancelListener { dispatch(DialogCanceledEvent) }
        dialog.setOnDismissListener { dispatch(DialogDismissEvent) }
        return dialog
    }

    open fun interceptDialogBuilder(builder: AlertDialog.Builder) {
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reactorViewHelper = ReactorViewHelper(this)

        reactorViewHelper.onViewCreated()
        initUi()
    }

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

    override fun onLoadFinished(loader: Loader<T>?, data: T) {
        reactorViewHelper.onTranslatorAttached(data)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<T> {
        return TranslatorLoader(context, translatorFactory)
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

    inline fun <T : ReactorUiModel> receiveUpdatesOnUi(receiver: Observable<T>, crossinline action: (T) -> Unit) {
        receiveUpdatesOnUi(receiver, Consumer<T> {
            action.invoke(it)
        })
    }

    open fun initUi() {}

    abstract fun onCreateDialogView(inflater: LayoutInflater): View

    open fun bindViews(view: View) {}

}