plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget.balance"
}

dependencies {
    implementation(projects.ivyCore)
    implementation(projects.ivyResources)
    implementation(projects.ivyDesign)
    implementation(projects.ivyWidgetBase)
}