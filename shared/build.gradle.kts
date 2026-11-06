import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType


    plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidKmpLibrary)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.kotlinSerialization)
    }


kotlin {
    androidLibrary {
        namespace = "eu.giant.agenda.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        androidResources {
            enable = true
        }
        withHostTest {}

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
            baseName = "ComposeApp"
            isStatic = true
            freeCompilerArgs += "-Xbinary=bundleId=eu.giant.agenda.MinimalAgenda"

        }
    }

    sourceSets {

        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
        }

        named { it.lowercase().startsWith("ios") }.configureEach {
            languageSettings {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlin.time.ExperimentalTime")
            }
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(libs.kotlinx.datetime)
           // implementation(libs.lifecycle.viewmodel)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.navigation.compose)
            api(libs.androidx.datastore)
            api(libs.androidx.datastore.preferences)
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        matching { it.name == "nativeMain" }.configureEach {
            dependencies {
                implementation(libs.cupertino)
                implementation(libs.cupertino.native)
                implementation(libs.cupertino.icons.extended)
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

compose.resources {
    // Keep the pre-split generated package so existing imports stay unchanged.
    packageOfResClass = "minimalagenda.composeapp.generated.resources"
}
