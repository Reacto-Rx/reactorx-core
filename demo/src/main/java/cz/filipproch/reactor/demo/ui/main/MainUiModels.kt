package cz.filipproch.reactor.demo.ui.main

import cz.filipproch.reactor.base.view.ReactorUiModel

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class MainUiModel(
        val postTitle: String?,
        val postContent: String?,
        val isLoading: Boolean,
        val success: Boolean?
) : ReactorUiModel {

    override fun getType(): Class<*> {
        return MainUiModel::class.java
    }

    companion object {
        val IDLE = MainUiModel(null, null, false, null)
        val LOADING = MainUiModel(null, null, true, null)
        val ERROR = MainUiModel(null, null, false, false)

        fun success(postTitle: String, postContent: String): MainUiModel {
            return MainUiModel(postTitle, postContent, false, true)
        }
    }
}