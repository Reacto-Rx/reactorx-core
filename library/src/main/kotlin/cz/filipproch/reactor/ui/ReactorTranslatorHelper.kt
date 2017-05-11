package cz.filipproch.reactor.ui

import android.annotation.SuppressLint
import android.support.v4.app.FragmentManager
import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.base.translator.TranslatorFactory

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
object ReactorTranslatorHelper {

    @Suppress("UNCHECKED_CAST")
    fun <T : ReactorTranslator> findTranslatorFragment(fragmentManager: FragmentManager): ReactorTranslatorFragment<T>? {
        return fragmentManager.findFragmentByTag(ReactorTranslatorFragment.TAG)
                as ReactorTranslatorFragment<T>?
    }

    @SuppressLint("CommitTransaction")
    fun <T : ReactorTranslator> getTranslatorFromFragment(fragmentManager: FragmentManager, translatorFactory: TranslatorFactory<T>): T {
        var translatorFragment = findTranslatorFragment<T>(fragmentManager)
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