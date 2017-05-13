package cz.filipproch.reactor.extras.ui.views.activity

import cz.filipproch.reactor.base.translator.IReactorTranslator

@Deprecated("Renamed to ExtendedReactorCompatActivity")
abstract class ToolbarReactorActivity<T : IReactorTranslator> : ToolbarReactorCompatActivity<T>()