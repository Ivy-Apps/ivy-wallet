
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        val deps = com.ivy.wallet.buildsrc.allDeps()
            .filter { it.type == com.ivy.wallet.buildsrc.DependencyType.CLASSPATH }

        for (dep in deps) {
            classpath(dep.value)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}