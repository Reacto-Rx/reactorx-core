package org.reactorx2.state

import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.reactorx2.state.model.Action

class StateHelpersKtTest {

    private val emittingSubject = PublishSubject.create<Action>()
    private val receivedActions: MutableList<Action> = mutableListOf()
    private val actionsConsumer = Consumer<Action> { receivedActions.add(it) }

    private lateinit var stateStore: StateStore<TestStoreModel>

    @Before
    fun prepareForTest() {
        stateStore = StateStore.Builder(initialState = TestStoreModel()) {

        }.build()
    }

    @Test
    fun testEpic() {

    }

    class ActionPositive : Action

    class ActionNegative : Action

    data class TestStoreModel(
            val value: Boolean = false
    )

}