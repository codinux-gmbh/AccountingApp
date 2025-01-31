import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.desktop.tasks.AbstractJarsFlattenTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        // suppresses compiler warning: [EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING] 'expect'/'actual' classes (including interfaces, objects, annotations, enums, and 'actual' typealiases) are in Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }


    jvm("desktop")

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "AccountingFramework"
            isStatic = false
        }

        // don't know why but this has to be added here, adding it in BankingPersistence.build.gradle.kt does not work
        iosTarget.binaries.forEach { binary ->
            if (binary is org.jetbrains.kotlin.gradle.plugin.mpp.Framework) {
                binary.linkerOpts.add("-lsqlite3") // without this we get a lot of "Undefined symbol _co_touchlab_sqliter..." errors in Xcode
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "Accounting"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "Accounting.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    applyDefaultHierarchyTemplate()

    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":AccountingPersistence"))

            implementation(libs.invoicing)

            implementation(libs.kI18n)

            implementation(libs.kmpBase)

            implementation(libs.kotlinxSerializationJson)

            implementation(libs.jackson.kotlin)
            implementation(libs.jackson.datetime)

            implementation(libs.klf)

            // UI
            implementation(libs.filekit)
            implementation(libs.calendarCompose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }



        val nonWebMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation(project(":AccountingSqlPersistence"))
            }
        }
        val nonWebTest by creating {
            dependsOn(commonTest.get())
        }

        val javaCommonMain by creating {
            dependsOn(nonWebMain)

            dependencies {
                implementation(libs.epcQrCode)
            }
        }
        val javaCommonTest by creating {
            dependsOn(nonWebTest)
        }

        val desktopMain by getting {
            dependsOn(javaCommonMain)

            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        val desktopTest by getting {
            dependsOn(javaCommonTest)

            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
        
        androidMain {
            dependsOn(javaCommonMain)

            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
            }
        }
        val androidUnitTest by getting {
            dependsOn(javaCommonTest)
        }


        iosMain {
            dependsOn(nonWebMain)

            dependencies {
                implementation(libs.epcQrCode)
            }
        }
        iosTest {
            dependsOn(nonWebTest)
        }

    }
}


compose.resources {
    packageOfResClass = "net.codinux.accounting.resources"
}


compose.desktop {
    application {
        mainClass = "net.codinux.accounting.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "net.codinux.accounting"
            packageVersion = "1.0.0"

            linux {
                modules("jdk.security.auth") // required by FileKit
            }
        }

        // disable Proguard, it causes that Uber Jar generation fails
        buildTypes.release.proguard {
            isEnabled = false
            version = "7.4.2"
        }
    }
}

android {
    namespace = "net.codinux.accounting"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.codinux.invoicing.einvoice"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 5
        versionName = "$version"
    }
    packaging {
        resources {
            pickFirsts += "META-INF/LICENSE.md"
            pickFirsts += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "META-INF/NOTICE.md"
            pickFirsts += "META-INF/DEPENDENCIES"
            pickFirsts += "META-INF/buildinfo.xml"
            pickFirsts += "META-INF/sun-jaxb.episode"
            pickFirsts += "translation/translatable-texts.xml"
        }
    }

    buildTypes {
        named("debug") {
            applicationIdSuffix = ".develop"
        }

        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        coreLibraryDesugaring(libs.androidDesuger)

        debugImplementation(compose.uiTooling)
    }

    configurations {
        implementation {
            // remove dependency on PDFBox, which was included by codinx:invoicing -> mustang, as it
            // makes use of java.awt and therefore doesn't run on Android
            exclude(group = "org.apache.pdfbox", module = "pdfbox")
        }
    }

    signingConfigs {
        named("debug") {
            // so that all machines have the same signing key, no matter if app is installed from developer's machine or from Jenkins
            storeFile = file("src/androidMain/debug-keystore.jks")
            storePassword = "find_my_bugs_before_releasing_me"
            keyAlias = "DebugKey"
            keyPassword = "find_my_bugs_before_releasing_me"
        }
    }
}


gradle.taskGraph.whenReady {
    tasks.named<AbstractJarsFlattenTask>("flattenJars") {
        removeThirdPartySignaturesFromJar()
    }

    tasks.named<AbstractJarsFlattenTask>("flattenReleaseJars") {
        removeThirdPartySignaturesFromJar()
    }
}

// Signatures of third party libraries get copied to output jar's META-INF folder so that java -jar refuses to run created uber jar:
// Error: A JNI error has occurred, please check your installation and try again
// Exception in thread "main" java.lang.SecurityException: Invalid signature file digest for Manifest main attributes
//        at java.base/sun.security.util.SignatureFileVerifier.processImpl(SignatureFileVerifier.java:340)
//        at java.base/sun.security.util.SignatureFileVerifier.process(SignatureFileVerifier.java:282)
//        at java.base/java.util.jar.JarVerifier.processEntry(JarVerifier.java:276)
//
// -> remove signatures of third party libraries from jar's META-INF folder
fun AbstractJarsFlattenTask.removeThirdPartySignaturesFromJar() {
    val outputJar = (this.flattenedJar as? FileSystemLocationProperty<*>)?.asFile?.get()

    doLast {
        if (outputJar != null && outputJar.exists()) {
            val extractedFilesFolder = File(outputJar.parentFile, "extracted").also { it.mkdirs() }
            extractedFilesFolder.deleteRecursively()

            project.copy { // unzip jar file
                from(project.zipTree(outputJar))
                into(extractedFilesFolder)
            }

            // Remove unwanted META-INF files (*.SF, *.DSA, *.RSA)
            project.fileTree(extractedFilesFolder.resolve("META-INF")).matching {
                include("*.SF", "*.DSA", "*.RSA")
            }.forEach {
                it.delete() // Delete the matching signature files
            }

            outputJar.delete() // Remove the original JAR

            // Zip the modified content back into a new JAR using Ant
            ant.withGroovyBuilder {
                "zip"(
                    "destfile" to outputJar,
                    "basedir" to extractedFilesFolder
                )
            }

            // Clean up the temporary directory
            extractedFilesFolder.deleteRecursively()
        }
    }
}
