plugins {
    java
}

group = "com.mpessentials"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
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
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        // Include sqlite-jdbc in the jar (shade/merge)
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
            // Package only the platforms the server runs on; drop the rest (~24MB of native libs)
            exclude("org/sqlite/native/Mac/**")
            exclude("org/sqlite/native/Linux-Android/**")
            exclude("org/sqlite/native/Linux-Musl/**")
            exclude("org/sqlite/native/FreeBSD/**")
            exclude("org/sqlite/native/Linux/x86/**")
            exclude("org/sqlite/native/Linux/aarch64/**")
            exclude("org/sqlite/native/Linux/arm/**")
            exclude("org/sqlite/native/Linux/armv6/**")
            exclude("org/sqlite/native/Linux/armv7/**")
            exclude("org/sqlite/native/Linux/ppc64/**")
            exclude("org/sqlite/native/Windows/x86/**")
            exclude("org/sqlite/native/Windows/aarch64/**")
            exclude("org/sqlite/native/Windows/armv7/**")

            exclude("META-INF/*.SF")
            exclude("META-INF/*.DSA")
            exclude("META-INF/*.RSA")
        }
    }
}
