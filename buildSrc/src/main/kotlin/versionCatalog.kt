@file:Suppress("Filename")

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

// Make version catalog available in precompiled scripts
// https://github.com/gradle/gradle/issues/15383#issuecomment-1567461389
val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()
