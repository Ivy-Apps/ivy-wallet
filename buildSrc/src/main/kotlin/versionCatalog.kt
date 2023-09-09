@file:Suppress("Filename")

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.the

// Make version catalog available in precompiled scripts
// https://github.com/gradle/gradle/issues/15383#issuecomment-1567461389
val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal val Project.catalog: VersionCatalog
    get() =
        project.extensions.getByType<VersionCatalogsExtension>().named("libs")

fun VersionCatalog.version(alias: String): String =
    this.findVersion(alias).get().requiredVersion

fun VersionCatalog.bundle(alias: String): Any =
    this.findBundle(alias).get()

fun VersionCatalog.library(alias: String): Any =
    this.findLibrary(alias).get()
