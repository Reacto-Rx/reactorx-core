package org.reactorx.presenter

import org.junit.Before
import org.junit.Test

/**
 * @author Filip Prochazka (@filipproch)
 */
class PresenterFactoryTest {

    private var factoryCalled: Boolean = false
    private val factory = object : PresenterFactory<TestPresenter>() {
        override fun newInstance(): TestPresenter {
            factoryCalled = true
            return TestPresenter()
        }
    }

    @Before
    fun prepareTest() {
        factoryCalled = false
    }

    @Test
    fun create() {
        val newInstance = factory.create(Presenter::class.java)

        assert(factoryCalled, { "Factory method not called" })

        assert(newInstance is TestPresenter, { "Returned instance is not instance of TestPresenter" })
    }

    private class TestPresenter : Presenter<Any>() {
        override val initialState = Any()
    }

}