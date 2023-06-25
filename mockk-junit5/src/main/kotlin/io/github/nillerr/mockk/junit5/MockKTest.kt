package io.github.nillerr.mockk.junit5

import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExtendWith(MockKExtension::class)
annotation class MockKTest
