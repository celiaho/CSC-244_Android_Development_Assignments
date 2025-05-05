// Top-level build.gradle.kts

plugins {    // These are defined in the version catalog at gradle/libs.versions.toml
    alias(libs.plugins.android.application) apply false // Declare, but don't apply here
    alias(libs.plugins.kotlin.android) apply false    // Declare, but don't apply here
    alias(libs.plugins.kotlin.compose) apply false    // Declare, but don't apply here
}

buildscript {
    dependencies {    // Declare the classpaths for Gradle plugins; versions are defined in gradle/libs.versions.toml
        classpath(libs.plugins.android.application.get().pluginId))    // Ref agp version in gradle/libs.versions.toml
        classpath(libs.plugins.kotlin.android).get().pluginId)
    }
}

allprojects {
    // Repositories are in settings.gradle.kts
}