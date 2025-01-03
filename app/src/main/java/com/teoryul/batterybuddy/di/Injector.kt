package com.teoryul.batterybuddy.di

typealias Definitions = Injector.() -> Unit

/**
 * Notes:
 * - there is no support for scoped dependencies, ie. custom scope or activity/fragment lifecycle
 * - the maps holding the dependencies are not cleared even if there is no other class that is using them
 */
object Injector {

    val singletonInstances = mutableMapOf<Class<*>, Lazy<Any>>()
    val factoryProviders = mutableMapOf<Class<*>, () -> Any>()

    fun module(block: Injector.() -> Unit): Definitions {
        return block
    }

    fun addDependencies(definitions: List<Definitions>) {
        definitions.forEach { add -> Injector.add() }
    }

    inline fun <reified T : Any> singleton(noinline provider: () -> T) {
        singletonInstances[T::class.java] = lazy(provider)
    }

    inline fun <reified T : Any> factory(noinline provider: () -> T) {
        factoryProviders[T::class.java] = provider
    }

    inline fun <reified T : Any> get(): T {
        val instance = T::class.java

        if (singletonInstances.containsKey(instance)) {
            return singletonInstances[instance]?.value as? T
                ?: throw IllegalArgumentException("No singleton registered for ${T::class.java}")
        }

        if (factoryProviders.containsKey(instance)) {
            return factoryProviders[instance]?.invoke() as? T
                ?: throw IllegalArgumentException("No factory provider registered for ${T::class.java}")
        }

        throw IllegalArgumentException("No instance definition registered for ${T::class.java}")
    }
}