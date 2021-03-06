package cz.filipproch.reactor.common.views.activity

import android.support.v7.app.ActionBar
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.view.UiModel
import cz.filipproch.reactor.common.views.events.OptionsItemSelectedEvent
import cz.filipproch.reactor.common.views.model.ToolbarUiModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 */
abstract class ToolbarReactorActivity<T : IReactorTranslator> : ExtendedReactorActivity<T>() {

    private val optionsItemSubject = PublishSubject.create<OptionsItemSelectedEvent>()

    override fun onUiReady() {
        super.onUiReady()
        bindToolbar()
    }

    override fun onEmittersInit() {
        super.onEmittersInit()
        registerEmitter(optionsItemSubject)
    }

    override fun onConnectModelStream(modelStream: Observable<out UiModel>) {
        super.onConnectModelStream(modelStream)

        modelStream.ofType(ToolbarUiModel::class.java).consumeOnUi {
            (title, homeAsUpEnabled, homeIndicator) ->
            supportActionBar?.let {
                if (title != null) {
                    it.title = title
                }

                if (homeAsUpEnabled != null) {
                    it.setDisplayHomeAsUpEnabled(homeAsUpEnabled)
                }

                if (homeIndicator != null) {
                    it.setHomeAsUpIndicator(homeIndicator)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (optionsMenuResId != NO_OPTIONS_MENU) {
            menuInflater.inflate(optionsMenuResId, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        optionsItemSubject.onNext(OptionsItemSelectedEvent(item))
        return super.onOptionsItemSelected(item)
    }

    private fun bindToolbar() {
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            setupActionBar(supportActionBar!!) // we now it's not null
        }
    }

    open fun setupActionBar(actionBar: ActionBar) {}

    open val optionsMenuResId: Int = NO_OPTIONS_MENU

    abstract val toolbar: Toolbar

    companion object {
        const val NO_OPTIONS_MENU = -1
    }

}