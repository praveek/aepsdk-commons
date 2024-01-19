package com.adobe.marketing.mobile.gradle

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.JavadocMemberLevel
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AEPLibraryPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.createAepLibraryConfiguration()

        project.plugins.apply("com.android.library")
        project.plugins.apply("kotlin-android")
        project.plugins.apply(JacocoPlugin::class.java)
        project.plugins.apply(PublishPlugin::class.java)

        configureAndroidLibrary(project, extension)
        configureKotlin(project)

        project.afterEvaluate {
            if (extension.enableDokkaDoc.getOrElse(false)) {
                project.plugins.apply("org.jetbrains.dokka")
                configureDokkaDoc(project)
            } else {
                configureJavaDoc(project)
            }

            if (extension.enableSpotless.getOrElse(false)) {
                project.plugins.apply("com.diffplug.spotless")
                configureSpotless(project, extension)
            }

            if (extension.enableCheckStyle.getOrElse(false)) {
                project.plugins.apply("checkstyle")
                configureCheckStyle(project)
            }

            if (!extension.disableCommonDependencies.getOrElse(false)) {
                configureCommonDependencies(project, extension)
            }

            configureTaskDependencies(project)
        }
    }

    private fun configureAndroidLibrary(project: Project, extension: AEPLibraryExtension) {
        val android = project.extensions.getByType(LibraryExtension::class.java)
        android.apply {
            compileSdk = BuildConstants.ProjectConfig.COMPILE_SDK_VERSION
            defaultConfig {
                minSdk = BuildConstants.ProjectConfig.MIN_SDK_VERSION
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
            }
            buildFeatures.buildConfig = true
            flavorDimensionList.add(BuildConstants.BuildDimensions.TARGET)
            productFlavors {
                create(BuildConstants.ProductFlavors.PHONE) {
                    dimension = BuildConstants.BuildDimensions.TARGET
                }
            }
            buildTypes {
                getByName(BuildConstants.BuildTypes.DEBUG) {
                    enableAndroidTestCoverage = true
                    enableUnitTestCoverage = true
                }

                getByName(BuildConstants.BuildTypes.RELEASE) {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
                }
            }
            publishing {
                singleVariant(BuildConstants.BuildTypes.RELEASE) {
                    withSourcesJar()
                    withJavadocJar()
                }
            }
            testOptions {
                unitTests.isReturnDefaultValues = true
            }
            compileOptions {
                sourceCompatibility = BuildConstants.ProjectConfig.JAVA_SOURCE_COMPATIBILITY
                targetCompatibility = BuildConstants.ProjectConfig.JAVA_TARGET_COMPATIBILITY
            }

            libraryVariants.configureEach {
                project.tasks.withType(Javadoc::class.java).configureEach {
                    val mainSourceSet = android.sourceSets.getByName(BuildConstants.SourceSets.MAIN)
                    val phoneSourceSet = android.sourceSets.getByName(BuildConstants.SourceSets.PHONE)
                    val mainSourceDirs = mainSourceSet.java.srcDirs.first()
                    val phoneSourceDirs = phoneSourceSet.java.srcDirs.first()

                    source = project.fileTree(mainSourceDirs) + project.fileTree(phoneSourceDirs)

                    doFirst {
                        classpath =
                            project.files(javaCompileProvider.get().classpath.files) + project.files(project.androidJarPath)
                    }

                    exclude(BuildConstants.Reporting.BUILD_CONFIG_CLASS, BuildConstants.Reporting.R_CLASS)
                    options {
                        memberLevel = JavadocMemberLevel.PUBLIC
                    }
                }
            }
        }
    }

    private fun configureKotlin(project: Project) {
        // Don't have access to android.kotlinOptions inside the plugin.
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions {
                jvmTarget = BuildConstants.ProjectConfig.KOTLIN_JVM_TARGET
                languageVersion = BuildConstants.ProjectConfig.KOTLIN_LANGUAGE_VERSION
                apiVersion = BuildConstants.ProjectConfig.KOTLIN_API_VERSION
            }
        }
    }

    private fun configureJavaDoc(project: Project) {
        project.tasks.register<Javadoc>("javadoc") {
            options.memberLevel = JavadocMemberLevel.PUBLIC
        }

        // This task generates a JAR file containing Javadoc documentation to be bundled with the AAR.
        project.javadocJar.configure {
            from(project.tasks.named("javadoc"))
            archiveClassifier.set("javadoc")
        }
    }

    private fun configureDokkaDoc(project: Project) {
        val dokkaJavadoc = project.tasks.named<DokkaTask>("dokkaJavadoc")
        dokkaJavadoc.configure {
            dokkaSourceSets.named("main") {
                noAndroidSdkLink.set(false)
                perPackageOption {
                    matchingRegex.set(".*\\.internal.*") // proper setting
                    suppress.set(true)
                }
            }

            dokkaSourceSets.named("phone") {
                noAndroidSdkLink.set(false)
                perPackageOption {
                    matchingRegex.set(".*\\.internal.*") // proper setting
                    suppress.set(true)
                }
            }
        }

        // This task generates a JAR file containing Javadoc documentation to be bundled with the AAR.
        project.javadocJar.configure {
            dependsOn(dokkaJavadoc)
            archiveClassifier.set("javadoc")
            from(dokkaJavadoc.get().outputDirectory)
        }
    }

    private fun configureCommonDependencies(project: Project, extension: AEPLibraryExtension) {
        project.dependencies {
            add("implementation", "androidx.appcompat:appcompat:1.4.2")
            add("testImplementation", "junit:junit:4.13.2")
            add("testImplementation", "org.mockito:mockito-core:4.5.1")
            add("testImplementation", "org.mockito:mockito-inline:4.5.1")
            add("testImplementation", "org.json:json:20180813")
            add("androidTestImplementation", "androidx.test.ext:junit:1.1.3")
            add("androidTestImplementation", "androidx.test.espresso:espresso-core:3.4.0")
        }
    }

    private fun configureTaskDependencies(project: Project) {
        val assemblePhone = project.tasks.named("assemblePhone")
        project.tasks.named("publish").configure { dependsOn(assemblePhone) }
        project.tasks.named("publishToMavenLocal").configure { dependsOn(assemblePhone)}
        project.tasks.named("publishReleasePublicationToMavenLocal").configure { dependsOn(assemblePhone)}
        project.tasks.named("signReleasePublication").configure { mustRunAfter(assemblePhone)}
    }

    private fun configureSpotless(project: Project, extension: AEPLibraryExtension) {
        project.extensions.configure<SpotlessExtension> {
            java {
                toggleOffOn("format:off", "format:on")
                target(BuildConstants.Formatting.JAVA_TARGETS)
                if (extension.enableSpotlessPrettierForJava.getOrElse(false)) {
                    googleJavaFormat(BuildConstants.Formatting.GOOGLE_JAVA_FORMAT_VERSION).aosp().reflowLongStrings()
                } else {
                    prettier(
                            mapOf(
                                    "prettier" to BuildConstants.Formatting.PRETTIER_VERSION,
                                    "prettier-plugin-java" to BuildConstants.Formatting.PRETTIER_JAVA_PLUGIN_VERSION
                            )
                    ).config(BuildConstants.Formatting.PRETTIER_CONFIG)
                }

                importOrder()
                removeUnusedImports()
                endWithNewline()
                formatAnnotations()
                licenseHeader(BuildConstants.Formatting.LICENSE_HEADER)
            }
            kotlin {
                target(BuildConstants.Formatting.KOTLIN_TARGETS)
                ktlint(BuildConstants.Formatting.KTLINT_VERSION)
                endWithNewline()
                licenseHeader(BuildConstants.Formatting.LICENSE_HEADER)
            }
        }
    }

    private fun configureCheckStyle(project: Project) {
        project.extensions.configure<CheckstyleExtension> {
            config = project.resources.text.fromString(BuildConstants.CheckStyle.CONFIG)
            isIgnoreFailures = false
            isShowViolations = true
            toolVersion = BuildConstants.CheckStyle.TOOLS_VERSION
        }

        /** Checkstyle task for new files (not in exclude list). Fail build if a check fails **/
        project.tasks.register<Checkstyle>("checkstyle") {
            source("src")
            include("**/*.java")
            exclude("**/gen/**")
            exclude("**/test/**")
            exclude("**/legacy/**")
            exclude("**/androidTest/**")
            exclude("**/R.java")
            exclude("**/BuildConfig.java")
            classpath = project.files()
        }
    }



    /**
     * The android jar path based on the compile sdk version
     */
    private val Project.androidJarPath: String
        get() {
            val android = project.extensions.getByType(BaseExtension::class.java)
            return "${android.sdkDirectory}/platforms/android-${BuildConstants.ProjectConfig.COMPILE_SDK_VERSION}/android.jar"
        }
}
