import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            implementation(libs.ktor.clientCore)
            implementation(libs.kotlinx.coroutinesCore)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.ktor.clientOkHttp)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.clientCio)
        }
    }
}

android {
    namespace = "com.fatec.at2_base.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
