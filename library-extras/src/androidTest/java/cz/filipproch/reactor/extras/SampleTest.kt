package cz.filipproch.reactor.extras

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cz.filipproch.reactor.extras.util.TestActivity
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Filip Prochazka (@filipproch)
 */
@RunWith(AndroidJUnit4::class)
class SampleTest {

    @Rule
    @JvmField val activityRule = ActivityTestRule(TestActivity::class.java)

    @Test
    fun sampleTest() {
        assertThat(true).isTrue()
    }

}