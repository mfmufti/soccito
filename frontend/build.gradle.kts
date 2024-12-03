// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Apply plugins to all sub-projects/modules, but don't enable them by default (apply false)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10" apply false
}

buildscript {
    dependencies {
        // Define dependencies for the build script itself
        // Google Maps Secrets Gradle plugin
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}