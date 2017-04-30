package cz.filipproch.reactor.demo

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}