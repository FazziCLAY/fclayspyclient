plugins {
    id("application")
    id("java")
}

group = "com.fazziclay.fclaypersonstatusclient"
version = "1.3"

repositories {
    mavenCentral()
}



dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation(project(":apiclient"))
    implementation(project(":api"))

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    compileOnly("org.jetbrains:annotations:25.0.0")


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}


tasks.jar {
    manifest {
        attributes("Main-Class" to "com.fazziclay.fclaypersonstatusclient.Main")
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}



java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.test {
    useJUnitPlatform()
}