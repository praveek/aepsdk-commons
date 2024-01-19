package com.adobe.marketing.mobile.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

/**
 * Creates the AEP Library configuration extension for the project.
 */
internal fun Project.createAepLibraryConfiguration(): AEPLibraryExtension {
    val aepConfig = extensions.create<AEPLibraryExtension>("aepLibrary")

    aepConfig.compose.convention(false)
    // These properties are typically set in gradle.properties with build scripts relying on them.
    aepConfig.moduleName.convention(providers.gradleProperty("moduleName"))
    aepConfig.moduleVersion.convention(providers.gradleProperty("moduleVersion"))
    aepConfig.publishing.mavenRepoName.convention(providers.gradleProperty("mavenRepoName"))
    aepConfig.publishing.mavenRepoDescription.convention(providers.gradleProperty("mavenRepoDescription"))

    return aepConfig
}

/**
 * Retrieves the AEP Library configuration extension for the project.
 */
internal val Project.aepLibraryConfiguration: AEPLibraryExtension
    get() {
        return extensions["aepLibrary"] as AEPLibraryExtension
    }

/**
 * Retrieves the Javadoc Jar task for the project.
 */
internal val Project.javadocJar: TaskProvider<Jar>
    get() {
        return if (tasks.findByName("javadocJar") != null) {
            tasks.named<Jar>("javadocJar")
        } else {
            tasks.register<Jar>("javadocJar")
        }
    }

/**
 * Retrieves the name of the module's AAR.
 */
internal val Project.moduleAARName: String
    get() {
        return "${aepLibraryConfiguration.moduleName.get()}-phone-release.aar"
    }

/**
 * Retrieves the path to the module's AAR.
 */
internal val Project.moduleAARPath: String
    get() {
        return "${buildDir}/outputs/aar/$moduleAARName"
    }

// Publishing related project extensions.

/**
 * Retrieves the publishing configuration for the project.
 */
internal val Project.publishConfig: PublishConfig
    get() {
        return aepLibraryConfiguration.publishing
    }

/**
 * Verifies if the current build is a JitPack build.
 */
internal fun Project.isJitPackBuild(): Boolean = hasProperty(BuildConstants.Publishing.JITPACK_PROPERTY)

/**
 * Verifies if the current build is a release build.
 */
internal fun Project.isReleaseBuild(): Boolean = hasProperty(BuildConstants.Publishing.RELEASE_PROPERTY)

/**
 * Verifies if the current build is a snapshot build.
 */
internal fun Project.isSnapshotBuild(): Boolean = !isReleaseBuild()

/**
 * Returns the publish URL based on the build type.
 */
internal val Project.publishUrl: String
    get() {
        return if (isSnapshotBuild()) {
            BuildConstants.Publishing.SNAPSHOTS_URL
        } else {
            BuildConstants.Publishing.RELEASES_URL
        }
    }

/**
 * Retrieves the publish group id for the build.
 */
internal val Project.publishGroupId: String
    get() {
        return if (isJitPackBuild()) {
            "com.github.adobe.${publishConfig.gitRepoName.get()}"
        } else {
            BuildConstants.Publishing.ADOBE_GROUP_ID
        }
    }

/**
 * Retrieves the publish artifact id for the build.
 */
internal val Project.publishArtifactId: String
    get() {
        return aepLibraryConfiguration.moduleName.get()
    }

/**
 * Retrieves the publish version name for the build.
 */
internal val Project.publishVersion: String
    get() {
        val moduleVersion = aepLibraryConfiguration.moduleVersion.get()
        return if (isReleaseBuild()) {
            moduleVersion
        } else {
            "$moduleVersion-${BuildConstants.Publishing.SNAPSHOT_SUFFIX}"
        }
    }
