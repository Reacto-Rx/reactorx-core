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

    @SuppressLint("CommitTransaction")
    @Suppress("UNCHECKED_CAST")
    fun <T: ReactorTranslator> getTranslatorFromFragment(fragmentManager: FragmentManager, translatorFactory: TranslatorFactory<T>): T {
        var translatorFragment = fragmentManager.findFragmentByTag(ReactorTranslatorFragment.TAG)
                as ReactorTranslatorFragment<T>?
        if (translatorFragment == null || translatorFragment.invalid) {
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