package cz.filipproch.reactor.ui.helper

import android.annotation.SuppressLint
import android.support.v4.app.FragmentManager
import cz.filipproch.reactor.base.translator.IReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory

/**
 * Helper class used by [cz.filipproch.reactor.base.view.ReactorView] implementations
 * which use [ReactorTranslatorFragment] for their [IReactorTranslator] persistence
 */
object ReactorTranslatorHelper {

    @Suppress("UNCHECKED_CAST")
    fun <T : IReactorTranslator> findTranslatorFragment(
            fragmentManager: FragmentManager
    ): ReactorTranslatorFragment<T>? {
        return fragmentManager.findFragmentByTag(ReactorTranslatorFragment.TAG)
                as ReactorTranslatorFragment<T>?
    }

    @SuppressLint("CommitTransaction")
    fun <T : IReactorTranslator> getTranslatorFromFragment(
            fragmentManager: FragmentManager,
            translatorFactory: TranslatorFactory<T>
    ): T {
        var translatorFragment = ReactorTranslatorHelper.findTranslatorFragment<T>(fragmentManager)
        if (translatorFragment == null || translatorFragment.isInvalid) {
            val oldFragment = translatorFragment
            translatorFragment = ReactorTranslatorFragment()
            translatorFragment.setTranslatorFactory(translatorFactory)
            val transaction = fragmentManager.beginTransaction()

            if (oldFragment != null) {
                transaction.remove(oldFragment)
            }

            transaction
                    .add(translatorFragment, ReactorTranslatorFragment.TAG)
                    .commitNow()
        }

        return requireNotNull(translatorFragment.translator)
    }

}