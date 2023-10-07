plugins {
    id("ivy.script")
    application
}

application {
    mainClass = "ivy.automate.issue.MainKt"
}

dependencies {
    implementation(projects.automations.base)
}