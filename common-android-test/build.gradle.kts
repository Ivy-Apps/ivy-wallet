import com.ivy.buildsrc.AndroidXTest
import com.ivy.buildsrc.Hilt
import com.ivy.buildsrc.Testing

apply<com.ivy.buildsrc.IvyPlugin>()

plugins {
    `android-library`
    `kotlin-android`
}

dependencies {
    Hilt()

    Testing(commonTest = false)
    AndroidXTest(dependency = { dep ->
        api(dep)
    })
}