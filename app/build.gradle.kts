plugins {
    id("com.android.application")
}

    val room_version = "2.4.2"
android {
    namespace = "algonquin.cst2335.team_project"
    compileSdk = 33

    defaultConfig {
        applicationId = "algonquin.cst2335.team_project"
        minSdk = 22
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
    // optional - RxJava2 support for Room
    implementation ("androidx.room:room-rxjava2:$room_version")
    // optional - RxJava3 support for Room
    implementation ("androidx.room:room-rxjava3:$room_version")
    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation ("androidx.room:room-guava:$room_version")
    // optional - Test helpers
    testImplementation ("androidx.room:room-testing:$room_version")
    // optional - Paging 3 Integration
    implementation ("androidx.room:room-paging:2.5.0-alpha01")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    //  for Remote
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation ("com.github.akarnokd:rxjava3-retrofit-adapter:3.0.0")
    implementation ("io.reactivex.rxjava3:rxjava:3.0.0")
}