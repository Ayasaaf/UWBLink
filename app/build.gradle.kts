plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("com.google.protobuf")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}


android {
    namespace = "fr.eya.uwblink"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.eya.uwblink"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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
        viewBinding = true
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
    implementation(project(":ranging"))
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    val nav_version = "2.7.7"

    implementation("com.google.accompanist:accompanist-permissions:0.33.1-alpha")

    implementation ("androidx.compose.runtime:runtime-livedata:1.6.7")


    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.room:room-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation ("androidx.compose.material:material:1.6.5")


    implementation("androidx.compose.ui:ui-tooling:1.6.3")
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")


    //Dager implementation

    implementation("com.google.dagger:hilt-android:2.51")
    ksp("com.google.dagger:hilt-compiler:2.51")


//navigation

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

//lottie
    implementation("com.airbnb.android:lottie-compose:5.2.0")
    implementation ("com.airbnb.android:lottie:5.2.0")

    //paging
    implementation("com.google.accompanist:accompanist-pager:0.12.0")

    // Use to implement UWB (ultra-wideband) on supported devices
    implementation ("androidx.core.uwb:uwb:1.0.0-alpha05")


    //porto
    implementation ("androidx.datastore:datastore:1.0.0")
    implementation ("com.google.protobuf:protobuf-javalite:3.21.5")
    implementation ("com.google.protobuf:protobuf-kotlin-lite:3.21.5")


    // LifeCycle
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")


    implementation ("com.google.code.gson:gson:2.8.8")

    //notification

    implementation ("androidx.core:core-ktx:1.6.0")
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
}








