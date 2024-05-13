plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("com.google.protobuf")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")

}

android {
    namespace = "fr.eya.ranging"
    compileSdk = 34

    defaultConfig {

        minSdk = 31
        targetSdk = 33
        

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.20.0"
        }
        plugins {
            generateProtoTasks {
                all().forEach {
                    it.builtins {
                        create("java") {
                            option("lite")
                        }
                    }
                }
            }
        }
    }

}



dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.android.support:support-annotations:28.0.0")
    implementation("androidx.test:core-ktx:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation ("org.robolectric:robolectric:4.2.1")


//nearbyconnection Api
    implementation ("com.google.android.gms:play-services-nearby:18.5.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    implementation ("com.google.protobuf:protobuf-javalite:3.21.5")
    implementation ("com.google.protobuf:protobuf-kotlin-lite:3.21.5")
    implementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
    implementation ("org.mockito:mockito-core:4.7.0")
    implementation ("org.mockito:mockito-inline:4.7.0")

//uwb
    implementation ("androidx.core.uwb:uwb:1.0.0-alpha05")

    implementation ("com.google.android.gms:play-services-tasks:18.0.2")

    implementation ("com.google.truth:truth:1.1.3")
    implementation ("com.google.guava:guava:31.1-jre")
    implementation ("org.robolectric:robolectric:4.8.2")

    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation ("com.airbnb.android:lottie:5.2.0")

}
