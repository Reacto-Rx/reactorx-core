package cz.filipproch.reactor.rx

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class TypeBehaviorSubjectTest {

    private lateinit var subject: TypeBehaviorSubject<TypedObject>

    @Before
    fun setupInstance() {
        subject = TypeBehaviorSubject.create()
    }

    @Test
    fun testObjectBeingPersisted() {
        val test = TestObject()
        subject.onNext(test)

        val observable = subject.asObservable()
        val result = observable.take(1).blockingFirst()

        assertThat(test).isEqualTo(result)
    }

    @Test
    fun testRememberTwoDifferentTypes() {

    }

    @Test
    fun testEmitCorrectItemWhenThereAreMultipleTypes() {

    }

    class TestObject : TypedObject {
        override fun getType(): Class<*> {
            return TestObject::class.java
        }
    }

    class TestObject2 : TypedObject {
        override fun getType(): Class<*> {
            return TestObject2::class.java
        }
    }

}