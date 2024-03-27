import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

configure<DetektExtension> {
    source.setFrom(projectDir)
    config.setFrom("$rootDir/config/detekt/config.yml")
    baseline = file("$rootDir/config/detekt/baseline.yml")
}

tasks.register<Detekt>("detektFormat") {
    autoCorrect = true
}

tasks.withType<Detekt> {
    // Disable task caching
    outputs.upToDateWhen { false }

    val filesToCheck: List<String>? = System.getProperty("detekt.filesToCheck")
        ?.split(",")
        ?.filter { it.isNotBlank() }
    setSource(filesToCheck ?: projectDir)

    reports {
        html.required.set(true)
        sarif.required.set(false)
        md.required.set(false)
        xml.required.set(false)
        txt.required.set(false)
    }

    include("**/*.kt", "**/*.kts")
    exclude("**/resources/**", "**/build/**")

    parallel = true

    buildUponDefaultConfig = true

    allRules = true

    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = catalog.version("jvm-target")
}

dependencies {
    detektPlugins(libs.detekt.ruleset.compiler)
    detektPlugins(libs.detekt.ruleset.ktlint)
    detektPlugins(libs.detekt.ruleset.compose)
    detektPlugins(libs.detekt.ruleset.ivy.explicit)
}
