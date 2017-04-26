package cz.filipproch.reactor.extras.ui.views

import cz.filipproch.reactor.base.translator.ReactorTranslator
import cz.filipproch.reactor.ui.ReactorFragment

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
@Deprecated("This class was renamed and moved to another package",
        ReplaceWith(
                "ExtendedReactorFragment<T>",
                "cz.filipproch.reactor.extras.ui.views.fragment.ExtendedReactorFragment"
        ),
        DeprecationLevel.WARNING)
abstract class BaseReactorFragment<T: ReactorTranslator> : ReactorFragment<T>()