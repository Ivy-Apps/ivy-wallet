plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget.transaction"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.ivyWidgetBase)
}