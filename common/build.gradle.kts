plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }
}