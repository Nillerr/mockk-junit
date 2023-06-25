package io.github.nillerr.mockk.junit5

import io.mockk.checkUnnecessaryStub
import io.mockk.isMockKMock
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.logging.Logger
import org.junit.platform.commons.logging.LoggerFactory
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class MockKExtension : AfterEachCallback {
    private val logger = LoggerFactory.getLogger(MockKExtension::class.java)

    override fun afterEach(context: ExtensionContext) {
        val instance: Any = context.requiredTestInstance
        val type: KClass<*> = context.requiredTestClass.kotlin

        val mocks = type.memberProperties
            .filterIsInstance<KProperty1<Any, Any?>>()
            .mapNotNull { property -> getValue(logger, property, instance) }
            .filter { isMockKMock(it) }
            .toTypedArray()

        checkUnnecessaryStub(*mocks)
    }

    companion object {
        fun <T, V> getValue(logger: Logger, property: KProperty1<T, V?>, instance: T): V? {
            try {
                property.isAccessible = true
                return property.get(instance)
            } catch (e: InvocationTargetException) {
                if (e.cause is UninitializedPropertyAccessException) {
                    return null
                } else {
                    logger.warn { "Retrieving the value of the property `$property` threw an exception: ${e.message}${System.lineSeparator()}${e.stackTraceToString()}" }
                    return null
                }
            } catch (e: Exception) {
                logger.warn { "Retrieving the value of the property `$property` threw an exception: ${e.message}${System.lineSeparator()}${e.stackTraceToString()}" }
                return null
            }
        }
    }
}
