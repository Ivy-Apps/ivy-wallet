package com.ivy.buildsrc

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class IvyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.apply {
            plugin("android-library")
            plugin("kotlin-android")
            plugin("kotlin-kapt")
        }

        val library = project.extensions.getByType(LibraryExtension::class.java)
        library.compileSdk = com.ivy.buildsrc.Project.compileSdkVersion
        library.defaultConfig {
            minSdk = com.ivy.buildsrc.Project.minSdk
            targetSdk = com.ivy.buildsrc.Project.targetSdk
        }

    }
}