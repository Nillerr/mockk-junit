import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java-library")

    kotlin("jvm")

    id("org.jetbrains.dokka")
    id("org.jetbrains.dokka-javadoc")
    id("com.vanniktech.maven.publish") apply false
}

allprojects {
    group = "io.github.nillerr"
    version = "1.2.0"

    repositories {
        mavenCentral()
    }

    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.dokka-javadoc")
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")

    dependencies {
        testImplementation(kotlin("test"))
    }

    tasks {
        test {
            useJUnitPlatform()
        }

        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    configure<MavenPublishBaseExtension> {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        signAllPublications()

        pom {
            name.set("MockK for JUnit")
            url.set("https://github.com/Nillerr/mockk-junit")
            description.set("MockK for JUnit")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://github.com/Nillerr/mockk-junit/LICENSE")
                }
            }

            developers {
                developer {
                    id.set("Nillerr")
                    name.set("Nicklas Jensen")
                    url.set("https://github.com/Nillerr")
                }
            }

            scm {
                url.set("https://github.com/Nillerr/mockk-junit")
            }
        }
    }
}
