package com.ivy.buildsrc

import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.dependency(dependency: Any, api: Boolean) {
    if (api) api(dependency) else implementation(dependency)
}

internal fun DependencyHandler.debugDependency(dependency: Any, api: Boolean) {
    if (api) debugApi(dependency) else debugImplementation(dependency)
}

internal fun DependencyHandler.testDependency(dependency: Any, api: Boolean) {
    if (api) testApi(dependency) else testImplementation(dependency)
}

internal fun DependencyHandler.androidTestDependency(dependency: Any, api: Boolean) {
    if (api) androidTestApi(dependency) else androidTestImplementation(dependency)
}

// ----------------------------------------------------------------------------------

internal fun DependencyHandler.implementation(dependency: Any) {
    this.add("implementation", dependency)
}

internal fun DependencyHandler.debugImplementation(dependency: Any) {
    this.add("debugImplementation", dependency)
}

internal fun DependencyHandler.implementation(value: String) {
    this.implementation(dependency = value)
}

internal fun DependencyHandler.api(dependency: Any) {
    this.add("api", dependency)
}

internal fun DependencyHandler.debugApi(dependency: Any) {
    this.add("debugApi", dependency)
}

internal fun DependencyHandler.api(value: String) {
    this.api(dependency = value)
}

internal fun DependencyHandler.kapt(dependency: Any) {
    this.add("kapt", dependency)
}

fun DependencyHandler.kapt(value: String) {
    this.kapt(dependency = value)
}

fun DependencyHandler.ksp(value: String) {
    this.ksp(dependency = value)
}

internal fun DependencyHandler.ksp(dependency: Any) {
    this.add("ksp", dependency)
}

internal fun DependencyHandler.testImplementation(value: Any) {
    this.add("testImplementation", value)
}

internal fun DependencyHandler.testApi(value: Any) {
    this.add("testApi", value)
}

internal fun DependencyHandler.androidTestImplementation(value: Any) {
    this.add("androidTestImplementation", value)
}

internal fun DependencyHandler.androidTestApi(value: Any) {
    this.add("androidTestApi", value)
}

internal fun DependencyHandler.kaptAndroidTest(value: Any) {
    this.add("kaptAndroidTest", value)
}

