plugins {
    java
    `maven-publish`
}

group = "com.mpessentials"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to project.version,
                "name" to project.name
            )
        }
    }

    jar {
        archiveBaseName.set("MPEssentials")
        
        // Include sqlite-jdbc in the jar (shade/merge)
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
            exclude("META-INF/*.SF")
            exclude("META-INF/*.DSA")
            exclude("META-INF/*.RSA")
        }
    }
}
