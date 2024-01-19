package com.adobe.marketing.mobile.gradle

import org.gradle.api.JavaVersion

/**
 * A collection of constants used throughout the build scripts in this project.
 */
object BuildConstants {
    object ProjectConfig {
        const val MIN_SDK_VERSION = 21
        const val COMPILE_SDK_VERSION = 34
        const val TARGET_SDK_VERSION = 34
        const val VERSION_CODE = 1
        const val VERSION_NAME = "1.0"

        val JAVA_SOURCE_COMPATIBILITY: JavaVersion = JavaVersion.VERSION_1_8
        val JAVA_TARGET_COMPATIBILITY: JavaVersion = JavaVersion.VERSION_1_8

        const val KOTLIN_VERSION = "1.7.0"
        const val COMPOSE_VERSION = "1.2.0"
        const val KOTLIN_LANGUAGE_VERSION = "1.5"
        const val KOTLIN_API_VERSION = "1.5"
        const val KOTLIN_JVM_TARGET = "1.8"
    }

    object Formatting {
        const val KTLINT_VERSION = "0.42.1"
        const val GOOGLE_JAVA_FORMAT_VERSION = "1.15.0"
        const val JAVA_TARGETS = "src/*/java/**/*.java"
        const val KOTLIN_TARGETS = "src/*/java/**/*.kt"
        val PRETTIER_VERSION = "2.7.1"
        val PRETTIER_JAVA_PLUGIN_VERSION = "1.6.2"
        val PRETTIER_CONFIG = mapOf(
                "parser" to "java",
                "tabWidth" to 4,
                "useTabs" to true,
                "printWidth" to 120
        )
        const val LICENSE_HEADER = """/*
  Copyright ${'$'}YEAR Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/"""
    }

    object CheckStyle {
        const val TOOLS_VERSION = "8.36.1"
        const val CONFIG = """<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
		"-//Puppy Crawl//DTD Check Configuration 1.2//EN"
		"http://www.puppycrawl.com/dtds/configuration_1_2.dtd">

<module name="Checker">	
 	<module name="SuppressWarningsFilter" />
	<module name="TreeWalker">
		<module name="SuppressWarningsHolder" />
		<module name="FinalParameters"/>
		<module name="BooleanExpressionComplexity"/>
		<module name="EqualsAvoidNull"/>
		<module name="FallThrough"/>
		<module name="NestedForDepth"/>
		<module name="NestedIfDepth"/>
		<module name="NestedTryDepth"/>
		<module name="MagicNumber"/>
		<module name="AvoidStaticImport"/>
		<module name="IllegalImport"></module> 
    	<module name="RedundantImport"></module> 
    	<module name="UnusedImports"></module> 
	</module>
</module>"""
    }

    object Publishing {
        const val SNAPSHOTS_URL = "https://oss.sonatype.org/content/repositories/snapshots/"
        const val RELEASES_URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

        const val RELEASE_PROPERTY = "release"
        const val JITPACK_PROPERTY = "jitpack"
        const val SNAPSHOT_SUFFIX = "SNAPSHOT"

        const val LICENSE_NAME = "The Apache License, Version 2.0"
        const val LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0.txt"
        const val LICENSE_DIST = "repo"

        const val DEVELOPER_ID = "adobe"
        const val DEVELOPER_NAME = "adobe"
        const val DEVELOPER_EMAIL = "adobe-mobile-testing@adobe.com"
        const val DEVELOPER_DOC_URL = "https://developer.adobe.com/client-sdks"

        const val SCM_CONNECTION_URL_TEMPLATE = "scm:git:github.com//adobe/%s.git"
        const val SCM_REPO_URL_TEMPLATE = "https://github.com/adobe/%s"

        const val ADOBE_GROUP_ID = "com.adobe.marketing.mobile"


        const val SIGNING_GNUPG_EXECUTABLE = "gpg"
        val SIGNING_GNUPG_KEY_NAME by lazy { System.getenv("GPG_KEY_ID") }
        val SIGNING_GNUPG_PASSPHRASE by lazy { System.getenv("GPG_PASSPHRASE") }
    }

    object Reporting {
        const val UNIT_TEST_EXECUTION_RESULTS_REGEX =
            "outputs/unit_test_code_coverage/phoneDebugUnitTest/*.exec"
        const val FUNCTIONAL_TEST_EXECUTION_RESULTS_REGEX =
            "outputs/code_coverage/phoneDebugAndroidTest/connected/*coverage.ec"
        const val BUILD_CONFIG_CLASS = "**/BuildConfig.java"
        const val R_CLASS = "**/R.java"
        const val ADB_CLASS = "**/ADB*.class"
    }

    object BuildTypes {
        const val RELEASE = "release"
        const val DEBUG = "debug"
    }

    object SourceSets {
        const val MAIN = "main"
        const val PHONE = "phone"
    }

    object ProductFlavors {
        const val PHONE = "phone"
    }

    object BuildDimensions {
        const val TARGET = "target"
    }
}