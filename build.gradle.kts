
// Top-level build file where you can add configuration options common to all sub-projects/modules.



buildscript {

    dependencies {
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.51")
        classpath ("com.google.protobuf:protobuf-gradle-plugin:0.9.4")

    }
}


plugins {
    id ("com.android.application") version "8.2.2" apply false
    id ("com.android.library") version "7.4.0" apply false
    id ("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false


}

