rootProject.name = "AccountingApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        mavenLocal()
        maven {
            setUrl("https://maven.dankito.net/api/packages/codinux/maven")
        }
    }
}


// had to extract Sql'delight' dependencies to an extra project as they conflict with Compose dependencies on iOS
include(":AccountingPersistence")
include(":AccountingSqlPersistence")

include(":composeApp")