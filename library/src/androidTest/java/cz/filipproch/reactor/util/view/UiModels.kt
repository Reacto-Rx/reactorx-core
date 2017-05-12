package cz.filipproch.reactor.util.view

import cz.filipproch.reactor.base.view.ReactorUiModel

object TestUiModel : ReactorUiModel {
    override fun getType(): Class<*> {
        return TestUiModel::class.java
    }
}