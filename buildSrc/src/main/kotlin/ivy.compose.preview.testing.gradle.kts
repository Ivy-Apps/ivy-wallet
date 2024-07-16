plugins {
    id("ivy.compose")
    /*
    Will refactor code for import plugin for compose preview screenshot testing
    when compose.screenshot has a stable version -
    remove android.experimental.enableScreenshotTest
     */
//    id("com.android.compose.screenshot")
}

android {
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}
