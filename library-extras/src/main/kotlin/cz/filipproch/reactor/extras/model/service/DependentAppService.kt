package cz.filipproch.reactor.extras.model.service

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class DependentAppService : BaseAppService() {

    private val dependencies = mutableListOf<ServiceDependency<*>>()

    fun <S : IAppService> dependency(clazz: Class<S>): ServiceDependency<S> {
        val dependency = ServiceDependency(clazz)

        synchronized(dependencies) {
            dependencies.add(dependency)
        }

        return dependency
    }

    override fun dispose() {
        super.dispose()
        dependencies.forEach { it.dispose() }
    }
}