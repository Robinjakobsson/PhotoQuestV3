
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.photoquestv3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.photoquestv3"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

        implementation(libs.androidx.lifecycle.livedata.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.glide)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.firebase.common.ktx)
        implementation(libs.firebase.firestore.ktx)
        implementation(libs.firebase.auth.ktx)
        implementation(libs.firebase.firestore)
        implementation(libs.firebase.storage)
        implementation(libs.firebase.auth)
        implementation(libs.play.services.auth)
        implementation(libs.androidx.credentials)
        implementation(libs.androidx.credentials.play.services.auth)
        implementation(libs.googleid)
        implementation(libs.androidx.work.runtime.ktx)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        implementation(libs.facebook.login.vlatestrelease)

    //  Facebook Shimmer
        implementation(libs.shimmer)

    //  Lottie
        implementation(libs.lottie)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)



    implementation(libs.androidx.datastore.preferences)


        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.coroutines.android)
  


}