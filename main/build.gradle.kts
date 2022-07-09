import com.ivy.buildsrc.EventBus
import com.ivy.buildsrc.ThirdParty

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    implementation(project(":ui-common"))
    implementation(project(":data-model"))
    implementation(project(":temp-domain"))
    implementation(project(":screens"))
    implementation(project(":app-base"))
    implementation(project(":ui-components-old"))


    EventBus()

    implementation(project(":home"))
    implementation(project(":accounts"))
}