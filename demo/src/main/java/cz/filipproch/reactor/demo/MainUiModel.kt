package cz.filipproch.reactor.demo

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
        fun idle(): MainUiModel {
            return MainUiModel(null, null, false, null)
        }

        fun loading(): MainUiModel {
            return MainUiModel(null, null, true, null)
        }

        fun error(): MainUiModel {
            return MainUiModel(null, null, false, false)
        }

        fun success(postTitle: String, postContent: String): MainUiModel {
            return MainUiModel(postTitle, postContent, false, true)
        }
    }
}