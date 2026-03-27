buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Force a newer version of JavaPoet in the build classpath to fix Hilt/Room conflicts
        classpath("com.squareup:javapoet:1.13.0")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}