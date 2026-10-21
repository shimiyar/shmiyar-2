pluginManagement {
  repositories {
    maven("https://maven.myket.ir")
    maven("https://maven.devneeds.ir")
    maven("https://gradle.iranrepo.ir")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/gradle-plugin")
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    maven("https://maven.myket.ir")
    maven("https://maven.devneeds.ir")
    maven("https://gradle.iranrepo.ir")
    maven("https://maven.aliyun.com/repository/google")
    maven("https://maven.aliyun.com/repository/public")
    google()
    mavenCentral()
    maven("https://jitpack.io")
  }
}

rootProject.name = "shimiyar"
include(":app")