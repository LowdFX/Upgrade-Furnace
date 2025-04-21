plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0" // ShadowJar ist das gleiche wie shade in Maven.
    id("xyz.jpenilla.run-paper") version "2.2.3" // Um code zu ändern, ohne den server neu zu starten.
}

group = "at.lowdfx"
version = "1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases/")
    maven("https://marcpg.com/repo/")
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven {
        name = "CodeMC"
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("xyz.xenondevs.invui:invui:1.43")
    implementation("com.marcpg:libpg-paper:1.0.1")
    implementation("com.marcpg:libpg-storage-json:1.0.1")
    implementation("com.marcpg:libpg-storage-yaml:1.0.1")
    implementation("de.tr7zw:item-nbt-api:2.14.1")
    implementation(files("lib/craftbukkit-1.21.4-R0.1-SNAPSHOT-remapped-mojang.jar"))
}

tasks {
    build {
        dependsOn(shadowJar) // ShadowJar ist das gleiche wie shade in Maven.
    }
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.21.4")
    }
    shadowJar {
        archiveClassifier.set("") // Kein -all im .jar Namen.
        minimize()
    }
}
