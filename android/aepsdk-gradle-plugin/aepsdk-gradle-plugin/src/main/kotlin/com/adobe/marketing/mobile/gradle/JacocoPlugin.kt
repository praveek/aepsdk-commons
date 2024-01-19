package com.adobe.marketing.mobile.gradle

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport


internal class JacocoPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Apply the Jacoco plugin to the project
        project.plugins.apply(JacocoPlugin::class.java)

        // Register a task for unit test coverage report
        project.tasks.register("unitTestCoverageReport", JacocoReport::class.java) {
            dependsOn("testPhoneDebugUnitTest")

            // Configure report formats and requirements
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }

            // Add source sets for coverage analysis
            addSourceSets()

            // Set the execution data for unit tests
            executionData.setFrom(
                project.fileTree(
                    mapOf(
                        "dir" to "${project.buildDir}",
                        "includes" to listOf(BuildConstants.Reporting.UNIT_TEST_EXECUTION_RESULTS_REGEX)
                    )
                )
            )
        }

        // Register a task for functional test coverage report
        project.tasks.register("functionalTestsCoverageReport", JacocoReport::class.java) {
            dependsOn("createPhoneDebugCoverageReport")

            // Configure report formats and requirements
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }

            // Add source sets for coverage analysis
            addSourceSets()

            // Set the execution data for functional tests
            executionData.setFrom(
                project.fileTree(
                    mapOf(
                        "dir" to "${project.buildDir}",
                        "includes" to listOf(BuildConstants.Reporting.FUNCTIONAL_TEST_EXECUTION_RESULTS_REGEX)
                    )
                )
            )
        }
    }
}

/**
 * Extension to add the sources to the Jacoco report
 */
private fun JacocoReport.addSourceSets() {
    val android = project.extensions.getByType(LibraryExtension::class.java)

    // Configure source sets for the main code
    val mainSourceSet = android.sourceSets.getByName(BuildConstants.SourceSets.MAIN)
    val mainSourceDir = mainSourceSet.java.srcDirs
    sourceDirectories.setFrom(project.files(mainSourceDir))

    // Configure additional class directories for coverage analysis
    val debugTree = project.fileTree(
        mapOf(
            "dir" to "${project.buildDir}/intermediates/javac/phoneDebug/classes/com/adobe/marketing/mobile",
            "excludes" to listOf(BuildConstants.Reporting.ADB_CLASS, BuildConstants.Reporting.BUILD_CONFIG_CLASS)
        )
    )
    additionalClassDirs.setFrom(project.files(debugTree))

    // Configure source sets for the phone code
    val phoneSourceSet = android.sourceSets.getByName(BuildConstants.SourceSets.PHONE)
    val phoneSourceDir = phoneSourceSet.java.srcDirs
    additionalSourceDirs.setFrom(project.files(phoneSourceDir))
}
