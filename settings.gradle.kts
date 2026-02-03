rootProject.name = "mockk-junit"

pluginManagement {
    repositories {
        mavenCentral()
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val dokkaVersion = extra["dokka.version"] as String
        kotlin("jvm") version kotlinVersion apply false
        id("org.jetbrains.dokka") version dokkaVersion apply false
    }
}

include(":mockk-junit5")
