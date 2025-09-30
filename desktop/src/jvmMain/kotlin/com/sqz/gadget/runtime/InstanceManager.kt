package com.sqz.gadget.runtime

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.reflect.KClass

/**
 * InstanceManager is a class that manages the instances of the class.
 *
 * It is used to create the instance and hold the instance until the application is closed or the
 * instance is destroyed or overwritten.
 *
 *
 * Samples:
 * Create general instance `class ClassName : InstanceManager() { /* ... */ }`.
 * Create instance with key `class ClassName(key: String) : InstanceManager(key) { /* ... */ }`
 *
 * @see InstanceManager.Companion.getInstance
 */
open class InstanceManager {
    protected data class Instances(
        val key: String,
        val instance: Any,
    )

    private val _instanceList: MutableList<Instances> = mutableListOf()

    private var _instance: Any? = null

    @Suppress("unused")
    companion object {
        /**
         * Primary instance of [InstanceManager]
         * Used to hold all the instances of the class until the application is closed
         */
        val current = InstanceManager()

        /**
         * Get/Create the instance of the class but extended from [InstanceManager]
         *
         * Sample: `val instance: className = InstanceManager.getInstance()`
         *
         * @param IM the class to be extended from [InstanceManager]
         * @return the instance of the class
         */
        inline fun <reified IM : InstanceManager> getInstance(): IM {
            return try {
                current.getInstance() as IM
            } catch (_: NullPointerException) {
                IM::class.java.getDeclaredConstructor().newInstance()
            }
        }

        /**
         * Get/Create the instance of the class by key but extended from [InstanceManager]
         *
         * Sample: `
         * val instance: className = InstanceManager.getInstance(className::class, "keyName")
         * `
         * @param IM the class to be extended from [InstanceManager]
         * @param key the key of the instance; restore the instance if the key is the same
         * @return the instance of the class
         */
        inline fun <reified IM : InstanceManager> getInstance(clazz: KClass<IM>, key: String): IM {
            return try {
                current.getInstance(key) as IM
            } catch (_: NullPointerException) {
                val constructor = clazz.java.getDeclaredConstructor(String::class.java)
                constructor.newInstance(key)
            }
        }

        /**
         * Destroy the instance of the class
         */
        fun destroy() {
            current._instance = null
        }

        /**
         * Destroy the instance of the class by key
         */
        fun destroy(key: String) {
            current._instanceList.removeIf { it.key == key }
        }

        /**
         * Destroy all the instances of the class
         */
        fun destroyAll() {
            current._instance = null
            current._instanceList.clear()
        }

        /**
         * Clear the instances list
         */
        fun clearList() {
            current._instanceList.clear()
        }
    }

    // Disable default constructor
    private constructor()

    /**
     * Get the instance of the class by key
     */
    fun getInstance(key: String): Any? {
        return this._instanceList.find { it.key == key }?.instance
    }

    /**
     * Get the instance of the class
     */
    fun getInstance(): Any? {
        return this._instance
    }

    /**
     * Create the instance of the extended class
     */
    protected constructor(key: String? = null) {
        if (key != null) {
            if (current._instanceList.find { it.key == key } == null) {
                current._instanceList.add(Instances(key, this))
            } else if (current._instanceList.filter { it.key == key }.size > 1) {
                throw ArrayStoreException("Duplicate key: $key")
            }
        } else {
            current._instance = this
        }
    }

    /**
     * Global scope launch
     */
    @Suppress("unused")
    @OptIn(DelicateCoroutinesApi::class)
    protected fun globalScopeLaunch(lambda: suspend () -> Unit) {
        GlobalScope.launch { lambda() }
    }
}
