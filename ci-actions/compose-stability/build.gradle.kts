plugins {
    id("ivy.script")
    application
}

application {
    mainClass = "ivy.automate.compose.stability.MainKt"
}

dependencies {
    implementation(projects.ciActions.base)
}
