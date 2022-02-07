package com.ivy.wallet.buildsrc

fun allDeps(): List<WrappedDependency> {
    val scope = DependencyScope()
    with(scope) {
        dependencies()
    }

    return scope.deps
}

class DependencyGroup(
    val name: String,
    val version: String? = null,
    deps: MutableList<WrappedDependency>
) : DependencyScope(deps)

open class DependencyScope(
    val deps: MutableList<WrappedDependency> = mutableListOf()
)

fun DependencyScope.group(
    name: String,
    version: String? = null,
    deps: DependencyGroup.() -> Unit
) {
    with(
        DependencyGroup(
            name = name,
            version = version,
            deps = this.deps
        )
    ) {
        deps()
    }
}

fun DependencyScope.dependency(
    type: () -> DependencyType,
    value: String
) {
    deps += WrappedDependency(
        type = type(),
        value = value
    )
}

fun classpath() = DependencyType.CLASSPATH
fun implementation() = DependencyType.IMPLEMENTATION
fun kapt() = DependencyType.KAPT
fun testImplementation() = DependencyType.TEST_IMPLEMENTATION
fun androidTestImplementation() = DependencyType.ANDROID_TEST_IMPLEMENTATION
fun kaptAndroidTest() = DependencyType.KAPT_ANDROID_TEST
fun plugin() = DependencyType.PLUGIN_ID

data class WrappedDependency(
    val type: DependencyType,
    val value: String
)

enum class DependencyType {
    CLASSPATH,
    IMPLEMENTATION,
    KAPT,
    TEST_IMPLEMENTATION,
    ANDROID_TEST_IMPLEMENTATION,
    KAPT_ANDROID_TEST,
    PLUGIN_ID
}