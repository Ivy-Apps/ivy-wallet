package com.ivy.wallet.buildsrc

import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.implementation(dependency: Any) {
    this.add("implementation", dependency)
}

internal fun DependencyHandler.implementation(value: String) {
    this.implementation(dependency = value)
}

internal fun DependencyHandler.api(dependency: Any) {
    this.add("api", dependency)
}

internal fun DependencyHandler.api(value: String) {
    this.api(dependency = value)
}

internal fun DependencyHandler.kapt(dependency: Any) {
    this.add("kapt", dependency)
}

internal fun DependencyHandler.kapt(value: String) {
    this.kapt(dependency = value)
}

internal fun DependencyHandler.testImplementation(value: String) {
    this.add("testImplementation", value)
}

internal fun DependencyHandler.androidTestImplementation(value: String) {
    this.add("androidTestImplementation", value)
}

internal fun DependencyHandler.kaptAndroidTest(value: String) {
    this.add("kaptAndroidTest", value)
}

