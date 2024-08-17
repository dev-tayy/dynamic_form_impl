// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    val roomVersion = "2.6.1"
    id("androidx.room") version roomVersion apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}