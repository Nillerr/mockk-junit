rootProject.name = "mockk-junit"

pluginManagement {
    repositories {
        mavenCentral()
    }

    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        kotlin("jvm") version kotlinVersion apply false
    }
}

include(":mockk-junit5")
