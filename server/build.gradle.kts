plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
    kotlin("plugin.serialization") version "2.3.0"
}

group = "com.fatec.at2_base"
version = "1.0.0"
application {
    mainClass.set("com.fatec.at2_base.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.serialization.kotlinxJson)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}
