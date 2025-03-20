plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.1"
}

group = "fun.jaobabus"
version = "1.2-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    mavenLocal() // Используем локальный Maven-репозиторий
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds") // Отключаем кеширование версий
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT") // Спигот API

    implementation("fun.jaobabus:commandlib:0.2.1-SNAPSHOT")

    // Дополнительные зависимости
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

}

// Шейдинг CommandLib внутрь плагина
tasks.shadowJar {
    archiveClassifier.set("")
    configurations = listOf(project.configurations.runtimeClasspath.get())
    dependencies {
        include(dependency("fun.jaobabus:commandlib:0.2.1-SNAPSHOT"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "fun.jaobabus"
            artifactId = "stafftools"
            version = "1.2-SNAPSHOT"
            from(components["java"])
        }
    }
}
