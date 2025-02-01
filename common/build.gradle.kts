plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.12.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release = 17
    }
}