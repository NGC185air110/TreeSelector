plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.dlc.dlctreeselector"
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
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

    publishing {
        singleVariant("release")
    }

}
afterEvaluate {
    publishing {
        val versionName = "1.6.1" // 当前版本依赖库版本号，这个jitpack不会使用到，只是我们开发者自己查看
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                from(components["release"]) // 表示发布 release（jitpack 都不会使用到）
                // You can then customize attributes of the publication as shown below.
                groupId = "com.dlc.dlctreeselector" // 这个是依赖库的组 id
                artifactId = "dlctreeselector" // 依赖库的名称（jitpack 都不会使用到）
                version = versionName
            }
        }
        repositories {
            // 下面这部分，不是很清楚加不加，但是最后加上
            maven {
                val baseUrl = buildDir.parentFile.toString()
                val releasesRepoUrl = "$baseUrl/repos/releases"
                val snapshotsRepoUrl = "$baseUrl/repos/snapshots"
                setUrl(if (versionName.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            }
        }
    }
}


dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    //flexbox
    implementation("com.google.android.flexbox:flexbox:3.0.0")
}