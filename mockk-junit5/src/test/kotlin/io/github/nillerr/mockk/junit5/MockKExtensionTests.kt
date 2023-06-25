package io.github.nillerr.mockk.junit5

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import kotlin.reflect.full.memberProperties

class MockKExtensionTests {
    private val logger = LoggerFactory.getLogger(MockKExtensionTests::class.java)

    @Test
    fun `with unnecessary stubs throws AssertionError`() {
        // When
        val failure = EngineTestKit
            .engine("junit-jupiter")
            .configurationParameter("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition")
            .selectors(DiscoverySelectors.selectClass(WithUnnecessaryStubs::class.java))
            .execute()
            .testEvents()
            .failed()
            .stream()
            .toList()
            .single()

        // Then
        assertThat(failure.payload.get()).isInstanceOfSatisfying(TestExecutionResult::class.java) { result ->
            assertThat(result.status).isEqualTo(TestExecutionResult.Status.FAILED)
            assertThat(result.throwable.get()).isInstanceOfSatisfying(AssertionError::class.java) { cause ->
                assertThat(cause).hasMessageStartingWith("Unnecessary stubbings detected.")
            }
        }
    }

    @Test
    fun `when everything is okay then test is successful`() {
        // When
        val successes = EngineTestKit
            .engine("junit-jupiter")
            .configurationParameter("junit.jupiter.conditions.deactivate", "org.junit.*DisabledCondition")
            .selectors(DiscoverySelectors.selectClass(AllGood::class.java))
            .execute()
            .testEvents()
            .succeeded()
            .stream()
            .toList()

        // Then
        assertThat(successes.size).isEqualTo(1)
    }

    @Test
    fun `getValue returns value of public property`() {
        // Given
        val publicValue = "pub"
        val privateValue = "pri"
        val instance = DemoObject(publicValue, privateValue)

        val property = DemoObject::publicValue

        // When
        val result = MockKExtension.getValue(logger, property, instance)

        // Then
        assertThat(result).isSameAs(publicValue)
    }

    @Test
    fun `getValue returns value of private property`() {
        // Given
        val publicValue = "pub"
        val privateValue = "pri"
        val instance = DemoObject(publicValue, privateValue)

        val property = DemoObject.privateValueProperty

        // When
        val result = MockKExtension.getValue(logger, property, instance)

        // Then
        assertThat(result).isSameAs(privateValue)
    }

    @Test
    fun `getValue returns null for late-init property through java`() {
        // Given
        val publicValue = "pub"
        val privateValue = "pri"
        val instance = DemoObject(publicValue, privateValue)

        val property = DemoObject::class.java.kotlin.memberProperties.first { it.isLateinit }

        // When
        val result = MockKExtension.getValue(logger, property, instance)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getValue returns null for throwing property`() {
        // Given
        val publicValue = "pub"
        val privateValue = "pri"
        val instance = DemoObject(publicValue, privateValue)

        val property = DemoObject::throwingProperty

        // When
        val result = MockKExtension.getValue(logger, property, instance)

        // Then
        assertThat(result).isNull()
    }

    @Test
    fun `getValue returns null for throwing property through java`() {
        // Given
        val publicValue = "pub"
        val privateValue = "pri"
        val instance = DemoObject(publicValue, privateValue)

        val property = DemoObject::class.java.kotlin.memberProperties.first { it.name == "throwingProperty" }

        // When
        val result = MockKExtension.getValue(logger, property, instance)

        // Then
        assertThat(result).isNull()
    }

    class DemoObject(
        val publicValue: String,
        private val privateValue: String,
    ) {
        lateinit var lateInitValue: String

        val throwingProperty: String
            get() = error("Thrown!")

        companion object {
            val privateValueProperty = DemoObject::privateValue
        }
    }

    @Disabled
    @MockKTest
    class WithUnnecessaryStubs {
        val mockProperty1 = mockk<AutoCloseable>()
        val mockProperty2 = mockk<AutoCloseable> {
            every { close() }.returnsMany(Unit)
        }

        @Test
        fun test() {
            // Nothing
        }
    }

    @Disabled
    @MockKTest
    class AllGood {
        val mockProperty1 = mockk<AutoCloseable>()
        val mockProperty2 = mockk<AutoCloseable>()

        lateinit var lateInitProperty: String

        var nullableProperty: String? = null

        @Test
        fun test() {
            // Nothing
        }
    }
}
