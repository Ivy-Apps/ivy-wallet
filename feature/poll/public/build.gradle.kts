plugins {
  id("ivy.feature")
}

android {
  namespace = "com.ivy.poll"
}

dependencies {
  implementation(projects.shared.domain)
}