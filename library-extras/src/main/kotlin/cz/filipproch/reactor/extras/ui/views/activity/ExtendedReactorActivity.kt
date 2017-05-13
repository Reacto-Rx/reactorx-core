package cz.filipproch.reactor.extras.ui.views.activity

import cz.filipproch.reactor.base.translator.IReactorTranslator

@Deprecated("Renamed to ExtendedReactorCompatActivity")
abstract class ExtendedReactorActivity<T : IReactorTranslator> : ExtendedReactorCompatActivity<T>()