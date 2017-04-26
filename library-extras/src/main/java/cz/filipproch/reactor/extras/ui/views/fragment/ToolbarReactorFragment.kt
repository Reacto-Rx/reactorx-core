package cz.filipproch.reactor.extras.ui.views.fragment

import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.extras.ui.views.events.OptionsItemSelectedEvent
import cz.filipproch.reactor.extras.ui.views.model.ToolbarUiModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class ToolbarReactorFragment<T : ReactorTranslator> : ExtendedReactorFragment<T>() {

    private val optionsItemSubject = PublishSubject.create<OptionsItemSelectedEvent>()

    override fun initUi() {
        bindToolbar()
    }

    override fun onEmittersInit() {
        super.onEmittersInit()
        registerEmitter(optionsItemSubject)
    }

    override fun onConnectModelChannel(modelStream: Observable<out ReactorUiModel>) {
        receiveUpdatesOnUi(modelStream.ofType(ToolbarUiModel::class.java)) {
            (title, homeAsUpEnabled, homeIndicator) ->
            (activity as AppCompatActivity).supportActionBar?.let {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (optionsMenuResId != NO_OPTIONS_MENU) {
            inflater.inflate(optionsMenuResId, menu)
            return
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        optionsItemSubject.onNext(OptionsItemSelectedEvent(menuItem = item))
        return super.onOptionsItemSelected(item)
    }

    private fun bindToolbar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        if (activity.supportActionBar != null) {
            setupActionBar(activity.supportActionBar!!) // we now it's not null
        }
    }

    open fun setupActionBar(actionBar: ActionBar) {}

    open val optionsMenuResId: Int = NO_OPTIONS_MENU

    abstract val toolbar: Toolbar

    companion object {
        const val NO_OPTIONS_MENU = -1
    }

}