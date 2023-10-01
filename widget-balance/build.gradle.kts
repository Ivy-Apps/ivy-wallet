plugins {
    id("ivy.widget")
}

android {
    namespace = "com.ivy.widget.balance"
}

dependencies {
    implementation(projects.ivyBase)
    implementation(projects.ivyDomain)
    implementation(projects.ivyResources)
    implementation(projects.tempOldDesign)
    implementation(projects.ivyWidgetBase)
    implementation(projects.tempLegacyCode)
}