import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Lifecycle
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()
    implementation(project(":common"))
    implementation(project(":temp-persistence"))
    implementation(project(":core:persistence"))
    implementation(project(":core:exchange-provider"))
    implementation(project(":sync:public"))
    implementation(project(":app-base")) // TODO: migrate to :resources

    Lifecycle(api = false)

    Testing()
}