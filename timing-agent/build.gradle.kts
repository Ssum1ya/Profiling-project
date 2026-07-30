plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.18.11")
    implementation("net.bytebuddy:byte-buddy-agent:1.18.11")
}

tasks.jar {
    manifest {
        attributes(
            "Premain-Class" to "agent.TimingAgent",
            "Can-Retransform-Classes" to "true"
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
}