package cz.filipproch.reactor.rx

import cz.filipproch.reactor.base.view.ReactorUiModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class TypeBehaviorSubjectTest {

    private lateinit var subject: TypeBehaviorSubject

    @Before
    fun setupInstance() {
        subject = TypeBehaviorSubject.create()
    }

    @Test
    fun testObjectBeingPersisted() {
        val test = TestObject()
        subject.onNext(test)

        val observable = subject
        val result = observable.take(1).blockingFirst()

        assertThat(test).isEqualTo(result)
    }

    @Test
    fun testRememberTwoDifferentTypes() {
        val test = TestObject()
        val test2 = TestObject2()

        subject.onNext(test)
        subject.onNext(test2)

        val observable = subject
        val results = observable.take(2).blockingIterable()

        assertThat(results).hasSize(2)

        assertThat(results).containsAll(listOf(
                test, test2
        ))
    }

    @Test
    fun testEmitCorrectItemWhenThereAreMultipleTypes() {
        val test = TestObject()
        val test2 = TestObject2()

        val emittedItems = mutableListOf<Any>()

        val observable = subject
        observable.subscribe {
            emittedItems.add(it)
        }

        subject.onNext(test)

        assertThat(emittedItems).hasSize(1)

        assertThat(emittedItems.last()).isEqualTo(test)

        subject.onNext(test2)

        assertThat(emittedItems).hasSize(2)

        assertThat(emittedItems.last()).isEqualTo(test2)
    }

    class TestObject : ReactorUiModel {
        override fun getType(): Class<*> {
            return TestObject::class.java
        }
    }

    class TestObject2 : ReactorUiModel {
        override fun getType(): Class<*> {
            return TestObject2::class.java
        }
    }

}