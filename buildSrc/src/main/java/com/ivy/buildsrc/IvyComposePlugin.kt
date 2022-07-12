package com.ivy.buildsrc

import org.gradle.api.Project

abstract class IvyComposePlugin : IvyPlugin() {

    override fun apply(project: Project) {
        super.apply(project)

        val library = project.androidLibrary()
        library.composeOptions {
            kotlinCompilerExtensionVersion = Versions.composeCompilerVersion
        }
        library.buildFeatures {
            compose = true
        }

    }
}