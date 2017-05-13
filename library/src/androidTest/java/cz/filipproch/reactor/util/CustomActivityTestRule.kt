package cz.filipproch.reactor.util

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.lifecycle.Stage
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Makes sure it terminates all activities before ending the test
 */
class CustomActivityTestRule<T : Activity>(clazz: Class<T>) : ActivityTestRule<T>(clazz) {

    override fun apply(base: Statement?, description: Description?): Statement {
        return ActivityCleanupStatement(super.apply(base, description))
    }

    private fun killAllRunningActivities() {
        val activitiesToDie = ActivityStateWatcher.activityStates
                .filter { it.value.stage != Stage.DESTROYED }
                .map { it.value.activity }

        activitiesToDie.forEach {
            finishActivitySync(it)
        }
    }

    private inner class ActivityCleanupStatement(private val statement: Statement) : Statement() {
        override fun evaluate() {
            try {
                statement.evaluate()
            } finally {
                killAllRunningActivities()
            }
        }
    }

}