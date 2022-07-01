package com.ivy.buildsrc

import org.gradle.api.Plugin
import org.gradle.api.Project

class IvyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
//        val missingConfigs = listOf(
//            "androidTestApi",
//            "androidTestDebugApi",
//            "androidTestReleaseApi",
//            "testApi",
//            "testDebugApi",
//            "testReleaseApi",
//        )
//        missingConfigs.forEach(project.configurations::create)
    }
}