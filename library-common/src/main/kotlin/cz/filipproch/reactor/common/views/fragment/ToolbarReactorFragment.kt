package cz.filipproch.reactor.common.views.fragment

import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.view.ReactorUiModel
import cz.filipproch.reactor.common.views.model.ToolbarUiModel
import cz.filipproch.reactor.common.views.events.OptionsItemSelectedEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * TODO: add description
 */
abstract class ToolbarReactorFragment<T : IReactorTranslator> : ExtendedReactorFragment<T>() {

    private val optionsItemSubject = PublishSubject.create<OptionsItemSelectedEvent>()

    override fun onUiReady() {
        super.onUiReady()
        bindToolbar()
        if (optionsMenuResId != NO_OPTIONS_MENU) {
            setHasOptionsMenu(true)
        }
    }

    override fun onEmittersInit() {
        super.onEmittersInit()
        registerEmitter(optionsItemSubject)
    }

    override fun onConnectModelStream(modelStream: Observable<out ReactorUiModel>) {
        super.onConnectModelStream(modelStream)

        modelStream.ofType(ToolbarUiModel::class.java).consumeOnUi {
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
        optionsItemSubject.onNext(OptionsItemSelectedEvent(item))
        return super.onOptionsItemSelected(item)
    }

    private fun bindToolbar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        if (activity.supportActionBar != null) {
            setupActionBar(activity.supportActionBar!!) // we now it's not null
        }
    }

    /**
     * TODO
     */
    open fun setupActionBar(actionBar: ActionBar) {}

    /**
     * TODO
     */
    open val optionsMenuResId: Int = NO_OPTIONS_MENU

    /**
     * TODO
     */
    abstract val toolbar: Toolbar

    companion object {

        /**
         * TODO
         */
        const val NO_OPTIONS_MENU = -1

    }

}