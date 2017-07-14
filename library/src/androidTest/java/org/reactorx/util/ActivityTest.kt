package org.reactorx.util

import org.junit.AfterClass
import org.junit.BeforeClass

abstract class ActivityTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun initializeActivityStateWatcher() {
            ActivityStateWatcher.watchActivityStates()
        }

        @AfterClass
        @JvmStatic
        fun deinitializeActivityStateWatcher() {
            ActivityStateWatcher.cleanup()
        }
    }

}