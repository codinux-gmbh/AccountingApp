import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi


plugins {
    alias(libs.plugins.kotlinMultiplatform)

    alias(libs.plugins.androidLibrary)

    alias(libs.plugins.sqldelight)

    alias(libs.plugins.kotlinxSerialization)
}


kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // suppresses compiler warning: [EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING] 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }


    jvmToolchain(11)

    jvm()

    androidTarget {
//        @OptIn(ExperimentalKotlinGradlePluginApi::class)
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_11)
//        }
    }


    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "AccountingSqlPersistence"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()



    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":AccountingPersistence"))

                api(libs.invoicing)

                api(libs.kmpBase)

                implementation(libs.kotlinxSerializationJson)

                api(libs.klf)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)

                implementation(libs.coroutines.test)

                implementation(libs.assertk)
            }
        }


        val nonWebMain by creating {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.sqldelight.paging.extensions)

                implementation(libs.klf)
            }
        }
        val nonWebTest by creating {
            dependsOn(commonTest)
        }

        jvmMain {
            dependsOn(nonWebMain)

            dependencies {
                implementation(libs.appDirs)

                implementation(libs.sqldelight.sqlite.driver)
            }
        }
        jvmTest {
            dependsOn(nonWebTest)

            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }

        androidMain {
            dependsOn(nonWebMain)

            dependencies {
                implementation(libs.sqldelight.android.driver)
            }
        }
        val androidUnitTest by getting {
            dependsOn(nonWebTest)
        }

        iosMain {
            dependsOn(nonWebMain)

            dependencies {
                implementation(libs.sqldelight.native.driver)
            }
        }
        iosTest {
            dependsOn(nonWebTest)
        }
    }
}


sqldelight {
    databases {
        create("AccountingDb") {
            packageName.set("net.codinux.accounting.persistence")
            generateAsync = true

            schemaOutputDirectory = file("src/nonWebMain/sqldelight/databases")
        }
    }
}


android {
    namespace = "net.codinux.accounting.persistence.sql"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
dependencies {
    testImplementation(project(":AccountingPersistence"))
}
