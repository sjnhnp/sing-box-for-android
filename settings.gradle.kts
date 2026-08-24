pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // For libghostty-android snapshots; remove after release.
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.xposed.info/") }
    }
}
rootProject.name = "sing-box"
include(":app")
include(":libxposed-api")
project(":libxposed-api").projectDir = file("third_party/libxposed-api")
include(":terminal-emulator")
project(":terminal-emulator").projectDir = file("third_party/termux-app/terminal-emulator")
include(":terminal-view")
project(":terminal-view").projectDir = file("third_party/termux-app/terminal-view")
