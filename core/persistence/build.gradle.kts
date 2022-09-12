import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.RealmDb
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
    id("io.realm.kotlin")
}

dependencies {
    Hilt()
    implementation(project(":common"))
    RealmDb()

    Testing()
}