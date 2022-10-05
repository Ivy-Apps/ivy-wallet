package com.ivy.buildsrc

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class IvyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        applyPlugins(project)
        addKotlinCompilerArgs(project)
        setProjectSdkVersions(project)

        kotest(project)
        // Robolectric doesn't integrate well with JUnit5 and Kotest
//        robolectric(project)
        androidTest(project)
        lint(project)
    }

    private fun kotest(project: Project) {
        val library = project.androidLibrary()
        library.testOptions {
            unitTests.all {
                it.useJUnitPlatform()
            }
        }
    }

//    private fun robolectric(project: Project) {
//        project.androidLibrary().testOptions {
//            unitTests.isIncludeAndroidResources = true
//        }
//    }

    private fun androidTest(project: Project) {
        project.androidLibrary().defaultConfig {
            testInstrumentationRunner = "com.ivy.common.androidtest.IvyTestRunner"
        }
    }

    /**
     * Global lint configuration
     */
    private fun lint(project: Project) {
        project.androidLibrary().lint {
            disable.add("MissingTranslation")
        }
    }

    private fun applyPlugins(project: Project) {
        project.apply {
            plugin("android-library")
            plugin("kotlin-android")
            plugin("kotlin-kapt")
            plugin("dagger.hilt.android.plugin")
            plugin("io.kotest")

            //TODO: Enable if we migrate to kotlinx serialization
//            plugin("kotlinx-serialization")
        }
    }

    private fun addKotlinCompilerArgs(project: Project) {
        project.allprojects {
            allprojects {
                tasks.withType(KotlinCompile::class).all {
                    with(kotlinOptions) {
                        freeCompilerArgs = freeCompilerArgs + listOf("-Xcontext-receivers")
                        //Suppress Jetpack Compose versions compiler incompatibility, do NOT do it
//                        freeCompilerArgs = freeCompilerArgs + listOf(
//                            "-P",
//                            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
//                        )
                        freeCompilerArgs = freeCompilerArgs + listOf("-Xskip-prerelease-check")
                    }
                }
            }
        }
    }

    private fun setProjectSdkVersions(project: Project) {
        val library = project.androidLibrary()
        library.compileSdk = com.ivy.buildsrc.Project.compileSdkVersion
        library.defaultConfig {
            minSdk = com.ivy.buildsrc.Project.minSdk
            targetSdk = com.ivy.buildsrc.Project.targetSdk
        }
    }

    protected fun Project.androidLibrary(): LibraryExtension =
        extensions.getByType(LibraryExtension::class.java)
}