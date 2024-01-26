plugins {
    id("ivy.script")
    application
}

application {
    mainClass = "ivy.automate.issue.create.MainKt"
}

dependencies {
    implementation(projects.ciActions.base)
}
