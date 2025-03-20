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
    implementation("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")

    implementation("fun.jaobabus:commandlib:0.2.1-SNAPSHOT")

    // Дополнительные зависимости
    implementation("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    implementation("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
    implementation("org.yaml:snakeyaml:2.4")


    implementation("com.google.guava:guava:32.1.2-jre")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

}

tasks.jar {
    archiveClassifier.set("plugin") // plugin.jar
    dependsOn(tasks.shadowJar)
}

var mainClass = "fun.jaobabus.stafftolls.main.Main";

// Шейдинг CommandLib внутрь плагина
tasks.shadowJar {
    archiveClassifier.set("full")
    configurations = listOf(project.configurations.runtimeClasspath.get())

    dependencies {
        include(dependency("fun.jaobabus:commandlib:0.2.1-SNAPSHOT"))
        include(dependency("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT"))
        include(dependency("com.sk89q.worldguard:worldguard-bukkit:7.0.9"))
        include(dependency("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT"))
        include(dependency("com.google.guava:guava:32.1.2-jre"))
        include(dependency("org.yaml:snakeyaml:2.4"))
    }

    from(project.configurations.runtimeClasspath.get().filter {
        it.name.contains("guava")
    })

    // relocate("org.bukkit", "fun.jaobabus.libs.bukkit")
    // relocate("com.google.common", "fun.jaobabus.libs.guava")

    manifest {
        attributes(
            "Main-Class" to mainClass
        )
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
