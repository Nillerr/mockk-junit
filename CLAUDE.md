# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

MockK for JUnit 5 — a Kotlin library providing `@MockKTest` annotation that automatically runs `checkUnnecessaryStub()` on all MockK mock instances in test class member properties after each test.

Published as `io.github.nillerr:mockk-junit5` to Maven Central.

## Build Commands

```bash
./gradlew build          # Build the project
./gradlew test           # Run all tests
./gradlew :mockk-junit5:test --tests "io.github.nillerr.mockk.junit5.MockKExtensionTests.testName"  # Run a single test
./gradlew publishAndReleaseToMavenCentral   # Publish release to Maven Central
```

Publishing requires `mavenCentralUsername`, `mavenCentralPassword`, and PGP signing properties (`signingInMemoryKeyId`, `signingInMemoryKey`, `signingInMemoryKeyPassword`) in `~/.gradle/gradle.properties`.

## Architecture

This is a single-module Gradle project (`mockk-junit5/`) with two source files:

- **MockKTest.kt** — Marker annotation with `@ExtendWith(MockKExtension::class)` that users apply to test classes.
- **MockKExtension.kt** — JUnit 5 `AfterEachCallback` that uses Kotlin reflection to scan all member properties of the test instance, identifies MockK mocks via `isMockKMock()`, and calls `checkUnnecessaryStub()` on them. Handles uninitialized late-init properties and inaccessible properties gracefully.

Tests in `MockKExtensionTests.kt` use JUnit Platform Test Kit (`EngineTestKit`) to run embedded test classes and verify the extension behavior end-to-end.

## Build Configuration

- Kotlin 2.1.20, JVM target 21
- MockK 1.14.9, JUnit 5.9.3
- Dokka 2.1.0 for documentation generation
- Publishing via Vanniktech Maven Publish Plugin to Maven Central
