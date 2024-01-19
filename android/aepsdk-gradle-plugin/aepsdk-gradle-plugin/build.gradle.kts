
object Plugins {
    const val ANDROID_GRADLE_PLUGIN_VERSION = "8.2.0"
    const val KOTLIN_GRADLE_PLUGIN_VERSION = "1.7.0"
    const val SPOTLESS_GRADLE_PLUGIN_VERSION = "6.11.0"
    const val DOKKA_GRADLE_PLUGIN_VERSION = "1.7.10"
}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    signing

    // Apply the Kotlin JVM aepsdk-gradle-plugin to add support for Kotlin.
    // alias(libs.plugins.jvm)
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${Plugins.KOTLIN_GRADLE_PLUGIN_VERSION}")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${Plugins.SPOTLESS_GRADLE_PLUGIN_VERSION}")
    implementation("com.android.tools.build:gradle:${Plugins.ANDROID_GRADLE_PLUGIN_VERSION}")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:${Plugins.DOKKA_GRADLE_PLUGIN_VERSION}")
}

gradlePlugin {
    plugins {
        register("AEPLibraryPlugin") {
            id = "aep-library"
            implementationClass = "com.adobe.marketing.mobile.gradle.AEPLibraryPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginJitpack") {
            groupId = "com.github.adobe.aepsdk-commons"
            artifactId = "aepsdk-gradle-plugin"
            version = "0.0.1"
            from(components["java"])
        }
    }
}