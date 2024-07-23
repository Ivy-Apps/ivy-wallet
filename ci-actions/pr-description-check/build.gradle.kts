plugins {
    id("ivy.script")
    application
}

application {
    mainClass = "ivy.automate.pr.MainKt"
}

dependencies {
    implementation(projects.ciActions.base)
}
