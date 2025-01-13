import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl


plugins {
    alias(libs.plugins.kotlinMultiplatform)

    alias(libs.plugins.androidLibrary)

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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            testTask {
                useKarma {
//                    useChromeHeadless()
                    useFirefoxHeadless()
                }
            }
        }
    }


    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "AccountingPersistence"
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()



    sourceSets {
        val commonMain by getting {
            dependencies {
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
        jvmTest {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
    }
}


android {
    namespace = "net.codinux.accounting.persistence"
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