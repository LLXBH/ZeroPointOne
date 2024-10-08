plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "llxbh.zeropointone"
    compileSdk = 34

    defaultConfig {
        applicationId = "llxbh.zeropointone"
        minSdk = 24
        targetSdk = 33
        versionCode = 240925001
        versionName = "1.0.52"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 输出类型
    android.applicationVariants.all {
        // 编译类型
        val buildType = this.buildType.name
        outputs.all {
            // 判断是否是输出 apk 类型
            if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                this.outputFileName = "${defaultConfig.versionName}_$buildType.apk"
            }
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
    buildFeatures {
        compose = true
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/LICENSE-LGPL-2.1.txt",
                "META-INF/LICENSE-LGPL-3.txt",
                "META-INF/LICENSE-W3C-TEST",
                "META-INF/DEPENDENCIES"
            )
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Room
    val roomVersion = "2.5.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    // Material Design 3
    val material3Version = "1.0.1"
    implementation("androidx.compose.material3:material3:$material3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$material3Version")
    // BaseRecyclerViewAdapterHelper
    implementation("io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.1.3")
    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
    // SwipeRefreshLayout 下拉刷新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // Flexmark-java 文本解析
    implementation("com.vladsch.flexmark:flexmark-all:0.64.0")
    // Navigation
    val navigationVersion = "2.7.1" // 检查最新版本
    implementation("androidx.navigation:navigation-fragment:$navigationVersion")
    implementation("androidx.navigation:navigation-ui:$navigationVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    // CalendarView 第三方的日控件历库
    implementation("com.haibin:calendarview:3.7.1")
}