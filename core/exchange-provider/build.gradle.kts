import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Networking
import com.ivy.buildsrc.RoomDB
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    id("dagger.hilt.android.plugin")
}

dependencies {
    Hilt()
    implementation(project(":common:main"))
    implementation(project(":core:data-model"))
    implementation(project(":network"))
    RoomDB(api = true)
    Networking(api = false)

    Testing()
}