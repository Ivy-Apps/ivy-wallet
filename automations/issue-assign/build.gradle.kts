plugins {
    id("ivy.script")
    application
}

application {
    mainClass = "ivy.automate.issue.IssueAssignMainKt"
}

dependencies {
    implementation(projects.automations.base)
}